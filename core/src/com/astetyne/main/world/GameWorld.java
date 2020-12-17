package com.astetyne.main.world;

import com.astetyne.main.Constants;
import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.entity.DroppedItemEntity;
import com.astetyne.main.entity.Entity;
import com.astetyne.main.entity.MainPlayer;
import com.astetyne.main.entity.PlayerEntity;
import com.astetyne.main.net.client.actions.ChunkRequestActionC;
import com.astetyne.main.net.netobjects.*;
import com.astetyne.main.net.server.actions.EntityMoveActionS;
import com.astetyne.main.net.server.actions.InitDataActionS;
import com.astetyne.main.stages.RunningGameStage;
import com.astetyne.main.utils.BodyEditorLoader;
import com.astetyne.main.world.input.WorldInputListener;
import com.astetyne.main.world.tiles.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GameWorld {

    public static float PPM = 64;

    private final World b2dWorld;
    private final SpriteBatch batch;
    private final WorldChunk[] chunkArray;
    private BodyEditorLoader loader;
    private final Body terrainBody;
    private final HashSet<Integer> requestedChunks;
    private final HashMap<Integer, Entity> entitiesID;
    private final List<Entity> entities;
    private final RunningGameStage runningGameStage;
    private final ExpiContactListener contactListener;
    private MainPlayer player;
    private WorldInputListener worldListener;
    private final OrthographicCamera camera;

    public GameWorld(RunningGameStage gameStage, InitDataActionS data) {

        this.batch = gameStage.getBatch();
        this.runningGameStage = gameStage;

        requestedChunks = new HashSet<>();
        entitiesID = new HashMap<>();
        entities = new ArrayList<>();

        camera = new OrthographicCamera();

        b2dWorld = new World(new Vector2(0, -9.81f), false);
        //loader = new BodyEditorLoader(Gdx.files.internal("shapes.json"));
        chunkArray = new WorldChunk[data.getNumberOfChunks()];

        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = b2dWorld.createBody(terrainDef);

        contactListener = new ExpiContactListener();
        b2dWorld.setContactListener(contactListener);

        generateWorldBorders();

        resize();

    }

    public void postSetup(InitDataActionS data) {

        player = new MainPlayer(data.getPlayerID(), data.getPlayerLocation().toVector(), runningGameStage);
        SVector l = data.getPlayerLocation();
        player.getBody().setTransform(l.getX(), l.getY(), 0);

        for(SEntity e : data.getEntities()) {
            if(e instanceof SPlayer) {
                SPlayer p = (SPlayer) e;
                if(p.getID() == player.getID())
                    continue;
                PlayerEntity pe = new PlayerEntity(p.getID(), p.getLocation().toVector(), runningGameStage);
            }else if(e instanceof SDroppedItem) {
                SDroppedItem di = (SDroppedItem) e;
                DroppedItemEntity die = new DroppedItemEntity(di.getID(), di.getType(), runningGameStage, 0, di.getLocation().toVector());
            }
        }

        worldListener = new WorldInputListener(runningGameStage);
        runningGameStage.getMultiplexer().addProcessor(worldListener);

    }

    public void update() {

        worldListener.update();

        for(Entity e : entities) {
            if(e.getInterpolateDelta() == -1) continue;
            e.getBody().setTransform(e.getLocation().lerp(e.getTargetPosition().cpy(), e.getInterpolateDelta()), 0);
            e.setInterpolateDelta(e.getInterpolateDelta()+1.0f/Constants.SERVER_DEFAULT_TPS);
            if(e.getInterpolateDelta() >= 1) {
                e.getBody().setTransform(e.getTargetPosition(), 0);
                e.setInterpolateDelta(-1);
            }
        }

        player.update();

        b2dWorld.step(1/60f, 6, 2);

        cameraCenter();
    }

    public void render() {

        batch.setProjectionMatrix(camera.combined.cpy().scl(PPM));

        // render world
        for(WorldChunk chunk : chunkArray) {
            if(chunk == null) {
                continue;
            }
            Tile[][] terrain = chunk.getTerrain();
            for(int i = 0; i < Constants.T_H_CH; i++) {
                for(int j = 0; j < Constants.T_W_CH; j++) {
                    if(terrain[i][j].getType() != TileType.AIR) {
                        TextureRegion tex = terrain[i][j].getTexture();
                        batch.draw(tex, j + chunk.getId()*Constants.T_W_CH, i, 1, 1);
                    }
                }
            }
        }

        // render players
        for(Entity e : entities) {
            e.draw();
        }

        player.draw();

    }

    public void resize() {
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
    }

    public void checkChunks() {

        int renderDistance = 1;//(int) (Gdx.graphics.getWidth() / (Constants.TILES_WIDTH_CHUNK * ExpiriumGame.PPM)) + 1;

        int currentChunk = (int) (player.getLocation().x / Constants.T_W_CH);

        for(int i = 0; i < Constants.CHUNKS_NUMBER; i++) {
            WorldChunk chunk = chunkArray[i];
            if(i >= currentChunk - renderDistance && i <= currentChunk + renderDistance) {
                if(chunk == null && !requestedChunks.contains(i)) {
                    ExpiriumGame.getGame().getClientGateway().addAction(new ChunkRequestActionC(i));
                    requestedChunks.add(i);
                }
            }else if(chunk != null) {
                for(int k = 0; k < Constants.T_H_CH; k++) {
                    for(int h = 0; h < Constants.T_W_CH; h++) {
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

    public void destroyEntity(Entity entity) {
        entities.remove(entity);
        entitiesID.remove(entity.getID());
        contactListener.unregisterListener(entity);
        b2dWorld.destroyBody(entity.getBody());
    }

    public void dispose() {
        b2dWorld.dispose();
    }

    public void feedChunk(SWorldChunk chunk) {
        WorldChunk worldChunk = new WorldChunk(this, chunk);
        chunkArray[worldChunk.getId()] = worldChunk;
        requestedChunks.remove(worldChunk.getId());
    }

    public void onEntityMove(EntityMoveActionS ema) {
        if(ema.getEntityID() == player.getID()) return;
        if(!entitiesID.containsKey(ema.getEntityID())) return;
        Entity e = entitiesID.get(ema.getEntityID());
        e.getTargetPosition().set(ema.getNewLocation().toVector());
        e.setInterpolateDelta(0);
        e.getBody().setLinearVelocity(ema.getVelocity().toVector());
    }

    private void generateWorldBorders() {

        ChainShape chainShape = new ChainShape();

        float[] verts = new float[10];

        verts[0] = 0;
        verts[1] = 0;
        verts[2] = Constants.CHUNKS_NUMBER * Constants.T_W_CH;
        verts[3] = 0;
        verts[4] = Constants.CHUNKS_NUMBER * Constants.T_W_CH;
        verts[5] = Constants.CHUNKS_NUMBER * Constants.T_H_CH;
        verts[6] = 0;
        verts[7] = Constants.CHUNKS_NUMBER * Constants.T_H_CH;
        verts[8] = 0;
        verts[9] = 0;

        chainShape.createChain(verts);
        terrainBody.createFixture(chainShape, 1);

    }

    private void cameraCenter() {
        Vector3 position = camera.position;
        position.x = camera.position.x  + (player.getLocation().x * PPM - camera.position.x) * .1f;
        position.y = camera.position.y  + (player.getLocation().y * PPM - camera.position.y) * .1f;
        camera.position.set(position);
        camera.update();
    }

    public Tile getTileAt(Vector2 vec) {
        int chunk = (int) (vec.x / Constants.T_W_CH);
        int x = (int) (vec.x - (chunk * Constants.T_W_CH));
        int y = (int) vec.y;
        WorldChunk wch = chunkArray[chunk];
        return wch == null ? null : wch.getTerrain()[y][x];
    }

    public Tile getTileAt(int xG, int yG) {
        int chunk = xG / Constants.T_W_CH;
        int x = xG - (chunk * Constants.T_W_CH);
        WorldChunk wch = chunkArray[chunk];
        return wch == null ? null : wch.getTerrain()[yG][x];
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public World getB2dWorld() {
        return b2dWorld;
    }

    public HashMap<Integer, Entity> getEntitiesID() {
        return entitiesID;
    }

    public Body getTerrainBody() {
        return terrainBody;
    }

    public MainPlayer getPlayer() {
        return player;
    }

    public WorldChunk[] getChunks() {
        return chunkArray;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public ExpiContactListener getCL() {
        return contactListener;
    }
}
