package com.astetyne.expirium.main.world;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.entity.Entity;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.entity.MainPlayer;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.tiles.Tile;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.api.world.inventory.ChosenSlot;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameWorld {

    public static float PPM = 64;

    private final World b2dWorld;
    private final SpriteBatch batch;
    private final HashMap<Integer, WorldChunk> loadedChunks;
    private final Body terrainBody;
    private final HashMap<Integer, Entity> entitiesID;
    private final List<Entity> entities;
    private MainPlayer player;
    private final OrthographicCamera camera;
    private final HashMap<Integer, Fixture> fixturesID;
    private final FrameBuffer lightsFrameBuffer;
    private final List<LightSource> activeLights;
    private final Matrix4 screenMatrix;
    private int chunksInMap;

    private final Pool<WorldChunk> chunkPool = new Pool<WorldChunk>() {
        @Override
        protected WorldChunk newObject() {
            return new WorldChunk();
        }
    };

    public GameWorld() {

        this.batch = ExpiGame.get().getBatch();

        loadedChunks = new HashMap<>();
        fixturesID = new HashMap<>();
        entitiesID = new HashMap<>();
        entities = new ArrayList<>();
        activeLights = new ArrayList<>();

        screenMatrix = new Matrix4().setToOrtho2D(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        lightsFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
        camera.update();

        b2dWorld = new World(new Vector2(0, -9.81f), false);

        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = b2dWorld.createBody(terrainDef);
    }

    public void loadData(PacketInputStream in) {

        chunksInMap = in.getInt();
        chunkPool.fill(3);

        int pID = in.getInt();
        Vector2 loc = in.getVector();
        player = new MainPlayer(pID, loc);
        player.getBody().setTransform(loc, 0);

        int size = in.getInt();
        for(int i = 0; i < size; i++) {
            EntityType.getType(in.getInt()).initEntity();
        }

        generateWorldBorders();

    }

    public static int steps;

    public void update() {

        for(Entity e : entities) {
            e.move();
        }

        player.update();

        b2dWorld.step(1/60f, 6, 2);
        steps++;

        cameraCenter();
    }

    public void render() {

        batch.setProjectionMatrix(camera.combined);

        // render world
        for(Map.Entry<Integer, WorldChunk> entry : loadedChunks.entrySet()) {
            WorldChunk chunk = entry.getValue();
            Tile[][] terrain = chunk.getTerrain();
            int offset = chunk.getId() * Consts.T_W_CH;
            for(int i = 0; i < Consts.T_H_CH; i++) {
                for(int j = 0; j < Consts.T_W_CH; j++) {

                    Tile t = terrain[i][j];
                    if(t.getType() == TileType.AIR) continue;

                    if(GameScreen.get().getGameStage().getFocusedSlot().getSlotType() == ChosenSlot.MATERIAL_SLOT) {
                        player.getTilePlacer().render(t);
                        continue;
                    }
                    batch.draw(t.getTex(), j + offset, i, 1, 1);
                }
            }
        }

        // render entities
        for(Entity e : entities) {
            // this is for entities from entities.atlas
            if(e.getType() != EntityType.DROPPED_ITEM) {
                e.draw();
            }
        }

        player.draw();

        for(Entity e : entities) {
            // this is for dropped items from gui.atlas
            if(e.getType() == EntityType.DROPPED_ITEM) {
                e.draw();
            }
        }

        batch.end();

        lightsFrameBuffer.begin();

        Gdx.gl.glClearColor(0f,0f,0f,0.8f); // ambient color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_REVERSE_SUBTRACT);

        batch.begin();
        for(LightSource l : activeLights) {
            LightType type = l.getType();
            float width = type.width;
            float height = type.height;
            batch.draw(type.texture, l.getLoc().x - width/2, l.getLoc().y - height/2, width, height);
        }
        batch.end();
        lightsFrameBuffer.end();

        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);

        batch.setProjectionMatrix(screenMatrix);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        int fw = lightsFrameBuffer.getWidth();
        int fh = lightsFrameBuffer.getHeight();

        batch.begin();
        batch.draw(lightsFrameBuffer.getColorBufferTexture(), 0, 0, sw, sh,  0, 0, fw, fh, false, true);

    }

    public void dispose() {
        b2dWorld.dispose();
    }

    public void onFeedChunkEvent() {

        PacketInputStream in = ExpiGame.get().getClientGateway().getIn();

        WorldChunk chunk = chunkPool.obtain();
        chunk.init(in);
        loadedChunks.put(chunk.getId(), chunk);
    }

    public void onDestroyChunkEvent() {

        PacketInputStream in = ExpiGame.get().getClientGateway().getIn();

        WorldChunk chunk = loadedChunks.get(in.getInt());

        for(int i = 0; i < Consts.T_H_CH; i++) {
            for(int j = 0; j < Consts.T_W_CH; j++) {
                Tile t = chunk.getTerrain()[i][j];
                for(LightSource ls : t.getAttachedLights()) {
                    activeLights.remove(ls);
                }
            }
        }
        loadedChunks.remove(chunk.getId());
        chunkPool.free(chunk);
    }

    public void onTileChange(PacketInputStream in) {

        TileType type = TileType.getType(in.getInt());
        int c = in.getInt();
        int x = in.getInt();
        int y = in.getInt();

        Tile t = loadedChunks.get(c).getTerrain()[y][x];

        for(LightSource ls : t.getAttachedLights()) {
            activeLights.remove(ls);
        }
        t.getAttachedLights().clear();

        t.setType(type);

        if(type == TileType.CAMPFIRE_BIG) {
            LightSource ls = new LightSource(LightType.TRANSP_SPHERE_MEDIUM, new Vector2(c*Consts.T_W_CH+x + 0.5f, y+0.5f));
            activeLights.add(ls);
            t.getAttachedLights().add(ls);
        }

    }

    public void onFixturesChange(PacketInputStream in) {

        int size = in.getInt();
        EdgeShape shape = new EdgeShape();
        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.friction = 0.2f;
        def.filter.categoryBits = Consts.DEFAULT_BIT;

        for(int i = 0; i < size; i++) {
            int id = in.getInt();
            shape.set(in.getFloat(), in.getFloat(),in.getFloat(), in.getFloat());
            Fixture f = terrainBody.createFixture(def);
            fixturesID.put(id, f);
        }

        size = in.getInt();
        for(int i = 0; i < size; i++) {
            int fixID = in.getInt();
            Fixture f = fixturesID.get(fixID);
            terrainBody.destroyFixture(f);
            fixturesID.remove(fixID);
        }
    }

    public void onStabilityChange(PacketInputStream in) {

        int size = in.getInt();
        for(int i = 0; i < size; i++) {
            int c = in.getInt();
            int x = in.getInt();
            int y = in.getInt();
            int stability = in.getInt();
            System.out.println(stability);
            loadedChunks.get(c).getTerrain()[y][x].setStability(stability);
        }
    }

    private void generateWorldBorders() {

        ChainShape chainShape = new ChainShape();

        float[] verts = new float[10];

        verts[0] = 0;
        verts[1] = 0;
        verts[2] = chunksInMap * Consts.T_W_CH;
        verts[3] = 0;
        verts[5] = Consts.T_H_CH;
        verts[4] = chunksInMap * Consts.T_W_CH;
        verts[6] = 0;
        verts[7] = Consts.T_H_CH;
        verts[8] = 0;
        verts[9] = 0;

        chainShape.createChain(verts);
        terrainBody.createFixture(chainShape, 1);

    }

    private void cameraCenter() {
        Vector3 position = camera.position;
        position.x = camera.position.x  + (player.getLocation().x - camera.position.x) * .1f;
        position.y = camera.position.y  + (player.getLocation().y - camera.position.y) * .1f;
        camera.position.set(position);
        camera.update();

    }

    public Tile getTileAt(Vector2 vec) {
        int c = (int) (vec.x / Consts.T_W_CH);
        int x = (int) (vec.x - (c * Consts.T_W_CH));
        int y = (int) vec.y;
        WorldChunk wch = loadedChunks.get(c);
        return wch == null ? null : wch.getTerrain()[y][x];
    }

    public Tile getTileAt(int xG, int yG) {
        int c = xG / Consts.T_W_CH;
        int x = xG - (c * Consts.T_W_CH);
        WorldChunk wch = loadedChunks.get(c);
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

    public HashMap<Integer, WorldChunk> getChunks() {
        return loadedChunks;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public HashMap<Integer, Fixture> getFixturesID() {
        return fixturesID;
    }

    public List<LightSource> getActiveLights() {
        return activeLights;
    }

    public int getChunksInMap() {
        return chunksInMap;
    }
}
