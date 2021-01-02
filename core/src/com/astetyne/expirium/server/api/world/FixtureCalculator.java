package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.world.tiles.Solidity;
import com.astetyne.expirium.server.backend.FixRes;
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
    private final EdgeShape shape;
    private final FixtureDef fixDef;

    public FixtureCalculator(ExpiTile[][] worldTerrain, Body terrainBody) {
        this.worldTerrain = worldTerrain;
        this.h = worldTerrain.length;
        this.w = worldTerrain[0].length;
        this.terrainBody = terrainBody;
        fixturesID = new HashMap<>();

        shape = new EdgeShape();
        fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.friction = 0.2f;
        fixDef.filter.categoryBits = Constants.DEFAULT_BIT;
    }

    public void generateWorldFixtures() {

        FixturePack fp = new FixturePack();

        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                recalcTileFixtures(i, j, shape, fixDef, fp);
            }
        }
    }

    /** This method will check all nearby tiles and create his own fixtures + impact nearby tiles*/
    public void recalcTileFixturesPlus(ExpiTile t, FixturePack fp) {

        int x = t.getX();
        int y = t.getY();

        recalcTileFixtures(y, x, shape, fixDef, fp);
        if(y != 0) recalcTileFixtures(y-1, x, shape, fixDef, fp);
        if(y != h-1) recalcTileFixtures(y+1, x, shape, fixDef, fp);
        if(x != 0) recalcTileFixtures(y, x-1, shape, fixDef, fp);
        if(x != w-1) recalcTileFixtures(y, x+1, shape, fixDef, fp);

    }

    /** This method will remove all fixtures from given tile and also updates nearby tiles fixtures*/
    public void clearTileFixtures(ExpiTile t, FixturePack fp) {

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

        // updates nearby tiles fixtures
        if(y != 0) recalcTileFixtures(y-1, x, shape, fixDef, fp);
        if(y != h-1) recalcTileFixtures(y+1, x, shape, fixDef, fp);
        if(x != 0) recalcTileFixtures(y, x-1, shape, fixDef, fp);
        if(x != w-1) recalcTileFixtures(y, x+1, shape, fixDef, fp);
    }

    /** This method will check all nearby tiles and create his own fixtures (does not impact nearby tiles)*/
    private void recalcTileFixtures(int y, int x, EdgeShape shape, FixtureDef fixDef, FixturePack fp) {

        ExpiTile t = worldTerrain[y][x];

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

        if(t.getType().getEdgesData() != null) {
            FixRes.EdgesData data = t.getType().getEdgesData();
            for(int i = 0; i < data.l1.size(); i++) {
                shape.set(data.l1.get(i).x + x, data.l1.get(i).y + y, data.l2.get(i).x + x, data.l2.get(i).y + y);
                createFixture(fixDef, t, fp);
            }
        }

        if(t.getType().getSolidity() != Solidity.SOLID) return;

        if(y != 0 && worldTerrain[y-1][x].getType().getSolidity() != Solidity.SOLID) {
            shape.set(x, y, x+1,y);
            createFixture(fixDef, t, fp);
        }
        if(y != h-1 && worldTerrain[y+1][x].getType().getSolidity() != Solidity.SOLID) {
            shape.set(x, y+1, x+1,y+1);
            createFixture(fixDef, t, fp);
        }
        if(x != 0 && worldTerrain[y][x-1].getType().getSolidity() != Solidity.SOLID) {
            shape.set(x, y, x,y+1);
            createFixture(fixDef, t, fp);
        }
        if(x != w-1 && worldTerrain[y][x+1].getType().getSolidity() != Solidity.SOLID) {
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
