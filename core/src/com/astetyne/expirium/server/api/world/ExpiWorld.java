package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.WeatherType;
import com.astetyne.expirium.main.world.WorldLoadingException;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.Saveable;
import com.astetyne.expirium.server.api.entity.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entity.ExpiEntity;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.world.event.Source;
import com.astetyne.expirium.server.api.world.event.TileChangeEvent;
import com.astetyne.expirium.server.api.world.listeners.CampfireListener;
import com.astetyne.expirium.server.api.world.listeners.TreeListener;
import com.astetyne.expirium.server.backend.FixRes;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ExpiWorld implements Saveable {

    private ExpiTile[][] worldTerrain;
    private int partHeight;
    private World b2dWorld;
    private Body terrainBody;
    private StabilityCalculator stabilityCalc;
    private FixtureCalculator fixtureCalc;
    private WeatherType weatherType;
    private InteractHandler interactHandler;
    private ExpiContactListener contactListener;
    private final WorldFileManager fileManager;
    private float stepsTimeAccumulator;
    private int worldTime;
    private WorldSettings settings;

    public ExpiWorld(WorldSettings settings, boolean createNew) {

        fileManager = new WorldFileManager(this);

        if(!createNew) {
            try {
                fileManager.loadWorld(settings.name);
            }catch(WorldLoadingException e) {
                e.printStackTrace();
            }
            return;
        }

        this.settings = settings;

        worldTerrain = new ExpiTile[settings.height][settings.width];
        new WorldGenerator(worldTerrain).generateWorld();

        worldTime = 0;
        weatherType = WeatherType.SUNNY;

        initAfterCreation();

        try {
            fileManager.saveWorld(settings.name);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void initAfterCreation() {

        stepsTimeAccumulator = 0;

        interactHandler = new InteractHandler(this);

        new CampfireListener();
        new TreeListener();

        partHeight = 2; // todo: this should be calculated from BUFFER_SIZE and terrainWidth

        b2dWorld = new World(new Vector2(0, -9.81f), false);

        contactListener = new ExpiContactListener();
        b2dWorld.setContactListener(contactListener);

        FixRes.load();
        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = b2dWorld.createBody(terrainDef);

        stabilityCalc = new StabilityCalculator(worldTerrain);
        fixtureCalc = new FixtureCalculator(this, terrainBody);

        System.out.println("Creating fixtures...");
        fixtureCalc.generateWorldFixtures();
        System.out.println("Recalculating stability...");
        stabilityCalc.generateStability();
        System.out.println("Generating world done!");

    }

    public void onTick() {

        worldTime++;
        if(worldTime == Consts.SERVER_DEFAULT_TPS * Consts.DAY_TIME_SEC) {
            worldTime = 0;
        }

        for(ExpiPlayer pp : GameServer.get().getPlayers()) {
            pp.applyPhysics();
        }

        stepsTimeAccumulator += 1f / Consts.SERVER_DEFAULT_TPS;
        while(stepsTimeAccumulator >= 1/60f) {
            b2dWorld.step(1/60f, 6, 2);
            stepsTimeAccumulator -= 1/60f;
        }
        //b2dWorld.step(1f/Consts.SERVER_DEFAULT_TPS, 6, 2);

        for(ExpiEntity ee : GameServer.get().getEntities()) {
            for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                pp.getNetManager().putEntityMovePacket(ee);
            }
        }
    }

    public void onTilePlaceReq(ExpiTile t, Item item, ExpiPlayer p) {

        if(!isTileFree(t, item)) return;

        if(!stabilityCalc.canBeAdjusted(t, item.getBuildTile())) return;

        // confirmed from here
        p.getInv().removeItem(new ItemStack(item, 1));
        p.getNetManager().putInvFeedPacket();

        changeTile(t, item.getBuildTile(), true, p, Source.PLAYER);
    }

    public void changeTile(ExpiTile t, TileType to, boolean withDrops, ExpiPlayer p, Source source) {

        TileType from = t.getType();

        Vector2 tempVec = new Vector2();

        float off = (1 - Consts.D_I_SIZE)/2;

        if(to == TileType.AIR && withDrops && t.getType() != TileType.AIR) {
            tempVec.set(t.getX() + off, t.getY() + off);
            ExpiDroppedItem droppedItem = new ExpiDroppedItem(tempVec, t.getType().getDropItem(), Consts.ITEM_COOLDOWN_BREAK);
            for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                pp.getNetManager().putEntitySpawnPacket(droppedItem);
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
                    ExpiDroppedItem edi = new ExpiDroppedItem(tempVec, t2.getType().getDropItem(), Consts.ITEM_COOLDOWN_BREAK);
                    for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                        pp.getNetManager().putEntitySpawnPacket(edi);
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
                pp.getNetManager().putTileChangePacket(t2);
            }
            pp.getNetManager().putStabilityPacket(affectedTiles);
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
        while(i != settings.height && worldTerrain[i][10].getType() != TileType.AIR) {
            i++;
        }
        return new Vector2(10, i+2);
    }

    public ExpiTile getTileAt(float x, float y) {
        return worldTerrain[(int)y][(int)x];
    }

    public ExpiTile getTileAt(Vector2 vec) {
        return worldTerrain[(int)vec.y][(int)vec.x];
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
        return settings.width;
    }

    public int getTerrainHeight() {
        return settings.height;
    }

    public int getPartHeight() {
        return partHeight;
    }

    @Override
    public void readData(DataInputStream in) throws IOException {

        settings = new WorldSettings();
        settings.readData(in);

        worldTerrain = new ExpiTile[settings.height][settings.width];

        for(int h = 0; h < settings.height; h++) {
            for(int w = 0; w < settings.width; w++) {
                worldTerrain[h][w] = new ExpiTile(TileType.getType(in.readByte()), w, h);
            }
        }

        //todo: len docasne zatial
        worldTime = 0;
        weatherType = WeatherType.SUNNY;

        initAfterCreation();

    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

        settings.writeData(out);

        for(int h = 0; h < settings.height; h++) {
            for(int w = 0; w < settings.width; w++) {
                out.writeByte(worldTerrain[h][w].getType().getID());
            }
        }

    }
}
