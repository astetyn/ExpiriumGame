package com.astetyne.expirium.main.world;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.entity.Entity;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.entity.MainPlayer;
import com.astetyne.expirium.main.screens.GameScreen;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameWorld {

    public static float PPM = 45;

    private final SpriteBatch batch;
    private Tile[][] worldTerrain;
    private final HashMap<Integer, Entity> entitiesID;
    private final List<Entity> entities;
    private MainPlayer player;
    private final OrthographicCamera camera;
    private final FrameBuffer lightsFrameBuffer;
    private final List<LightSource> activeLights;
    private final Matrix4 screenMatrix;
    private int terrainWidth, terrainHeight;
    private final HashMap<Tile, Float> breakingTiles;

    public GameWorld() {

        this.batch = ExpiGame.get().getBatch();

        entitiesID = new HashMap<>();
        entities = new ArrayList<>();
        activeLights = new ArrayList<>();
        breakingTiles = new HashMap<>();

        screenMatrix = new Matrix4().setToOrtho2D(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        lightsFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 512, 512, false);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
        camera.update();
    }

    public void loadData(PacketInputStream in) {

        terrainWidth = in.getInt();
        terrainHeight = in.getInt();

        worldTerrain = new Tile[terrainWidth][terrainHeight];

        for(int i = 0; i < terrainWidth; i++) {
            for(int j = 0; j < terrainHeight; j++) {
                worldTerrain[i][j] = new Tile(TileType.AIR, i, j, 0);
            }
        }

        int pID = in.getInt();
        Vector2 loc = in.getVector();
        player = new MainPlayer(pID, loc);

        int size = in.getInt();
        for(int i = 0; i < size; i++) {
            EntityType.getType(in.getInt()).initEntity();
        }

    }

    public void update() {
        for(Entity e : entities) {
            e.move();
        }
        cameraCenter();
    }

    public void render() {

        batch.setProjectionMatrix(camera.combined);

        // render world
        int renderOffsetX = (int) (Gdx.graphics.getWidth() / (PPM * 2)) + 2;
        int renderOffsetY = (int) (Gdx.graphics.getHeight() / (PPM * 2)) + 2;
        int left = (int) Math.max(player.getCenter().x - renderOffsetX, 0);
        int right = (int) Math.min(player.getCenter().x + renderOffsetX, terrainWidth);
        int down = (int) Math.max(player.getCenter().y - renderOffsetY, 0);
        int up = (int) Math.min(player.getCenter().y + renderOffsetY, terrainHeight);

        for(int i =  left; i < right; i++) {
            for(int j = down; j < up; j++) {
                Tile t = worldTerrain[i][j];
                if(t.getType() == TileType.AIR) continue;

                if(GameScreen.get().getInventoryHandler().getHotSlotsData().getChosenSlot() == ChosenSlot.MATERIAL_SLOT) {
                    player.getTilePlacer().render(t);
                    continue;
                }
                batch.draw(t.getTex(), i, j, 1, 1);
            }
        }

        for(Map.Entry<Tile, Float> entry : breakingTiles.entrySet()) {
            Tile tile = entry.getKey();
            float state = entry.getValue();
            float durability = state / tile.getType().getBreakTime();
            batch.draw(Res.TILE_BREAK_ANIM.getKeyFrame(durability), tile.getX(), tile.getY(), 1, 1);
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

        Gdx.gl.glClearColor(0f,0f,0f,0f); // ambient color
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

    public void dispose() { }

    public void onFeedWorldEvent(PacketInputStream in) {

        int partHeight = in.getInt();
        int partNumber = in.getInt();

        int yOff = partNumber * partHeight;
        for(int i = 0; i < terrainWidth; i++) {
            for(int j = yOff; j < yOff + partHeight; j++) {
                Tile t = worldTerrain[i][j];
                TileType type = TileType.getType(in.getByte());
                int stability = in.getByte();
                t.setType(type);
                t.setStability(stability);
            }
        }
    }

    public void onTileChange(PacketInputStream in) {

        TileType type = TileType.getType(in.getInt());
        int x = in.getInt();
        int y = in.getInt();

        Tile t = worldTerrain[x][y];

        for(LightSource ls : t.getAttachedLights()) {
            activeLights.remove(ls);
        }
        t.getAttachedLights().clear();

        t.setType(type);

        if(type == TileType.CAMPFIRE_BIG) {
            LightSource ls = new LightSource(LightType.TRANSP_SPHERE_MEDIUM, new Vector2(x + 0.5f, y+0.5f));
            activeLights.add(ls);
            t.getAttachedLights().add(ls);
        }

    }

    public void onStabilityChange(PacketInputStream in) {
        int size = in.getInt();
        for(int i = 0; i < size; i++) {
            int x = in.getInt();
            int y = in.getInt();
            int stability = in.getInt();
            worldTerrain[x][y].setStability(stability);
        }
    }

    public void onBreakingTile(PacketInputStream in) {
        int x = in.getInt();
        int y = in.getInt();
        float state = in.getFloat();
        Tile t = worldTerrain[x][y];
        if(state == -1) {
            breakingTiles.remove(t);
            return;
        }
        breakingTiles.put(t, state);
    }

    private void cameraCenter() {
        Vector3 position = camera.position;
        position.x = camera.position.x  + (player.getLocation().x - camera.position.x) * .1f;
        position.y = camera.position.y  + (player.getLocation().y - camera.position.y) * .1f;
        camera.position.set(position);
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public HashMap<Integer, Entity> getEntitiesID() {
        return entitiesID;
    }

    public MainPlayer getPlayer() {
        return player;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<LightSource> getActiveLights() {
        return activeLights;
    }

    public int getTerrainWidth() {
        return terrainWidth;
    }

    public int getTerrainHeight() {
        return terrainHeight;
    }
}
