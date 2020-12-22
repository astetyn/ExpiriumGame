package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.backend.FixturePack;
import com.astetyne.expirium.server.backend.Packable;
import com.astetyne.expirium.server.backend.packables.PackableBrokenTile;
import com.astetyne.expirium.server.backend.packets.*;
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
    private List<Packable> subPackets;
    private final int w, h;
    private final WorldGenerator worldGenerator;
    private final StabilityCalculator stabilityCalc;
    private final FixtureCalculator fixtureCalc;

    public ExpiWorld(String worldName) {
        this(worldName, (long)(Math.random()*10000));
    }

    public ExpiWorld(String worldName, long seed) {

        this.worldName = worldName;
        this.seed = seed;

        w = Constants.T_W_CH * Constants.CHUNKS_NUMBER;
        h = Constants.T_H_CH;

        worldTerrain = new ExpiTile[h][w];

        box2dWorld = new World(new Vector2(0, -9.81f), false);
        System.out.println("SERVER: W: "+box2dWorld);
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

        subPackets = GameServer.get().getTickLooper().getTickSubPackets();

        for(int i = 0; i < 60/Constants.SERVER_DEFAULT_TPS; i++) {
            box2dWorld.step(1 / 60f, 6, 2);
        }

        checkChunks();

        recalculateDroppedItems();

        for(ExpiEntity ee : GameServer.get().getEntities()) {
            EntityMovePacket emp = new EntityMovePacket(ee);
            for(ExpiPlayer p : GameServer.get().getPlayers()) {
                if(p == ee) continue;
                p.getGateway().addSubPacket(emp);
            }
        }

    }

    private void recalculateDroppedItems() {

        Iterator<ExpiDroppedItem> it = GameServer.get().getDroppedItems().iterator();
        outer:
        while(it.hasNext()) {
            ExpiDroppedItem item = it.next();
            if(item.getCooldown() != 0) {
                item.reduceCooldown();
                continue;
            }
            Vector2 center = item.getCenter();
            for(ExpiPlayer p : GameServer.get().getPlayers()) {
                Vector2 dif = p.getCenter().sub(center);
                if(dif.len() < Constants.D_I_PICK_DIST) {
                    it.remove();
                    p.getGateway().addSubPacket(new ItemPickupPacket(item.getItemType()));
                    subPackets.add(new EntityDespawnPacket(item));
                    item.destroySafe();
                    continue outer;
                }
            }
            int remainingTicks = item.getTicksToDespawn();
            if(remainingTicks == 0) {
                subPackets.add(new EntityDespawnPacket(item));
                it.remove();
                item.destroySafe();
                continue;
            }
            item.setTicksToDespawn(remainingTicks-1);
        }

    }

    private void checkChunks() {

        for(ExpiPlayer p : GameServer.get().getPlayers()) {

            int renderDistance = 1;
            int currentChunk = (int) (p.getLocation().x / Constants.T_W_CH);

            for(int i = 0; i < Constants.CHUNKS_NUMBER; i++) {
                if(i >= currentChunk - renderDistance && i <= currentChunk + renderDistance) {
                    if(!p.getActiveChunks().contains(i)) {
                        p.getGateway().addSubPacket(new ChunkFeedPacket(worldTerrain, i));
                        p.getActiveChunks().add(i);
                    }
                }else if(p.getActiveChunks().contains(i)) {
                    p.getGateway().addSubPacket(new ChunkDestroyPacket(worldTerrain, i));
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

        float off = (1 - Constants.D_I_SIZE)/2;
        Vector2 loc = new Vector2();
        FixturePack fp = new FixturePack();
        List<PackableBrokenTile> brokenTiles = new ArrayList<>();

        // destroy origin tile first
        loc.set(tile.getX()+off, tile.getY()+off);
        ExpiDroppedItem droppedItem = new ExpiDroppedItem(loc, tile.getType().getDropItem(), Constants.SERVER_DEFAULT_TPS);
        subPackets.add(new EntitySpawnPacket(droppedItem));
        fixtureCalc.clearTile(tile, fp);
        brokenTiles.add(new PackableBrokenTile(tile));

        // recalculate all affected tiles
        HashSet<ExpiTile> affectedTiles = stabilityCalc.clearStabilityAndRecalculate(tile);

        // destroy affected tiles
        Iterator<ExpiTile> it = affectedTiles.iterator();
        while(it.hasNext()) {
            ExpiTile t = it.next();
            if(t.getStability() == 0 && t != tile) {
                loc.set(t.getX()+off, t.getY()+off);
                droppedItem = new ExpiDroppedItem(loc, t.getType().getDropItem(), Constants.SERVER_DEFAULT_TPS);
                subPackets.add(new EntitySpawnPacket(droppedItem));
                fixtureCalc.clearTile(t, fp);
                brokenTiles.add(new PackableBrokenTile(t));
                it.remove();
            }
        }
        subPackets.add(new TileBreakAckPacket(brokenTiles, fp, affectedTiles));
    }

    public void onTilePlaceReq(int c, int x, int y, ItemType item, ExpiPlayer p) {

        //todo: prepocitat stabilitu ci je mozne tam policko postavit
        ExpiTile t = worldTerrain[y][c*Constants.T_W_CH + x];
        FixturePack fp = new FixturePack();
        fixtureCalc.changeTileTo(c*Constants.T_W_CH + x, y, item.getBuildTile(), fp);
        subPackets.add(new TilePlaceAckPacket(t, fp));

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
}
