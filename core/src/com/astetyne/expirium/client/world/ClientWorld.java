package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.animation.WaterAnimationManager;
import com.astetyne.expirium.client.animation.WorldAnimationManager;
import com.astetyne.expirium.client.entity.ClientEntity;
import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.entity.MainClientPlayer;
import com.astetyne.expirium.client.resources.GuiRes;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.tiles.BreakingTile;
import com.astetyne.expirium.client.tiles.ClientTile;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.WorldInputListener;
import com.astetyne.expirium.server.core.world.inventory.ChosenSlot;
import com.astetyne.expirium.server.core.world.tile.Material;
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

public class ClientWorld {

    public static float PPM = 32; // pixel per meter when zoom == 1

    private ClientTile[][] terrain;
    private final HashMap<Short, ClientEntity> entitiesID;
    private final List<ClientEntity> entities;
    private MainClientPlayer player;
    private final OrthographicCamera camera;
    private int terrainWidth, terrainHeight;
    private LightCalculator lightCalculator;
    private final WorldInputListener worldInputListener;
    private final List<BreakingTile> breakingTiles;
    private final WorldAnimationManager animationManager;
    private final WaterAnimationManager waterManager;

    Color[] stabColors = new Color[] {
            new Color(0.9f, 0f, 0f, 1),
            new Color(0.9f, 0.3f, 0f, 1),
            new Color(0.9f, 0.6f, 0f, 1),
            new Color(0.9f, 0.9f, 0f, 1),
            new Color(0.9f, 1f, 0.2f, 1),
            new Color(0.7f, 1f, 0.2f, 1),
            new Color(0.5f, 1f, 0.2f, 1),
            new Color(0.2f, 1f, 0.2f, 1),
            new Color(0.2f, 1f, 0.4f, 1),
            new Color(0.2f, 1f, 0.6f, 1),
            new Color(0.2f, 1f, 0.8f, 1),
            new Color(0.2f, 1f, 1f, 1),
            new Color(0.2f, 0.7f, 1f, 1),
            new Color(0.2f, 0.4f, 1f, 1),
            };

    public ClientWorld(PacketInputStream in) {

        entitiesID = new HashMap<>();
        entities = new ArrayList<>();

        worldInputListener = new WorldInputListener(this);
        GameScreen.get().getMultiplexer().addProcessor(worldInputListener);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
        camera.update();

        breakingTiles = new ArrayList<>();
        animationManager = new WorldAnimationManager();

        terrainWidth = in.getInt();
        terrainHeight = in.getInt();

        terrain = new ClientTile[terrainWidth][terrainHeight];

        for(int i = 0; i < terrainWidth; i++) {
            for(int j = 0; j < terrainHeight; j++) {
                terrain[i][j] = new ClientTile(Material.AIR, (byte)0);
            }
        }

        lightCalculator = new LightCalculator(terrain);
        waterManager = new WaterAnimationManager(terrain, terrainWidth, terrainHeight);

        short pID = in.getShort();
        Vector2 loc = in.getVector();
        player = new MainClientPlayer(this, pID, loc);

        int size = in.getInt();
        for(int i = 0; i < size; i++) {
            EntityType.getType(in.getInt()).initEntity(this, in);
        }

    }

    public void update() {
        for(ClientEntity e : entities) {
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
                ClientTile t = terrain[i][j];

                if(t.hasBackWall()) {
                    float b = Consts.MAX_LIGHT_LEVEL_INVERTED * t.getLight();
                    batch.setColor(b,b,b,1);
                    batch.draw(TileTex.BACK_WALL.getTex(), i, j, 1, 1);
                }

                if(t.getMaterial() == Material.AIR) continue;

                // stability view
                ChosenSlot slot = GameScreen.get().getPlayerData().getHotSlotsData().getChosenSlot();
                if(slot == ChosenSlot.MATERIAL_SLOT && GameScreen.get().isBuildViewActive()) {
                    if(t.getStability() < 1 || t.getStability() >= stabColors.length) {
                        GuiRes.DEBUG.getDrawable().draw(batch, i, j, 1, 1);
                        continue;
                    }
                    batch.setColor(stabColors[t.getStability() - 1]);
                    batch.draw(TileTex.WHITE_TILE.getTex(), i, j, 1, 1);
                    continue;
                }

                // normal view
                float b = Consts.MAX_LIGHT_LEVEL_INVERTED * t.getLight();
                //float b = 1;
                batch.setColor(b,b,b,1);
                batch.draw(t.getMaterial().getTex(), i, j, 1, 1);
            }
        }
        batch.setColor(Color.WHITE);

        // breaking anim
        for(BreakingTile bt : breakingTiles) {
            batch.draw(TileTexAnim.TILE_BREAK.getAnim().getKeyFrame(bt.state), bt.x, bt.y, 1, 1);
        }

        // entities + player
        for(ClientEntity e : entities) {
            if(!e.isActive()) continue;
            // this is for entities from entities.atlas
            if(e.getType() != EntityType.DROPPED_ITEM) {
                float b = Consts.MAX_LIGHT_LEVEL_INVERTED * e.getCenterTile().getLight();
                batch.setColor(b,b,b,1);
                e.draw(batch);
            }
        }

        for(ClientEntity e : entities) {
            if(!e.isActive()) continue;
            // this is for dropped items from gui.atlas
            if(e.getType() == EntityType.DROPPED_ITEM) {
                float b = Consts.MAX_LIGHT_LEVEL_INVERTED * e.getCenterTile().getLight();
                batch.setColor(b,b,b,1);
                e.draw(batch);
            }
        }
        animationManager.draw(batch);

        for(int i =  left; i < right; i++) {
            for(int j = down; j < up; j++) {
                ClientTile t = terrain[i][j];
                float b = Consts.MAX_LIGHT_LEVEL_INVERTED * t.getLight();
                batch.setColor(b,b,b,1);
                waterManager.draw(batch, t, i, j);
            }
        }
        batch.setColor(Color.WHITE);
    }

    public void onServerTick() {
        breakingTiles.clear();
        for(ClientEntity e : entities) {
            e.setActive(false);
        }
    }

    public void onFeedWorldEvent(PacketInputStream in) {

        int layer = in.getInt();

        for(int i = 0; i < terrainWidth; i++) {
            ClientTile t = terrain[i][layer];
            Material type = Material.getMaterial(in.getByte());
            byte stability = in.getByte();
            boolean backWall = in.getBoolean();
            byte waterLevel = in.getByte();
            t.setMaterial(type);
            t.setStability(stability);
            t.setBackWall(backWall);
            t.setWaterLevel(waterLevel);
        }

        if(layer == terrainHeight-1) {
            lightCalculator.recalcAllTiles();
        }
    }

    public void onTileChange(PacketInputStream in) {

        Material type = Material.getMaterial((byte) in.getInt());
        int x = in.getInt();
        int y = in.getInt();

        ClientTile t = terrain[x][y];

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

    public void onWaterPacket(PacketInputStream in) {
        int size = in.getInt();
        for(int i = 0; i < size; i++) {
            int x = in.getInt();
            int y = in.getInt();
            byte waterLevel = in.getByte();
            terrain[x][y].setWaterLevel(waterLevel);
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

    public HashMap<Short, ClientEntity> getEntitiesID() {
        return entitiesID;
    }

    public MainClientPlayer getPlayer() {
        return player;
    }

    public List<ClientEntity> getEntities() {
        return entities;
    }

    public int getTerrainWidth() {
        return terrainWidth;
    }

    public int getTerrainHeight() {
        return terrainHeight;
    }

    public ClientTile getTileAt(float x, float y) {
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
