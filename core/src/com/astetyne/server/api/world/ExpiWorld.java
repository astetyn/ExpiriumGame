package com.astetyne.server.api.world;

import com.astetyne.main.items.ItemType;
import com.astetyne.main.net.netobjects.ExpiChunk;
import com.astetyne.main.net.netobjects.ExpiTile;
import com.astetyne.main.utils.Constants;
import com.astetyne.main.world.Noise;
import com.astetyne.main.world.tiles.TileType;
import com.astetyne.server.GameServer;
import com.astetyne.server.api.entities.ExpiDroppedItem;
import com.astetyne.server.api.entities.ExpiEntity;
import com.astetyne.server.api.entities.ExpiPlayer;
import com.astetyne.server.backend.FixturePack;
import com.astetyne.server.backend.packets.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashMap;

public class ExpiWorld {

    private ExpiChunk[] chunks;
    private final String worldName;
    private final long seed;
    private final World box2dWorld;
    private final Body terrainBody;
    private final HashMap<Fixture, Integer> fixturesID;

    public ExpiWorld(String worldName) {
        this(worldName, (long)(Math.random()*10000));
    }

    public ExpiWorld(String worldName, long seed) {

        this.worldName = worldName;
        this.seed = seed;

        fixturesID = new HashMap<>();

        box2dWorld = new World(new Vector2(0, -9.81f), false);
        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = box2dWorld.createBody(terrainDef);

        FileHandle file = Gdx.files.local("world/"+worldName+".txt");

        if(!file.exists()) {
            generateWorld();
        }else {
            //todo: nacitat svet zo suboru
        }
    }

    public void onTick() {

        for(int i = 0; i < 3; i++) {
            box2dWorld.step(1 / 60f, 6, 2);
        }

        checkChunks();

        for(ExpiEntity ee : GameServer.get().getEntities()) {

            EntityMovePacket emp = new EntityMovePacket(ee);

            for(ExpiPlayer p : GameServer.get().getPlayers()) {
                if(p == ee) continue;
                p.getGateway().addSubPacket(emp);
            }
        }

    }

    private void checkChunks() {

        for(ExpiPlayer p : GameServer.get().getPlayers()) {

            int renderDistance = 1;
            int currentChunk = (int) (p.getLocation().x / Constants.T_W_CH);

            for(int i = 0; i < Constants.CHUNKS_NUMBER; i++) {
                if(i >= currentChunk - renderDistance && i <= currentChunk + renderDistance) {
                    if(!p.getActiveChunks().contains(i)) {
                        p.getGateway().addSubPacket(new ChunkFeedPacket(chunks[i]));
                        p.getActiveChunks().add(i);
                    }
                }else if(p.getActiveChunks().contains(i)) {
                    p.getGateway().addSubPacket(new ChunkDestroyPacket(chunks[i]));
                    p.getActiveChunks().remove(i);
                }
            }
        }

    }

    public void generateWorld() {

        System.out.println("Generating world...");

        chunks = new ExpiChunk[Constants.CHUNKS_NUMBER];

        int chunkWidth = Constants.T_W_CH;
        int chunkHeight = Constants.T_H_CH;

        for(int c = 0; c < chunks.length; c++) {

            ExpiTile[][] terrain = new ExpiTile[chunkHeight][chunkWidth];

            chunks[c] = new ExpiChunk(c, terrain);

            for(int j = 0; j < chunkWidth; j++) {

                int h = (int) (50 + Noise.noise((c*chunkWidth+j) / 32.0f, 0, 0) * 20);

                for(int i = 0; i < chunkHeight; i++) {
                    if(i == h) {
                        terrain[i][j] = new ExpiTile(TileType.GRASS, chunks[c], j, i);
                    }else if(i < h && i > h-5) {
                        terrain[i][j] = new ExpiTile(TileType.DIRT, chunks[c], j, i);
                    }else if(i < h) {
                        terrain[i][j] = new ExpiTile(TileType.STONE, chunks[c], j, i);
                    }else {
                        terrain[i][j] = new ExpiTile(TileType.AIR, chunks[c], j, i);
                    }
                }
            }
        }

        System.out.println("Creating fixtures...");
        generateFixtures();

        System.out.println("Generating world done!");

    }

    private void recalculateStability(ExpiTile tile) {

        //todo: prepocitat stabilitu pre cely svet

    }

    public void onTileBreakReq(int c, int x, int y, ExpiPlayer p) {
        //todo: prepocitat stabilitu okolitych policok a podla toho vygenerovat TileBreakActions
        ExpiTile tile = chunks[c].getTerrain()[y][x];
        if(tile.getType() == TileType.AIR) return;
        float off = (1 - Constants.D_I_SIZE)/2;
        Vector2 loc = new Vector2(x+off, y+off);
        ExpiDroppedItem droppedItem = new ExpiDroppedItem(loc, tile.getType().getDropItem(), Constants.SERVER_DEFAULT_TPS);
        GameServer.get().getDroppedItems().add(droppedItem);

        FixturePack fp = destroyTile(tile);
        GameServer.get().getTickLooper().getTickSubPackets().add(new TileBreakAckPacket(c, x, y, fp));
        GameServer.get().getTickLooper().getTickSubPackets().add(new EntitySpawnPacket(droppedItem));
    }

    public void onTilePlaceReq(int c, int x, int y, ItemType item, ExpiPlayer p) {

        //todo: prepocitat stabilitu ci je mozne tam policko postavit

        //in case of success
        //ExpiTile t = chunks[tpa.getChunkID()].getTerrain()[tpa.getY()][tpa.getX()];
        //t.setType(tpa.getPlacedItem().initDefaultData().getType());
        //GameServer.get().getTickLooper().getTickActions().add(tpa);

    }

    public Vector2 getSaveLocation() {
        int i = 0;
        while(i != Constants.T_H_CH && chunks[0].getTerrain()[i][10].getType() != TileType.AIR) {
            i++;
        }
        return new Vector2(10, i+2);
    }

    public ExpiChunk getChunk(int id) {
        return chunks[id];
    }

    public void saveWorld() {

    }

    public ExpiChunk[] getChunks() {
        return chunks;
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

    /*public void changeTile(int c, int x, int y, TileExtraData data) {

        ExpiTile t = chunks[c].getTerrain()[y][x];

        if(!data.isSolid()) {
            destroyTile(t);
            //t.setTileExtraData(data);
            return;
        }

        //t.setTileExtraData(data);

        EdgeShape shape = new EdgeShape();
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.friction = 0.2f;
        fixDef.filter.categoryBits = Constants.DEFAULT_BIT;

        checkFixtures(c, y, x, shape, fixDef);
        checkFixtures(c, y-1, x, shape, fixDef);
        checkFixtures(c, y+1, x, shape, fixDef);
        checkFixtures(c, y, x-1, shape, fixDef);
        checkFixtures(c, y, x+1, shape, fixDef);

        shape.dispose();

    }*/

    public FixturePack destroyTile(ExpiTile t) {

        FixturePack fp = new FixturePack();

        for(Fixture f : t.getFixtures()) {
            terrainBody.destroyFixture(f);
            fp.removedFixtures.add(fixturesID.get(f));
            fixturesID.remove(f);
        }
        t.getFixtures().clear();

        int c = t.getChunk().getId();
        int i = t.getY();
        int j = t.getX();

        t.setType(TileType.AIR);

        EdgeShape shape = new EdgeShape();
        FixtureDef fixDef = new FixtureDef();

        fixDef.shape = shape;
        fixDef.friction = 0.2f;
        fixDef.filter.categoryBits = Constants.DEFAULT_BIT;

        fp.addAll(checkFixtures(c, i-1, j, shape, fixDef));
        fp.addAll(checkFixtures(c, i+1, j, shape, fixDef));
        fp.addAll(checkFixtures(c, i, j-1, shape, fixDef));
        fp.addAll(checkFixtures(c, i, j+1, shape, fixDef));

        shape.dispose();

        return fp;
    }

    public void generateFixtures() {

        EdgeShape shape = new EdgeShape();
        FixtureDef fixDef = new FixtureDef();

        fixDef.shape = shape;
        fixDef.friction = 0.2f;
        fixDef.filter.categoryBits = Constants.DEFAULT_BIT;

        int w = Constants.T_W_CH;
        int h = Constants.T_H_CH;

        for(int c = 0; c < chunks.length; c++) {
            for(int i = 0; i < h; i++) {
                for(int j = 0; j < w; j++) {
                    checkFixtures(c, i, j, shape, fixDef);
                }
            }
        }
        shape.dispose();
    }

    // This method will check all nearby tiles and create his own fixtures (does not impact nearby tiles)
    private FixturePack checkFixtures(int c, int i, int j, EdgeShape shape, FixtureDef fixDef) {

        FixturePack fp = new FixturePack();

        int w = Constants.T_W_CH;
        int h = Constants.T_H_CH;

        if(i == -1 || i == h) return fp;

        ExpiTile t;
        int chunkId;

        if(j == -1) {
            if(c == 0) return fp;
            j = w-1;
            t = chunks[c-1].getTerrain()[i][j];
            chunkId = c-1;
        }else if(j == w) {
            if(c == chunks.length-1) return fp;
            j = 0;
            t = chunks[c+1].getTerrain()[i][j];
            chunkId = c+1;
        }else {
            t = chunks[c].getTerrain()[i][j];
            chunkId = c;
        }

        if(!t.getType().isSolid()) return fp;

        for(Fixture f : t.getFixtures()) {
            terrainBody.destroyFixture(f);
            fp.removedFixtures.add(fixturesID.get(f));
            fixturesID.remove(f);
        }
        t.getFixtures().clear();

        if(isNotSolid(chunkId, c, i-1, j)) {
            shape.set(chunkId*w+j, i, chunkId*w+j+1,i);
            Fixture f = terrainBody.createFixture(fixDef);
            fp.addedFixtures.add(f);
            t.getFixtures().add(f);
            addUniqueFixture(f);
        }
        if(isNotSolid(chunkId, c, i+1, j)) {
            shape.set(chunkId*w+j, i+1, chunkId*w+j+1,i+1);
            Fixture f = terrainBody.createFixture(fixDef);
            fp.addedFixtures.add(f);
            t.getFixtures().add(f);
            addUniqueFixture(f);
        }
        if(isNotSolid(chunkId, c, i, j-1)) {
            shape.set(chunkId*w+j, i, chunkId*w+j,i+1);
            Fixture f = terrainBody.createFixture(fixDef);
            fp.addedFixtures.add(f);
            t.getFixtures().add(f);
            addUniqueFixture(f);
        }
        if(isNotSolid(chunkId, c, i, j+1)) {
            shape.set(chunkId*w+j+1, i, chunkId*w+j+1,i+1);
            Fixture f = terrainBody.createFixture(fixDef);
            fp.addedFixtures.add(f);
            t.getFixtures().add(f);
            addUniqueFixture(f);
        }

        return fp;
    }

    private boolean isNotSolid(int chunkId, int c, int i, int j) {

        int w = Constants.T_W_CH;
        int h = Constants.T_H_CH;

        if(i == -1 || i == h) return false;

        if(j == -1) {
            if(chunkId == 0) return false;
            return !chunks[chunkId-1].getTerrain()[i][w-1].getType().isSolid();
        }else if(j == w) {
            if(chunkId == chunks.length-1) return false;
            return !chunks[chunkId+1].getTerrain()[i][0].getType().isSolid();
        }else {
            if(chunkId == c) return !chunks[c].getTerrain()[i][j].getType().isSolid();
            return !chunks[chunkId].getTerrain()[i][j].getType().isSolid();
        }
    }

    private void addUniqueFixture(Fixture f) {
        int randomID;
        do {
            randomID = (int)(Math.random()*Integer.MAX_VALUE);
        } while(fixturesID.containsKey(f));
        fixturesID.put(f, randomID);
    }

    public HashMap<Fixture, Integer> getFixturesID() {
        return fixturesID;
    }

    public World getBox2dWorld() {
        return box2dWorld;
    }
}
