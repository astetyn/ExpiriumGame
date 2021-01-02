package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.world.WeatherType;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.api.world.listeners.CampfireListener;
import com.astetyne.expirium.server.backend.FixRes;
import com.astetyne.expirium.server.backend.FixturePack;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

import java.util.*;

public class ExpiWorld {

    private final ExpiTile[][] worldTerrain;
    private final String worldName;
    private final long seed;
    private final World box2dWorld;
    private final Body terrainBody;
    private final int w, h;
    private final WorldGenerator worldGenerator;
    private final StabilityCalculator stabilityCalc;
    private final FixtureCalculator fixtureCalc;
    private final List<TileListener> tileListeners;
    private WeatherType weatherType;

    public ExpiWorld(String worldName) {
        this(worldName, (long)(Math.random()*10000));
    }

    public ExpiWorld(String worldName, long seed) {

        this.worldName = worldName;
        this.seed = seed;

        weatherType = WeatherType.SUNNY;

        tileListeners = new ArrayList<>();

        CampfireListener list = new CampfireListener();
        registerTileListener(list);

        w = Constants.T_W_CH * Constants.CHUNKS_NUMBER;
        h = Constants.T_H_CH;

        worldTerrain = new ExpiTile[h][w];

        box2dWorld = new World(new Vector2(0, -9.81f), false);
        FixRes.load();
        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = box2dWorld.createBody(terrainDef);

        worldGenerator = new WorldGenerator(worldTerrain);
        stabilityCalc = new StabilityCalculator(worldTerrain);
        fixtureCalc = new FixtureCalculator(worldTerrain, terrainBody);

        FileHandle file = Gdx.files.local("world/"+worldName+".txt");

        if(!file.exists()) {
            createWorld();
        }else {
            //todo: nacitat svet zo suboru
        }
    }

    public void onTick() {

        for(int i = 0; i < 60.0/Constants.SERVER_DEFAULT_TPS; i++) {
            box2dWorld.step(1 / 60f, 6, 2);
        }

        checkChunks();

        recalculateDroppedItems();

        for(ExpiEntity ee : GameServer.get().getEntities()) {
            for(ExpiPlayer p : GameServer.get().getPlayers()) {
                if(p == ee) continue;
                p.getGateway().getManager().putEntityMovePacket(ee);
            }
        }

        for(TileListener l : tileListeners) {
            l.onTick();
        }

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
                if(dif.len() < Constants.D_I_PICK_DIST && p.getInv().canBeAdded(droppedItem.getItem())) {
                    it.remove();

                    p.getInv().addItem(new ItemStack(droppedItem.getItem(), 1));
                    p.getGateway().getManager().putInvFeedPacket(p.getInv());

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

    private void checkChunks() {

        for(ExpiPlayer p : GameServer.get().getPlayers()) {

            int renderDistance = 1;
            int currentChunk = (int) (p.getLocation().x / Constants.T_W_CH);

            for(int i = 0; i < Constants.CHUNKS_NUMBER; i++) {
                if(i >= currentChunk - renderDistance && i <= currentChunk + renderDistance) {
                    if(!p.getActiveChunks().contains(i)) {
                        p.getGateway().getManager().putChunkFeedPacket(worldTerrain, i);
                        p.getActiveChunks().add(i);
                    }
                }else if(p.getActiveChunks().contains(i)) {
                    p.getGateway().getManager().putChunkDestroyPacket(worldTerrain, i);
                    p.getActiveChunks().remove(i);
                }
            }
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

    public void onTileBreakReq(int c, int x, int y, ExpiPlayer p) {

        ExpiTile tile = worldTerrain[y][c*Constants.T_W_CH + x];
        if(tile.getType() == TileType.AIR) return;

        // confirmed from here
        for(TileListener l : tileListeners) {
            l.onTilePreBreak(tile);
        }

        changeTile(tile, TileType.AIR, true);
    }

    public void onTilePlaceReq(int c, int x, int y, Item item, ExpiPlayer p) {

        if(!p.getInv().contain(item)) return;

        ExpiTile t = worldTerrain[y][c*Constants.T_W_CH + x];
        if(t.getType() != TileType.AIR) return;

        if(!isTileFree(x, y)) return;

        if(!stabilityCalc.canBeAdjusted(t, item.getBuildTile())) return;

        // confirmed from here
        p.getInv().removeItem(new ItemStack(item, 1));
        p.getGateway().getManager().putInvFeedPacket(p.getInv());

        changeTile(t, item.getBuildTile(), true);

        for(TileListener l : tileListeners) {
            l.onTilePlace(t);
        }
    }

    public void onTileInteract(int c, int x, int y, ExpiPlayer p) {
        for(TileListener l : tileListeners) {
            l.onTileInteract(worldTerrain[y][c*Constants.T_W_CH + x]);
        }
    }

    public void changeTile(ExpiTile t, TileType to, boolean withDrops) {

        Vector2 tempVec = new Vector2();

        float off = (1 - Constants.D_I_SIZE)/2;

        if(to == TileType.AIR && withDrops && t.getType() != TileType.AIR) {
            tempVec.set(t.getX() + off, t.getY() + off);
            ExpiDroppedItem droppedItem = new ExpiDroppedItem(tempVec, t.getType().getDropItem(), Constants.SERVER_DEFAULT_TPS);
            for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                pp.getGateway().getManager().putEntitySpawnPacket(droppedItem);
            }
        }

        t.setType(to);
        FixturePack fp = new FixturePack();
        fixtureCalc.recalcTileFixturesPlus(t, fp);

        List<ExpiTile> changedTiles = new ArrayList<>();
        changedTiles.add(t);

        HashSet<ExpiTile> affectedTiles = stabilityCalc.adjustStability(t);
        Iterator<ExpiTile> it = affectedTiles.iterator();
        while(it.hasNext()) {
            ExpiTile t2 = it.next();
            if(t2.getStability() == 0) {
                if(Math.random() < 1 && withDrops && t2.getType().getDropItem() != null) {
                    tempVec.set(t2.getX()+off, t2.getY()+off);
                    ExpiDroppedItem edi = new ExpiDroppedItem(tempVec, t2.getType().getDropItem(), Constants.SERVER_DEFAULT_TPS);
                    for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                        pp.getGateway().getManager().putEntitySpawnPacket(edi);
                    }
                }
                t2.setType(TileType.AIR);
                fixtureCalc.clearTileFixtures(t2, fp);
                changedTiles.add(t2);
                it.remove();
            }
        }

        for(ExpiPlayer pp : GameServer.get().getPlayers()) {
            for(ExpiTile t2 : changedTiles) {
                pp.getGateway().getManager().putTileChangePacket(t2);
            }
            pp.getGateway().getManager().putFixturePacket(fp);
            pp.getGateway().getManager().putStabilityPacket(affectedTiles);
        }

    }

    private boolean isTileFree(int x, int y) {

        if(worldTerrain[y][x].getType().getSolidity().isSoft()) return true;

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
        while(i != Constants.T_H_CH && worldTerrain[i][10].getType() != TileType.AIR) {
            i++;
        }
        return new Vector2(10, i+2);
    }

    public void saveWorld() {

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

    public HashMap<Fixture, Integer> getFixturesID() {
        return fixtureCalc.getFixturesID();
    }

    public World getBox2dWorld() {
        return box2dWorld;
    }

    public void registerTileListener(TileListener listener) {
        tileListeners.add(listener);
    }

    public void unregisterTileListener(TileListener listener) {
        tileListeners.remove(listener);
    }

    public WeatherType getWeather() {
        return weatherType;
    }
}
