package com.astetyne.main.world;

import com.astetyne.main.Resources;
import com.astetyne.main.entity.*;
import com.astetyne.main.items.Item;
import com.astetyne.main.items.ItemType;
import com.astetyne.main.stages.GameStage;
import com.astetyne.main.utils.BodyEditorLoader;
import com.astetyne.main.utils.Constants;
import com.astetyne.main.world.tiles.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameWorld {

    public static float PPM = 32;

    private final World b2dWorld;
    private final SpriteBatch batch;
    private final WorldChunk[] chunkArray;
    private BodyEditorLoader loader;
    private final Body terrainBody;
    private final HashMap<Integer, Entity> entitiesID;
    private final List<Entity> entities;
    private final ExpiContactListener contactListener;
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
        //loader = new BodyEditorLoader(Gdx.files.internal("shapes.json"));
        chunkArray = new WorldChunk[numberOfChunks];

        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = b2dWorld.createBody(terrainDef);

        contactListener = new ExpiContactListener();
        b2dWorld.setContactListener(contactListener);

        generateWorldBorders();

        resize();

    }

    public void postSetup(ByteBuffer bb) {

        int pID = bb.getInt();
        Vector2 loc = new Vector2(bb.getFloat(), bb.getFloat());

        player = new MainPlayer(pID, loc);
        player.getBody().setTransform(loc, 0);

        int size = bb.getInt();

        for(int i = 0; i < size; i++) {

            int id = bb.getInt();
            EntityType type = EntityType.getType(bb.getInt());
            Vector2 loc2 = new Vector2(bb.getFloat(), bb.getFloat());

            if(type == EntityType.PLAYER) {
                if(id == player.getID()) continue;
                PlayerEntity pe = new PlayerEntity(id, loc2);
            }else if(type == EntityType.DROPPED_ITEM) {
                DroppedItemEntity die = new DroppedItemEntity(id, new Item(ItemType.DIRT, Resources.DIRT_TEXTURE), 0, loc2);
                //todo: spravit v packete list pre kazdy typ entity?
            }
        }

    }

    public void update() {

        for(Entity e : entities) {
            e.move();
        }

        player.update();

        b2dWorld.step(1/60f, 6, 2);

        cameraCenter();
    }

    public void render() {

        batch.setProjectionMatrix(camera.combined);

        // render world
        for(WorldChunk chunk : chunkArray) {
            if(chunk == null) continue;
            Tile[][] terrain = chunk.getTerrain();
            int offset = chunk.getId()*Constants.T_W_CH;
            for(int i = 0; i < Constants.T_H_CH; i++) {
                for(int j = 0; j < Constants.T_W_CH; j++) {
                    if(terrain[i][j].getType() != TileType.AIR) {
                        TextureRegion tex = terrain[i][j].getTexture();
                        batch.draw(tex, j + offset, i, 1, 1);
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
        camera.setToOrtho(false, Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
        camera.update();
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

    public void feedChunk(ByteBuffer bb) {

        WorldChunk worldChunk = new WorldChunk(bb);
        chunkArray[worldChunk.getId()] = worldChunk;

        int size = bb.getInt();
        EdgeShape shape = new EdgeShape();
        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.friction = 0.2f;
        def.filter.categoryBits = Constants.DEFAULT_BIT;

        for(int i = 0; i < size; i++) {
            int id = bb.getInt();
            shape.set(bb.getFloat(), bb.getFloat(),bb.getFloat(), bb.getFloat());
            Fixture f = terrainBody.createFixture(def);
            fixturesID.put(id, f);
        }
    }

    private void generateWorldBorders() {

        ChainShape chainShape = new ChainShape();

        float[] verts = new float[10];

        verts[0] = 0;
        verts[1] = 0;
        verts[2] = chunkArray.length * Constants.T_W_CH;
        verts[3] = 0;
        verts[5] = Constants.T_H_CH;
        verts[4] = chunkArray.length * Constants.T_W_CH;
        verts[6] = 0;
        verts[7] = Constants.T_H_CH;
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

    public HashMap<Integer, Fixture> getFixturesID() {
        return fixturesID;
    }
}
