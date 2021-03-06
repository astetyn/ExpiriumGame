package com.astetyne.expirium.server.core.world.calculator;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.world.tile.Tile;
import com.astetyne.expirium.server.core.world.tile.TileFix;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;

public class FixtureCalculator implements Disposable {

    private final Tile[][] terrain;
    private final int w, h;
    private final Body terrainBody;
    private final EdgeShape shape;
    private final FixtureDef fixDef;

    public FixtureCalculator(Tile[][] terrain, int w, int h, Body terrainBody) {
        this.terrain = terrain;
        this.h = h;
        this.w = w;
        this.terrainBody = terrainBody;

        // default setup for tile fixtures
        shape = new EdgeShape();
        fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.friction = 0.2f;
        fixDef.filter.categoryBits = Consts.DEFAULT_BIT;
    }

    public void generateWorldFixtures() {

        // tiles
        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                createOwnTileFixtures(j, i);
            }
        }

        // border
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
        chainShape.dispose();

    }

    /** This method will check all nearby tiles and create his own fixtures + impact nearby tiles*/
    public void updateTileFixturesAndNearbyTiles(Tile t) {

        int x = t.getX();
        int y = t.getY();

        createOwnTileFixtures(x, y);
        createOwnTileFixtures(x-1, y);
        createOwnTileFixtures(x+1, y);
        createOwnTileFixtures(x, y-1);
        createOwnTileFixtures(x, y+1);

    }

    /** This method will check all nearby tiles and create his own fixtures (does not impact nearby tiles)*/
    private void createOwnTileFixtures(int x, int y) {

        if(x < 0 || x >= w || y < 0 || y >= h) return;

        Tile t = terrain[x][y];

        for(Fixture f : t.getFixtures()) {
            terrainBody.destroyFixture(f);
        }
        t.getFixtures().clear();

        if(t.getMaterial().getFix() == TileFix.SOFT) return;

        fixDef.friction = t.getMaterial().getFix().getFriction();

        if(t.getMaterial().getFix() != TileFix.FULL) {
            // case when tile has own custom fixtures
            float[] vertices = t.getMaterial().getFix().getVertices();
            if(vertices.length < 4 || vertices.length % 2 != 0)  {
                System.out.println("Bad fixture vertices. Check TileFix please for: "+t.getMaterial().getFix());
                return;
            }
            float lastX = vertices[0];
            float lastY = vertices[1];
            for(int i = 2; i < vertices.length; i+=2) {
                createLineFixture(t, lastX + x, lastY + y, vertices[i] + x, vertices[i+1] + y);
                lastX = vertices[i];
                lastY = vertices[i+1];
            }
            return;
        }

        if(y != 0 && terrain[x][y-1].getMaterial().getFix() != TileFix.FULL) {
            createLineFixture(t, x, y, x+1,y);
        }
        if(y != h-1 && terrain[x][y+1].getMaterial().getFix() != TileFix.FULL) {
            createLineFixture(t, x, y+1, x+1,y+1);
        }
        if(x != 0 && terrain[x-1][y].getMaterial().getFix() != TileFix.FULL) {
            createLineFixture(t, x, y, x,y+1);
        }
        if(x != w-1 && terrain[x+1][y].getMaterial().getFix() != TileFix.FULL) {
            createLineFixture(t, x+1, y, x+1,y+1);
        }
    }

    private void createLineFixture(Tile t, float fromX, float fromY, float toX, float toY) {
        shape.set(fromX, fromY, toX, toY);
        Fixture f = terrainBody.createFixture(fixDef);
        t.getFixtures().add(f);
    }

    @Override
    public void dispose() {
        shape.dispose();
    }
}
