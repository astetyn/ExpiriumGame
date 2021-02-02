package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.api.world.tiles.ExpiTile;
import com.astetyne.expirium.server.resources.TileFix;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;

public class FixtureCalculator implements Disposable {

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
                updateTileFixtures(i, j);
            }
        }

        // borders
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
    public void updateTileFixturesAndNearbyTiles(ExpiTile t) {

        int x = t.getX();
        int y = t.getY();

        updateTileFixtures(y, x);
        if(y != 0) updateTileFixtures(y-1, x);
        if(y != h-1) updateTileFixtures(y+1, x);
        if(x != 0) updateTileFixtures(y, x-1);
        if(x != w-1) updateTileFixtures(y, x+1);

    }

    /** This method will check all nearby tiles and create his own fixtures (does not impact nearby tiles)*/
    private void updateTileFixtures(int y, int x) {

        ExpiTile t = worldTerrain[y][x];

        for(Fixture f : t.getFixtures()) {
            terrainBody.destroyFixture(f);
        }
        t.getFixtures().clear();

        if(t.getType().getFix() != TileFix.FULL && t.getType().getFix() != TileFix.SOFT) {
            // case when tile has own custom fixtures
            float[] vertices = t.getType().getFix().getVertices();
            if(vertices.length < 4)  {
                System.out.println("Bad fixture vertices. Check TileFix please for: "+t.getType().getFix());
                return;
            }
            float lastX = vertices[0];
            float lastY = vertices[1];
            for(int i = 2; i < vertices.length; i+=2) {
                shape.set(lastX + x, lastY + y, vertices[i] + x, vertices[i+1] + y);
                createFixture(t);
                lastX = vertices[i];
                lastY = vertices[i+1];
            }
            return;
        }

        if(t.getType().getFix() != TileFix.FULL) return;

        if(y != 0 && worldTerrain[y-1][x].getType().getFix() != TileFix.FULL) {
            shape.set(x, y, x+1,y);
            createFixture(t);
        }
        if(y != h-1 && worldTerrain[y+1][x].getType().getFix() != TileFix.FULL) {
            shape.set(x, y+1, x+1,y+1);
            createFixture(t);
        }
        if(x != 0 && worldTerrain[y][x-1].getType().getFix() != TileFix.FULL) {
            shape.set(x, y, x,y+1);
            createFixture(t);
        }
        if(x != w-1 && worldTerrain[y][x+1].getType().getFix() != TileFix.FULL) {
            shape.set(x+1, y, x+1,y+1);
            createFixture(t);
        }
    }

    private void createFixture(ExpiTile t) {
        Fixture f = terrainBody.createFixture(fixDef);
        t.getFixtures().add(f);
    }

    @Override
    public void dispose() {
        shape.dispose();
    }
}
