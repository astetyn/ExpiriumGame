package com.astetyne.expirium.main.world;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.entity.Entity;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.entity.MainPlayer;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.tiles.Tile;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameWorld {

    public static float PPM = 64;

    private final World b2dWorld;
    private final SpriteBatch batch;
    private final WorldChunk[] chunkArray;
    private final Body terrainBody;
    private final HashMap<Integer, Entity> entitiesID;
    private final List<Entity> entities;
    private MainPlayer player;
    private final OrthographicCamera camera;
    private final HashMap<Integer, Fixture> fixturesID;

    public GameWorld(int numberOfChunks) {

        this.batch = GameStage.get().getBatch();

        fixturesID = new HashMap<>();
        entitiesID = new HashMap<>();
        entities = new ArrayList<>();

        camera = new OrthographicCamera();

        b2dWorld = new World(new Vector2(0, -9.81f), false);

        chunkArray = new WorldChunk[numberOfChunks];

        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = b2dWorld.createBody(terrainDef);

        generateWorldBorders();

        resize();

    }

    public void postSetup() {

        PacketInputStream in = ExpiriumGame.get().getClientGateway().getIn();

        int pID = in.getInt();
        Vector2 loc = in.getVector();

        player = new MainPlayer(pID, loc);
        player.getBody().setTransform(loc, 0);

        int size = in.getInt();
        for(int i = 0; i < size; i++) {
            EntityType.getType(in.getInt()).initEntity();
        }

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
        for(WorldChunk chunk : chunkArray) {
            if(chunk == null) continue;
            Tile[][] terrain = chunk.getTerrain();
            int offset = chunk.getId()* Consts.T_W_CH;
            for(int i = 0; i < Consts.T_H_CH; i++) {
                for(int j = 0; j < Consts.T_W_CH; j++) {
                    Tile t = terrain[i][j];
                    if(GameStage.get().getInv().getMaterialSlot().isFocused()) {
                        player.getTilePlacer().render(t);
                        continue;
                    }
                    if(t.getType() != TileType.AIR) batch.draw(t.getTex(), j + offset, i, 1, 1);
                }
            }
        }

        // render entities
        for(Entity e : entities) {
            e.draw();
        }

        player.draw();
    }

    public void resize() {
        camera.setToOrtho(false, Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
        camera.update();
    }

    public void dispose() {
        b2dWorld.dispose();
    }

    public void onFeedChunkEvent() {

        PacketInputStream in = ExpiriumGame.get().getClientGateway().getIn();

        WorldChunk worldChunk = new WorldChunk(in);
        chunkArray[worldChunk.getId()] = worldChunk;
    }

    public void onDestroyChunkEvent() {

        PacketInputStream in = ExpiriumGame.get().getClientGateway().getIn();

        int chunkID = in.getInt();
        chunkArray[chunkID] = null;
    }

    public void onTileChange(PacketInputStream in) {

        int type = in.getInt();
        int c = in.getInt();
        int x = in.getInt();
        int y = in.getInt();

        chunkArray[c].getTerrain()[y][x].setType(TileType.getType(type));

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
            chunkArray[c].getTerrain()[y][x].setStability(stability);
        }
    }

    private void generateWorldBorders() {

        ChainShape chainShape = new ChainShape();

        float[] verts = new float[10];

        verts[0] = 0;
        verts[1] = 0;
        verts[2] = chunkArray.length * Consts.T_W_CH;
        verts[3] = 0;
        verts[5] = Consts.T_H_CH;
        verts[4] = chunkArray.length * Consts.T_W_CH;
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
        int chunk = (int) (vec.x / Consts.T_W_CH);
        int x = (int) (vec.x - (chunk * Consts.T_W_CH));
        int y = (int) vec.y;
        WorldChunk wch = chunkArray[chunk];
        return wch == null ? null : wch.getTerrain()[y][x];
    }

    public Tile getTileAt(int xG, int yG) {
        int chunk = xG / Consts.T_W_CH;
        int x = xG - (chunk * Consts.T_W_CH);
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

    public HashMap<Integer, Fixture> getFixturesID() {
        return fixturesID;
    }
}
