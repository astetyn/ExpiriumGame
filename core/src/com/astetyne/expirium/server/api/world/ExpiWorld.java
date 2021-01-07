package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.WeatherType;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.api.world.event.ServerTickEvent;
import com.astetyne.expirium.server.api.world.event.Source;
import com.astetyne.expirium.server.api.world.event.TileChangeEvent;
import com.astetyne.expirium.server.api.world.listeners.CampfireListener;
import com.astetyne.expirium.server.backend.FixRes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ExpiWorld {

    private final ExpiTile[][] worldTerrain;
    private final int terrainWidth, terrainHeight;
    private final int partHeight;
    private final String worldName;
    private final long seed;
    private final World b2dWorld;
    private final Body terrainBody;
    private final WorldGenerator worldGenerator;
    private final StabilityCalculator stabilityCalc;
    private final FixtureCalculator fixtureCalc;
    private WeatherType weatherType;
    private final InteractHandler interactHandler;
    private final ExpiContactListener contactListener;
    private float stepsTimeAccumulator;
    private int worldTime;

    public ExpiWorld(String worldName) {
        this(worldName, (long)(Math.random()*10000));
    }

    public ExpiWorld(String worldName, long seed) {

        this.worldName = worldName;
        this.seed = seed;

        worldTime = 0;

        weatherType = WeatherType.SUNNY;

        stepsTimeAccumulator = 0;

        interactHandler = new InteractHandler(this);

        CampfireListener list = new CampfireListener();


        terrainWidth = 320;
        terrainHeight = 320;

        partHeight = 2; // todo: this should be calculated from BUFFER_SIZE and terrainWidth

        worldTerrain = new ExpiTile[terrainHeight][terrainWidth];

        b2dWorld = new World(new Vector2(0, -9.81f), false);

        contactListener = new ExpiContactListener();
        b2dWorld.setContactListener(contactListener);

        FixRes.load();
        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = b2dWorld.createBody(terrainDef);

        worldGenerator = new WorldGenerator(worldTerrain);
        stabilityCalc = new StabilityCalculator(worldTerrain);
        fixtureCalc = new FixtureCalculator(this, terrainBody);

        FileHandle file = Gdx.files.local("world/"+worldName+".txt");

        if(!file.exists()) {
            createWorld();
        }else {
            //todo: nacitat svet zo suboru
        }
    }

    public void onTick() {

        worldTime++;
        if(worldTime == Consts.SERVER_DEFAULT_TPS * Consts.DAY_TIME_SEC) {
            worldTime = 0;
        }

        stepsTimeAccumulator += 1f / Consts.SERVER_DEFAULT_TPS;

        while(stepsTimeAccumulator >= 1/60f) {
            b2dWorld.step(1/60f, 6, 2);
            stepsTimeAccumulator -= 1/60f;
        }

        recalculateDroppedItems();

        for(ExpiPlayer p : GameServer.get().getPlayers()) {
            p.move();
        }

        for(ExpiEntity ee : GameServer.get().getEntities()) {
            for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                pp.getGateway().getManager().putEntityMovePacket(ee);
            }
        }

        ServerTickEvent.onTick();

    }

    private void recalculateDroppedItems() {

        Iterator<ExpiDroppedItem> it = GameServer.get().getDroppedItems().iterator();
        outer:
        while(it.hasNext()) {
            ExpiDroppedItem droppedItem = it.next();
            if(droppedItem.getCooldown() != 0) {
                droppedItem.reduceCooldown();
                continue;
            }
            Vector2 center = droppedItem.getCenter();
            for(ExpiPlayer p : GameServer.get().getPlayers()) {
                Vector2 dif = p.getCenter().sub(center);
                if(dif.len() < Consts.D_I_PICK_DIST && p.getInv().canBeAdded(droppedItem.getItem(), 1)) {
                    it.remove();

                    p.getInv().addItem(new ItemStack(droppedItem.getItem(), 1), true);
                    p.getGateway().getManager().putInvFeedPacket(p);

                    for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                        pp.getGateway().getManager().putEntityDespawnPacket(droppedItem);
                    }
                    droppedItem.destroySafe();
                    continue outer;
                }
            }
            int remainingTicks = droppedItem.getTicksToDespawn();
            if(remainingTicks == 0) {
                for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                    pp.getGateway().getManager().putEntityDespawnPacket(droppedItem);
                }
                it.remove();
                droppedItem.destroySafe();
                continue;
            }
            droppedItem.setTicksToDespawn(remainingTicks-1);
        }

    }

    public void createWorld() {
        System.out.println("Generating world...");
        worldGenerator.generateWorld();
        System.out.println("Creating fixtures...");
        fixtureCalc.generateWorldFixtures();
        System.out.println("Recalculating stability...");
        stabilityCalc.generateStability();
        System.out.println("Generating world done!");
    }

    public void onTilePlaceReq(ExpiTile t, Item item, ExpiPlayer p) {

        if(!isTileFree(t, item)) return;

        if(!stabilityCalc.canBeAdjusted(t, item.getBuildTile())) return;

        // confirmed from here
        p.getInv().removeItem(new ItemStack(item, 1));
        p.getGateway().getManager().putInvFeedPacket(p);

        changeTile(t, item.getBuildTile(), true, p, Source.PLAYER);
    }

    public void changeTile(ExpiTile t, TileType to, boolean withDrops, ExpiPlayer p, Source source) {

        TileType from = t.getType();

        Vector2 tempVec = new Vector2();

        float off = (1 - Consts.D_I_SIZE)/2;

        if(to == TileType.AIR && withDrops && t.getType() != TileType.AIR) {
            tempVec.set(t.getX() + off, t.getY() + off);
            ExpiDroppedItem droppedItem = new ExpiDroppedItem(tempVec, t.getType().getDropItem(), Consts.SERVER_DEFAULT_TPS);
            for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                pp.getGateway().getManager().putEntitySpawnPacket(droppedItem);
            }
        }

        t.setType(to);
        fixtureCalc.recalcTileFixturesPlus(t);

        List<ExpiTile> changedTiles = new ArrayList<>();
        changedTiles.add(t);

        HashSet<ExpiTile> affectedTiles = stabilityCalc.adjustStability(t);
        Iterator<ExpiTile> it = affectedTiles.iterator();
        while(it.hasNext()) {
            ExpiTile t2 = it.next();
            if(t2.getStability() == 0) {
                if(Math.random() < 1 && withDrops && t2.getType().getDropItem() != null) {
                    tempVec.set(t2.getX()+off, t2.getY()+off);
                    ExpiDroppedItem edi = new ExpiDroppedItem(tempVec, t2.getType().getDropItem(), Consts.SERVER_DEFAULT_TPS);
                    for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                        pp.getGateway().getManager().putEntitySpawnPacket(edi);
                    }
                }
                t2.setType(TileType.AIR);
                fixtureCalc.clearTileFixtures(t2);
                changedTiles.add(t2);
                it.remove();
            }
        }

        for(ExpiPlayer pp : GameServer.get().getPlayers()) {
            for(ExpiTile t2 : changedTiles) {
                pp.getGateway().getManager().putTileChangePacket(t2);
            }
            pp.getGateway().getManager().putStabilityPacket(affectedTiles);
        }

        new TileChangeEvent(t, from, p, source);
    }

    private boolean isTileFree(ExpiTile t, Item toPlace) {

        int x = t.getX();
        int y = t.getY();

        if(toPlace.getBuildTile().getSolidity().isSoft()) return true;

        for(ExpiEntity p : GameServer.get().getEntities()) {

            float px = p.getLocation().x;
            float py = p.getLocation().y;
            float pxe = px + p.getWidth();
            float pye = py + p.getHeight();

            if(((px > x && px < x+1) || (px < x && x < pxe) || (pxe > x && pxe < x+1)) && //check if x collide
                    ((py > y && py < y+1) || (py < y && y < pye) || (pye > y && pye < y+1))) { //check if y collide
                return false;
            }
        }
        return true;
    }

    public Vector2 getSaveLocationForSpawn() {
        int i = 0;
        while(i != terrainHeight && worldTerrain[i][10].getType() != TileType.AIR) {
            i++;
        }
        return new Vector2(10, i+2);
    }

    public void saveWorld() {

    }

    public ExpiTile getTileAt(float x, float y) {
        return worldTerrain[(int)y][(int)x];
    }

    public ExpiTile getTileAt(Vector2 vec) {
        return worldTerrain[(int)vec.y][(int)vec.x];
    }

    public String getWorldName() {
        return worldName;
    }

    public long getSeed() {
        return seed;
    }

    public Body getTerrainBody() {
        return terrainBody;
    }

    public World getB2dWorld() {
        return b2dWorld;
    }

    public WeatherType getWeather() {
        return weatherType;
    }

    public InteractHandler getInteractHandler() {
        return interactHandler;
    }

    public ExpiContactListener getCL() {
        return contactListener;
    }

    public int getWorldTime() {
        return worldTime;
    }

    public ExpiTile[][] getTerrain() {
        return worldTerrain;
    }

    public int getTerrainWidth() {
        return terrainWidth;
    }

    public int getTerrainHeight() {
        return terrainHeight;
    }

    public int getPartHeight() {
        return partHeight;
    }
}
