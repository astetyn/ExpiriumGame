package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.backend.FixturePack;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.HashMap;

public class FixtureCalculator {

    private final ExpiTile[][] worldTerrain;
    private final int w, h;
    private final Body terrainBody;
    private final HashMap<Fixture, Integer> fixturesID;

    public FixtureCalculator(ExpiTile[][] worldTerrain, Body terrainBody) {
        this.worldTerrain = worldTerrain;
        this.h = worldTerrain.length;
        this.w = worldTerrain[0].length;
        this.terrainBody = terrainBody;
        fixturesID = new HashMap<>();
    }

    public void generateWorldFixtures() {

        EdgeShape shape = new EdgeShape();
        FixtureDef fixDef = new FixtureDef();

        fixDef.shape = shape;
        fixDef.friction = 0.2f;
        fixDef.filter.categoryBits = Constants.DEFAULT_BIT;

        FixturePack fp = new FixturePack();

        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                checkFixtures(i, j, shape, fixDef, fp);
            }
        }
        shape.dispose();
    }

    public void changeTileTo(int x, int y, TileType type, FixturePack fp) {

        ExpiTile t = worldTerrain[y][x];

        if(!type.isSolid()) {
            clearTile(t, fp);
            t.setType(type);
            return;
        }

        t.setType(type);

        EdgeShape shape = new EdgeShape();
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.friction = 0.2f;
        fixDef.filter.categoryBits = Constants.DEFAULT_BIT;

        checkFixtures(y, x, shape, fixDef, fp);
        if(y != 0) checkFixtures(y-1, x, shape, fixDef, fp);
        if(y != h-1) checkFixtures(y+1, x, shape, fixDef, fp);
        if(x != 0) checkFixtures(y, x-1, shape, fixDef, fp);
        if(x != w-1) checkFixtures(y, x+1, shape, fixDef, fp);

        shape.dispose();

    }

    public void clearTile(ExpiTile t, FixturePack fp) {

        for(Fixture f : t.getFixtures()) {
            terrainBody.destroyFixture(f);
            if(fp.addedFixtures.contains(f)) {
                fp.addedFixtures.remove(f);
            }else {
                fp.removedFixtures.add(fixturesID.get(f));
            }
            fixturesID.remove(f);
        }
        t.getFixtures().clear();

        int y = t.getY();
        int x = t.getX();

        t.setType(TileType.AIR);

        EdgeShape shape = new EdgeShape();
        FixtureDef fixDef = new FixtureDef();

        fixDef.shape = shape;
        fixDef.friction = 0.2f;
        fixDef.filter.categoryBits = Constants.DEFAULT_BIT;

        // updates nearby tiles fixtures
        if(y != 0) checkFixtures(y-1, x, shape, fixDef, fp);
        if(y != h-1) checkFixtures(y+1, x, shape, fixDef, fp);
        if(x != 0) checkFixtures(y, x-1, shape, fixDef, fp);
        if(x != w-1) checkFixtures(y, x+1, shape, fixDef, fp);

        shape.dispose();
    }

    // This method will check all nearby tiles and create his own fixtures (does not impact nearby tiles)
    private void checkFixtures(int y, int x, EdgeShape shape, FixtureDef fixDef, FixturePack fp) {

        ExpiTile t = worldTerrain[y][x];

        if(!t.getType().isSolid()) return;

        for(Fixture f : t.getFixtures()) {
            terrainBody.destroyFixture(f);
            if(fp.addedFixtures.contains(f)) {
                fp.addedFixtures.remove(f);
            }else {
                fp.removedFixtures.add(fixturesID.get(f));
            }
            fixturesID.remove(f);
        }
        t.getFixtures().clear();

        if(y != 0 && !worldTerrain[y-1][x].getType().isSolid()) {
            shape.set(x, y, x+1,y);
            createFixture(fixDef, t, fp);
        }
        if(y != h-1 && !worldTerrain[y+1][x].getType().isSolid()) {
            shape.set(x, y+1, x+1,y+1);
            createFixture(fixDef, t, fp);
        }
        if(x != 0 && !worldTerrain[y][x-1].getType().isSolid()) {
            shape.set(x, y, x,y+1);
            createFixture(fixDef, t, fp);
        }
        if(x != w-1 && !worldTerrain[y][x+1].getType().isSolid()) {
            shape.set(x+1, y, x+1,y+1);
            createFixture(fixDef, t, fp);
        }
    }

    private void createFixture(FixtureDef fixDef, ExpiTile t, FixturePack fp) {

        Fixture f = terrainBody.createFixture(fixDef);
        fp.addedFixtures.add(f);
        t.getFixtures().add(f);

        int randomID;
        do {
            randomID = (int)(Math.random()*Integer.MAX_VALUE);
        } while(fixturesID.containsKey(f));
        fixturesID.put(f, randomID);

    }

    public HashMap<Fixture, Integer> getFixturesID() {
        return fixturesID;
    }
}
