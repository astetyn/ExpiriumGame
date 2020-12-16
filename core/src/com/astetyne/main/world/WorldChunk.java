package com.astetyne.main.world;

import com.astetyne.main.Constants;
import com.astetyne.main.net.netobjects.SWorldChunk;
import com.astetyne.main.world.tiles.Tile;
import com.astetyne.main.world.tiles.TileFactory;
import com.astetyne.main.world.tiles.data.AirExtraData;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class WorldChunk {

    private final int id;
    private final Tile[][] terrain;
    private final GameWorld world;

    public WorldChunk(GameWorld world, SWorldChunk chunk) {
        this.world = world;
        this.id = chunk.getId();

        terrain = new Tile[Constants.T_H_CH][Constants.T_W_CH];

        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                terrain[i][j] = new Tile(this, j, i);
                terrain[i][j].setTileExtraData(TileFactory.createExtraData(chunk.getTerrain()[i][j]));
            }
        }
        generateFixtures();
    }

    private void generateFixtures() {

        EdgeShape shape = new EdgeShape();
        FixtureDef fixDef = new FixtureDef();

        fixDef.shape = shape;
        fixDef.friction = 0.2f;
        fixDef.filter.categoryBits = Constants.GROUND_BIT;
        fixDef.filter.maskBits = Constants.ENTITY_BIT;

        int w = Constants.T_W_CH;
        int h = Constants.T_H_CH;

        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                checkFixtures(i, j, shape, fixDef);
            }
        }
        shape.dispose();
    }

    public void destroyTile(Tile t) {

        for(Fixture f : t.getFixtures()) {
            world.getTerrainBody().destroyFixture(f);
        }
        t.getFixtures().clear();

        int i = t.getY();
        int j = t.getX();

        t.setTileExtraData(new AirExtraData());

        EdgeShape shape = new EdgeShape();
        FixtureDef fixDef = new FixtureDef();

        fixDef.shape = shape;
        fixDef.friction = 0.2f;
        fixDef.filter.categoryBits = Constants.GROUND_BIT;
        fixDef.filter.maskBits = Constants.ENTITY_BIT;

        checkFixtures(i-1, j, shape, fixDef);
        checkFixtures(i+1, j, shape, fixDef);
        checkFixtures(i, j-1, shape, fixDef);
        checkFixtures(i, j+1, shape, fixDef);
        shape.dispose();

    }

    private void checkFixtures(int i, int j, EdgeShape shape, FixtureDef fixDef) {

        int w = Constants.T_W_CH;
        int h = Constants.T_H_CH;

        if(i == -1 || i == h) return;

        Tile t;
        int chunkId;

        if(j == -1) {
            if(id == 0) return;
            j = w-1;
            t = world.getChunks()[id-1].getTerrain()[i][j];
            chunkId = id-1;
        }else if(j == w) {
            if(id == world.getChunks().length-1) return;
            j = 0;
            t = world.getChunks()[id+1].getTerrain()[i][j];
            chunkId = id+1;
        }else {
            t = terrain[i][j];
            chunkId = id;
        }

        if(!t.isSolid()) return;

        for(Fixture f : t.getFixtures()) {
            world.getTerrainBody().destroyFixture(f);
        }
        t.getFixtures().clear();

        Body terrainBody = world.getTerrainBody();

        if(isNotSolid(chunkId, i-1, j)) {
            shape.set(chunkId*w+j, i, chunkId*w+j+1,i);
            t.getFixtures().add(terrainBody.createFixture(fixDef));
        }
        if(isNotSolid(chunkId, i+1, j)) {
            shape.set(chunkId*w+j, i+1, chunkId*w+j+1,i+1);
            t.getFixtures().add(terrainBody.createFixture(fixDef));
        }
        if(isNotSolid(chunkId, i, j-1)) {
            shape.set(chunkId*w+j, i, chunkId*w+j,i+1);
            t.getFixtures().add(terrainBody.createFixture(fixDef));
        }
        if(isNotSolid(chunkId, i, j+1)) {
            shape.set(chunkId*w+j+1, i, chunkId*w+j+1,i+1);
            t.getFixtures().add(terrainBody.createFixture(fixDef));
        }

    }

    private boolean isNotSolid(int chunkId, int i, int j) {

        int w = Constants.T_W_CH;
        int h = Constants.T_H_CH;

        if(i == -1 || i == h) return false;

        if(j == -1) {
            if(chunkId == 0 || world.getChunks()[chunkId-1] == null) return false;
            return !world.getChunks()[chunkId-1].getTerrain()[i][w-1].isSolid();
        }else if(j == w) {
            if(chunkId == world.getChunks().length-1 || world.getChunks()[chunkId+1] == null) return false;
            return !world.getChunks()[chunkId+1].getTerrain()[i][0].isSolid();
        }else {
            if(chunkId == id) return !terrain[i][j].isSolid();
            if(world.getChunks()[chunkId] == null) return false;
            return !world.getChunks()[chunkId].getTerrain()[i][j].isSolid();
        }
    }

    public int getId() {
        return id;
    }

    public Tile[][] getTerrain() {
        return terrain;
    }
}
