package com.astetyne.main.world;

import com.astetyne.main.Constants;
import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.entity.Entity;
import com.astetyne.main.entity.Player;
import com.astetyne.main.net.client.actions.ChunkRequestActionC;
import com.astetyne.main.net.netobjects.SWorldChunk;
import com.astetyne.main.stages.RunningGameStage;
import com.astetyne.main.utils.BodyEditorLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GameWorld {

    private final World world;
    private final SpriteBatch batch;
    private final WorldChunk[] chunkArray;
    private final BodyEditorLoader loader;
    private final Body terrainBody;
    private final HashSet<Integer> requestedChunks;
    private final HashMap<Integer, Entity> entitiesID;
    private final List<Player> otherPlayers;
    private final RunningGameStage runningGameStage;
    private final ExpiContactListener contactListener;

    public GameWorld(SpriteBatch batch, RunningGameStage runningGame) {

        this.batch = batch;
        this.runningGameStage = runningGame;

        requestedChunks = new HashSet<>();
        entitiesID = new HashMap<>();
        otherPlayers = new ArrayList<>();

        world = new World(new Vector2(0, -9.81f), false);
        loader = new BodyEditorLoader(Gdx.files.internal("shapes.json"));
        chunkArray = new WorldChunk[Constants.CHUNKS_NUMBER];

        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = world.createBody(terrainDef);

        contactListener = new ExpiContactListener();

        world.setContactListener(contactListener);

    }

    public void update() {

        for(Player p : otherPlayers) {
            if(p.getInterpolateDelta() == -1) continue;
            p.getBody().setTransform(p.getLocation().lerp(p.getTargetPosition().cpy(), p.getInterpolateDelta()), 0);
            p.setInterpolateDelta(p.getInterpolateDelta()+1.0f/Constants.SERVER_DEFAULT_TPS);
            if(p.getInterpolateDelta() >= 1) {
                p.getBody().setTransform(p.getTargetPosition(), 0);
                p.setInterpolateDelta(-1);
            }
        }

        world.step(1/60f, 6, 2);
    }

    public void render() {

        batch.setProjectionMatrix(runningGameStage.getCamera().combined.cpy().scl(RunningGameStage.PPM));

        for(WorldChunk chunk : chunkArray) {
            if(chunk == null) {
                continue;
            }
            Tile[][] terrain = chunk.getTerrain();
            for(int i = 0; i < Constants.TILES_HEIGHT_CHUNK; i++) {
                for(int j = 0; j < Constants.TILES_WIDTH_CHUNK; j++) {
                    if(terrain[i][j].getType() != TileType.AIR) {
                        TextureRegion tex = terrain[i][j].getTexture();
                        batch.draw(tex, j + chunk.getId()*Constants.TILES_WIDTH_CHUNK, i, 1, 1);
                    }
                }
            }
        }

    }

    public void checkChunks() {

        int renderDistance = 1;//(int) (Gdx.graphics.getWidth() / (Constants.TILES_WIDTH_CHUNK * ExpiriumGame.PPM)) + 1;

        int currentChunk = (int) (runningGameStage.getPlayer().getLocation().x / Constants.TILES_WIDTH_CHUNK);

        for(int i = 0; i < Constants.CHUNKS_NUMBER; i++) {
            WorldChunk chunk = chunkArray[i];
            if(i >= currentChunk - renderDistance && i <= currentChunk + renderDistance) {
                if(chunk == null && !requestedChunks.contains(i)) {
                    ExpiriumGame.getGame().getClientGateway().addAction(new ChunkRequestActionC(i));
                    //System.out.println("REQ ON CLIENT: "+i);
                    requestedChunks.add(i);
                }
            }else if(chunk != null) {
                for(int k = 0; k < Constants.TILES_HEIGHT_CHUNK; k++) {
                    for(int h = 0; h < Constants.TILES_WIDTH_CHUNK; h++) {
                        Tile t = chunk.getTerrain()[k][h];
                        for(Fixture f : t.getFixtures()) {
                            terrainBody.destroyFixture(f);
                        }
                    }
                }
                chunkArray[i] = null;
            }
        }
    }

    public Entity createEntity(int id, Vector2 location, Class<?> entityClass) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(location);

        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 40f;
        fixtureDef.friction = 3f;
        fixtureDef.restitution = 0f;

        body.setFixedRotation(true);

        loader.attachFixture(body, "player", fixtureDef, 2);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.4f, 0.1f, new Vector2(0.45f, 0f), 0);
        fixtureDef.density = 0f;
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        Fixture jumpSensor = body.createFixture(fixtureDef);
        shape.dispose();

        try {

            Entity entity = (Entity) entityClass.getConstructor(int.class, Body.class).newInstance(id, body);
            contactListener.registerListener(jumpSensor, entity);
            entitiesID.put(id, entity);
            return entity;

        }catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void dispose() {
        world.dispose();
    }

    public void feedChunk(SWorldChunk chunk) {
        WorldChunk worldChunk = new WorldChunk(chunk);
        generateFixtures(worldChunk);
        chunkArray[worldChunk.getId()] = worldChunk;
        requestedChunks.remove(worldChunk.getId());
    }

    private void generateFixtures(WorldChunk chunk) {

        EdgeShape shape = new EdgeShape();

        int w = Constants.TILES_WIDTH_CHUNK;
        int h = Constants.TILES_HEIGHT_CHUNK;

        Tile[][] terrain = chunk.getTerrain();

        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {

                Tile t = terrain[i][j];
                if(!t.isSolid()) continue;

                if(i != 0 && !terrain[i-1][j].isSolid()) {
                    shape.set(chunk.getId()*w+j, i, chunk.getId()*w+j+1,i);
                    t.getFixtures().add(terrainBody.createFixture(shape, 1));
                }
                if(i != h-1 && !terrain[i+1][j].isSolid()) {
                    shape.set(chunk.getId()*w+j, i+1, chunk.getId()*w+j+1,i+1);
                    t.getFixtures().add(terrainBody.createFixture(shape, 1));
                }
                if(j != 0 && !terrain[i][j-1].isSolid()) {
                    shape.set(chunk.getId()*w+j, i, chunk.getId()*w+j,i+1);
                    t.getFixtures().add(terrainBody.createFixture(shape, 1));
                }
                if(j != w-1 && !terrain[i][j+1].isSolid()) {
                    shape.set(chunk.getId()*w+j+1, i, chunk.getId()*w+j+1,i+1);
                    t.getFixtures().add(terrainBody.createFixture(shape, 1));
                }
            }
        }

        if(chunk.getId() != 0 && chunkArray[chunk.getId()-1] != null) {

            Tile[][] terrainLeft = chunkArray[chunk.getId()-1].getTerrain();

            // left column
            for(int i = 0; i < h; i++) {
                Tile t = terrain[i][0];
                if(t.isSolid() && !terrainLeft[i][w-1].isSolid()) {
                    shape.set(chunk.getId()*w, i, chunk.getId()*w,i+1);
                    t.getFixtures().add(terrainBody.createFixture(shape, 1));
                }
            }
        }

        // right column
        if(chunk.getId() != Constants.CHUNKS_NUMBER-1 && chunkArray[chunk.getId()+1] != null) {

            Tile[][] terrainRight = chunkArray[chunk.getId()+1].getTerrain();

            for(int i = 0; i < h; i++) {
                Tile t = terrain[i][w - 1];
                if(t.isSolid() && !terrainRight[i][0].isSolid()) {
                    shape.set((chunk.getId()+1) * w, i, (chunk.getId()+1)+1, i + 1);
                    t.getFixtures().add(terrainBody.createFixture(shape, 1));
                }
            }
        }
        shape.dispose();
    }

    public World getWorld() {
        return world;
    }

    public HashMap<Integer, Entity> getEntitiesID() {
        return entitiesID;
    }

    public List<Player> getOtherPlayers() {
        return otherPlayers;
    }

    public ExpiContactListener getContactListener() {
        return contactListener;
    }
}
