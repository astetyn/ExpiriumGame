package com.astetyne.main.world;

import com.astetyne.main.Constants;
import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.entity.Entity;
import com.astetyne.main.entity.MainPlayer;
import com.astetyne.main.entity.PlayerEntity;
import com.astetyne.main.net.client.actions.ChunkRequestActionC;
import com.astetyne.main.net.netobjects.SPlayer;
import com.astetyne.main.net.netobjects.SVector;
import com.astetyne.main.net.netobjects.SWorldChunk;
import com.astetyne.main.net.server.actions.EntityMoveActionS;
import com.astetyne.main.net.server.actions.InitDataActionS;
import com.astetyne.main.stages.RunningGameStage;
import com.astetyne.main.utils.BodyEditorLoader;
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
    private final List<PlayerEntity> otherPlayers;
    private final RunningGameStage runningGameStage;
    private final ExpiContactListener contactListener;
    private final MainPlayer player;
    private final WorldInputListener worldListener;
    private final OrthographicCamera camera;

    public GameWorld(SpriteBatch batch, RunningGameStage gameStage, InitDataActionS initData) {

        camera = new OrthographicCamera();

        this.batch = batch;
        this.runningGameStage = gameStage;

        requestedChunks = new HashSet<>();
        entitiesID = new HashMap<>();
        otherPlayers = new ArrayList<>();

        b2dWorld = new World(new Vector2(0, -9.81f), false);
        //loader = new BodyEditorLoader(Gdx.files.internal("shapes.json"));
        chunkArray = new WorldChunk[initData.getNumberOfChunks()];

        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = b2dWorld.createBody(terrainDef);

        contactListener = new ExpiContactListener();

        b2dWorld.setContactListener(contactListener);

        generateWorldBorders();

        player = createMainPlayer(initData.getPlayerID(), initData.getPlayerLocation().toVector());

        SVector l = initData.getPlayerLocation();
        player.getBody().setTransform(l.getX(), l.getY(), 0);

        for(SPlayer p : initData.getPlayersEntities()) {
            if(p.getID() == player.getID()) continue;
            createPlayerEntity(p.getID(), p.getLocation().toVector());
        }

        worldListener = new WorldInputListener(gameStage, this);
        runningGameStage.getMultiplexer().addProcessor(worldListener);

        resize();

    }

    public void update() {

        worldListener.update();

        for(PlayerEntity p : otherPlayers) {
            if(p.getInterpolateDelta() == -1) continue;
            p.getBody().setTransform(p.getLocation().lerp(p.getTargetPosition().cpy(), p.getInterpolateDelta()), 0);
            p.setInterpolateDelta(p.getInterpolateDelta()+1.0f/Constants.SERVER_DEFAULT_TPS);
            if(p.getInterpolateDelta() >= 1) {
                p.getBody().setTransform(p.getTargetPosition(), 0);
                p.setInterpolateDelta(-1);
            }
        }

        player.move();

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
        worldListener.renderBreakingTile(batch);

        // render players
        for(PlayerEntity pe : otherPlayers) {
            pe.draw();
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
                        com.astetyne.main.world.tiles.Tile t = chunk.getTerrain()[k][h];
                        for(Fixture f : t.getFixtures()) {
                            terrainBody.destroyFixture(f);
                        }
                    }
                }
                chunkArray[i] = null;
            }
        }
    }

    public MainPlayer createMainPlayer(int id, Vector2 location) {
        BodyInfo bodyInfo = createPlayerBody(location);
        MainPlayer player = new MainPlayer(id, bodyInfo.body, batch, runningGameStage.getGameGUI().getMovementTS());
        contactListener.registerListener(bodyInfo.jumpSensor, player);
        entitiesID.put(id, player);
        return player;
    }

    public PlayerEntity createPlayerEntity(int id, Vector2 location) {
        BodyInfo bodyInfo = createPlayerBody(location);
        PlayerEntity pe = new PlayerEntity(id, bodyInfo.body, batch);
        contactListener.registerListener(bodyInfo.jumpSensor, pe);
        entitiesID.put(id, pe);
        otherPlayers.add(pe);
        return pe;
    }

    private BodyInfo createPlayerBody(Vector2 location) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(location);

        Body body = b2dWorld.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 30f;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = Constants.ENTITY_BIT;
        fixtureDef.filter.maskBits = Constants.GROUND_BIT;

        body.setFixedRotation(true);

        // upper poly
        PolygonShape polyShape = new PolygonShape();
        polyShape.setAsBox(0.45f, 0.55f, new Vector2(0.45f, 0.7f), 0);
        fixtureDef.shape = polyShape;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef);

        // bottom poly
        float[] verts = new float[8];
        verts[0] = 0.05f;
        verts[1] = 0.15f;
        verts[2] = 0.2f;
        verts[3] = 0;
        verts[4] = 0.7f;
        verts[5] = 0;
        verts[6] = 0.85f;
        verts[7] = 0.15f;

        polyShape.set(verts);
        fixtureDef.shape = polyShape;
        fixtureDef.friction = 1;
        body.createFixture(fixtureDef);

        // sensor
        polyShape.setAsBox(0.4f, 0.3f, new Vector2(0.45f, 0.2f), 0);
        fixtureDef.density = 0f;
        fixtureDef.shape = polyShape;
        fixtureDef.isSensor = true;
        Fixture jumpSensor = body.createFixture(fixtureDef);

        polyShape.dispose();

        return new BodyInfo(body, jumpSensor);
    }

    public void destroyEntity(Entity entity) {
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
        position.x = player.getLocation().x * PPM;
        position.y = player.getLocation().y * PPM;
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

    public List<PlayerEntity> getOtherPlayers() {
        return otherPlayers;
    }

    static class BodyInfo {

        Body body;
        Fixture jumpSensor;

        public BodyInfo(Body body, Fixture jumpSensor) {
            this.body = body;
            this.jumpSensor = jumpSensor;
        }
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
}
