package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.client.tiles.Solidity;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.api.world.tiles.ExpiTile;
import com.badlogic.gdx.physics.box2d.*;

public class FixtureCalculator {

    private final ExpiTile[][] worldTerrain;
    private final int w, h;
    private final Body terrainBody;
    private final EdgeShape shape;
    private final FixtureDef fixDef;

    public FixtureCalculator(ExpiWorld world, Body terrainBody) {
        this.worldTerrain = world.getTerrain();
        this.h = world.getTerrainHeight();
        this.w = world.getTerrainWidth();
        this.terrainBody = terrainBody;

        shape = new EdgeShape();
        fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.friction = 0.2f;
        fixDef.filter.categoryBits = Consts.DEFAULT_BIT;
    }

    public void generateWorldFixtures() {

        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                recalcTileFixtures(i, j, shape, fixDef);
            }
        }

        ChainShape chainShape = new ChainShape();

        float[] verts = new float[10];

        verts[0] = 0;
        verts[1] = 0;
        verts[2] = w;
        verts[3] = 0;
        verts[5] = h;
        verts[4] = w;
        verts[6] = 0;
        verts[7] = h;
        verts[8] = 0;
        verts[9] = 0;

        chainShape.createChain(verts);
        terrainBody.createFixture(chainShape, 1);

    }

    /** This method will check all nearby tiles and create his own fixtures + impact nearby tiles*/
    public void recalcTileFixturesPlus(ExpiTile t) {

        int x = t.getX();
        int y = t.getY();

        recalcTileFixtures(y, x, shape, fixDef);
        if(y != 0) recalcTileFixtures(y-1, x, shape, fixDef);
        if(y != h-1) recalcTileFixtures(y+1, x, shape, fixDef);
        if(x != 0) recalcTileFixtures(y, x-1, shape, fixDef);
        if(x != w-1) recalcTileFixtures(y, x+1, shape, fixDef);

    }

    /** This method will remove all fixtures from given tile and also updates nearby tiles fixtures*/
    public void clearTileFixtures(ExpiTile t) {

        for(Fixture f : t.getFixtures()) {
            terrainBody.destroyFixture(f);
        }
        t.getFixtures().clear();

        int y = t.getY();
        int x = t.getX();

        // updates nearby tiles fixtures
        if(y != 0) recalcTileFixtures(y-1, x, shape, fixDef);
        if(y != h-1) recalcTileFixtures(y+1, x, shape, fixDef);
        if(x != 0) recalcTileFixtures(y, x-1, shape, fixDef);
        if(x != w-1) recalcTileFixtures(y, x+1, shape, fixDef);
    }

    /** This method will check all nearby tiles and create his own fixtures (does not impact nearby tiles)*/
    private void recalcTileFixtures(int y, int x, EdgeShape shape, FixtureDef fixDef) {

        ExpiTile t = worldTerrain[y][x];

        for(Fixture f : t.getFixtures()) {
            terrainBody.destroyFixture(f);
        }
        t.getFixtures().clear();

        if(t.getTypeFront().getTileFix() != null) {
            float[] vertices = t.getTypeFront().getTileFix().getVertices();
            if(vertices.length < 4)  {
                System.out.println("Bad fixture vertices. Check TileFix please for: "+t.getTypeFront().getTileFix());
                return;
            }
            float lastX = vertices[0];
            float lastY = vertices[1];
            for(int i = 2; i < vertices.length; i+=2) {
                shape.set(lastX + x, lastY + y, vertices[i] + x, vertices[i+1] + y);
                createFixture(fixDef, t);
                lastX = vertices[i];
                lastY = vertices[i+1];
            }
        }

        if(t.getTypeFront().getSolidity() != Solidity.SOLID) return;

        if(y != 0 && worldTerrain[y-1][x].getTypeFront().getSolidity() != Solidity.SOLID) {
            shape.set(x, y, x+1,y);
            createFixture(fixDef, t);
        }
        if(y != h-1 && worldTerrain[y+1][x].getTypeFront().getSolidity() != Solidity.SOLID) {
            shape.set(x, y+1, x+1,y+1);
            createFixture(fixDef, t);
        }
        if(x != 0 && worldTerrain[y][x-1].getTypeFront().getSolidity() != Solidity.SOLID) {
            shape.set(x, y, x,y+1);
            createFixture(fixDef, t);
        }
        if(x != w-1 && worldTerrain[y][x+1].getTypeFront().getSolidity() != Solidity.SOLID) {
            shape.set(x+1, y, x+1,y+1);
            createFixture(fixDef, t);
        }
    }

    private void createFixture(FixtureDef fixDef, ExpiTile t) {
        Fixture f = terrainBody.createFixture(fixDef);
        t.getFixtures().add(f);
    }
}
