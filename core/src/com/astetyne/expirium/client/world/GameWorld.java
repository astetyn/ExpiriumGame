package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.animation.WorldAnimationManager;
import com.astetyne.expirium.client.entity.Entity;
import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.entity.MainPlayer;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.tiles.BreakingTile;
import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.tiles.Tile;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.WorldInputListener;
import com.astetyne.expirium.server.core.world.inventory.ChosenSlot;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameWorld {

    public static float PPM = 32; // pixel per meter when zoom == 1

    private Tile[][] terrain;
    private final HashMap<Integer, Entity> entitiesID;
    private final List<Entity> entities;
    private MainPlayer player;
    private final OrthographicCamera camera;
    private int terrainWidth, terrainHeight;
    private LightCalculator lightCalculator;
    private final WorldInputListener worldInputListener;
    private final List<BreakingTile> breakingTiles;
    private final WorldAnimationManager animationManager;

    Color[] stabColors = new Color[] {
            new Color(0.9f, 0f, 0f, 1),
            new Color(0.9f, 0.3f, 0f, 1),
            new Color(0.9f, 0.6f, 0f, 1),
            new Color(0.9f, 0.9f, 0f, 1),
            new Color(0.9f, 1f, 0.2f, 1),
            new Color(0.8f, 1f, 0.2f, 1)};

    public GameWorld() {

        entitiesID = new HashMap<>();
        entities = new ArrayList<>();

        worldInputListener = new WorldInputListener(this);
        GameScreen.get().getMultiplexer().addProcessor(worldInputListener);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
        camera.update();

        breakingTiles = new ArrayList<>();
        animationManager = new WorldAnimationManager();
    }

    public void loadData(PacketInputStream in) {

        terrainWidth = in.getInt();
        terrainHeight = in.getInt();

        terrain = new Tile[terrainWidth][terrainHeight];

        for(int i = 0; i < terrainWidth; i++) {
            for(int j = 0; j < terrainHeight; j++) {
                terrain[i][j] = new Tile(Material.AIR, (byte)0);
            }
        }

        lightCalculator = new LightCalculator(terrain);

        int pID = in.getInt();
        Vector2 loc = in.getVector();
        player = new MainPlayer(pID, loc);

        int size = in.getInt();
        for(int i = 0; i < size; i++) {
            EntityType.getType(in.getInt()).initEntity(in);
        }

    }

    public void update() {
        for(Entity e : entities) {
            if(e.isActive()) e.update();
        }
        cameraCenter();
        animationManager.update();
    }

    public void draw(SpriteBatch batch) {

        batch.setProjectionMatrix(camera.combined);

        // tiles
        int renderOffsetX = (int) (Gdx.graphics.getWidth() / (PPM / camera.zoom * 2)) + 2;
        int renderOffsetY = (int) (Gdx.graphics.getHeight() / (PPM / camera.zoom * 2)) + 2;
        int left = (int) Math.max(camera.position.x - renderOffsetX, 0);
        int right = (int) Math.min(camera.position.x + renderOffsetX, terrainWidth);
        int down = (int) Math.max(camera.position.y - renderOffsetY, 0);
        int up = (int) Math.min(camera.position.y + renderOffsetY, terrainHeight);

        for(int i =  left; i < right; i++) {
            for(int j = down; j < up; j++) {
                Tile t = terrain[i][j];

                if(t.hasBackWall()) {
                    float b = 1f / Consts.MAX_LIGHT_LEVEL * t.getLight();
                    batch.setColor(b,b,b,1);
                    batch.draw(TileTex.BACK_WALL.getTex(), i, j, 1, 1);
                }

                if(t.getMaterial() == Material.AIR) continue;

                float b = 1f / Consts.MAX_LIGHT_LEVEL * t.getLight();
                //float b = 1;
                batch.setColor(b,b,b,1);

                ChosenSlot slot = GameScreen.get().getPlayerData().getHotSlotsData().getChosenSlot();
                if(slot == ChosenSlot.MATERIAL_SLOT && GameScreen.get().isBuildViewActive()) {
                    batch.setColor(stabColors[t.getStability()-1]);
                    batch.draw(TileTex.WHITE_TILE.getTex(), i, j, 1, 1);
                }else {
                    batch.draw(t.getMaterial().getTex(), i, j, 1, 1);
                }
            }
        }
        batch.setColor(Color.WHITE);

        // breaking anim
        for(BreakingTile bt : breakingTiles) {
            batch.draw(TileTexAnim.TILE_BREAK.getAnim().getKeyFrame(bt.state), bt.x, bt.y, 1, 1);
        }

        // entities + player
        for(Entity e : entities) {
            if(!e.isActive()) continue;
            // this is for entities from entities.atlas
            if(e.getType() != EntityType.DROPPED_ITEM) {
                float b = 1f / Consts.MAX_LIGHT_LEVEL * e.getCenterTile().getLight();
                batch.setColor(b,b,b,1);
                e.draw(batch);
            }
        }

        for(Entity e : entities) {
            if(!e.isActive()) continue;
            // this is for dropped items from gui.atlas
            if(e.getType() == EntityType.DROPPED_ITEM) {
                float b = 1f / Consts.MAX_LIGHT_LEVEL * e.getCenterTile().getLight();
                batch.setColor(b,b,b,1);
                e.draw(batch);
            }
        }

        animationManager.draw(batch);
    }

    public void onServerTick() {
        breakingTiles.clear();
        for(Entity e : entities) {
            e.setActive(false);
        }
    }

    public void onFeedWorldEvent(PacketInputStream in) {

        int partHeight = in.getInt();
        int partNumber = in.getInt();

        int yOff = partNumber * partHeight;
        for(int i = 0; i < terrainWidth; i++) {
            for(int j = yOff; j < yOff + partHeight; j++) {
                Tile t = terrain[i][j];
                Material type = Material.getMaterial(in.getByte());
                byte stability = in.getByte();
                boolean backWall = in.getBoolean();
                t.setMaterial(type);
                t.setStability(stability);
                t.setBackWall(backWall);
            }
        }

        if(yOff + partHeight == terrainHeight) {
            lightCalculator.recalcAllTiles();
        }
    }

    public void onTileChange(PacketInputStream in) {

        Material type = Material.getMaterial((byte) in.getInt());
        int x = in.getInt();
        int y = in.getInt();

        Tile t = terrain[x][y];

        t.setMaterial(type);

        lightCalculator.onTileChange(x, y);

    }

    public void onStabilityChange(PacketInputStream in) {
        int size = in.getInt();
        for(int i = 0; i < size; i++) {
            int x = in.getInt();
            int y = in.getInt();
            byte stability = in.getByte();
            terrain[x][y].setStability(stability);
        }
    }

    public void onBreakingTile(PacketInputStream in) {
        int x = in.getInt();
        int y = in.getInt();
        float state = in.getFloat();
        breakingTiles.add(new BreakingTile(x, y, state));
    }

    public void onBackWallsChange(PacketInputStream in) {

        int size = in.getInt();
        for(int i = 0; i < size; i++) {
            int x = in.getInt();
            int y = in.getInt();
            boolean has = in.getBoolean();
            terrain[x][y].setBackWall(has);
        }
    }

    private void cameraCenter() {
        Vector3 position = camera.position;
        Vector2 pCenter = player.getCenter();
        position.x = Math.max(camera.position.x  + (pCenter.x - camera.position.x) * 0.1f, camera.viewportWidth * camera.zoom / 2);
        position.y = Math.max(camera.position.y  + (pCenter.y - camera.position.y) * 0.1f, camera.viewportHeight * camera.zoom / 2);
        position.x = Math.min(position.x, terrainWidth - camera.viewportWidth * camera.zoom / 2);
        position.y = Math.min(position.y, terrainHeight - camera.viewportHeight * camera.zoom / 2);
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

    public int getTerrainWidth() {
        return terrainWidth;
    }

    public int getTerrainHeight() {
        return terrainHeight;
    }

    public Tile getTileAt(float x, float y) {
        if(x < 0) x = 0;
        if(y < 0) y = 0;
        if(x >= terrainWidth) x = terrainWidth-1;
        if(y >= terrainHeight) y = terrainHeight-1;
        return terrain[(int)x][(int)y];
    }

    public WorldAnimationManager getAnimationManager() {
        return animationManager;
    }
}
