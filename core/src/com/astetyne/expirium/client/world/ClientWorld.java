package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.ExpiGame;
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
import com.astetyne.expirium.server.core.world.WeatherType;
import com.astetyne.expirium.server.core.world.tile.Material;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientWorld {

    private final ClientTile[][] terrain;
    private final HashMap<Short, ClientEntity> entitiesID;
    private final List<ClientEntity> entities;
    private final MainClientPlayer player;
    private final OrthographicCamera camera;
    private final int terrainWidth, terrainHeight;
    private final LightCalculator lightCalculator;
    private final WorldInputListener worldInputListener;
    private final List<BreakingTile> breakingTiles;
    private final WorldAnimationManager animationManager;
    private final WaterAnimationManager waterManager;
    private final Rain rain;
    private final GameScreen game;

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

    public ClientWorld(GameScreen game, PacketInputStream in) {

        this.game = game;

        entitiesID = new HashMap<>();
        entities = new ArrayList<>();

        worldInputListener = new WorldInputListener(game, this);
        game.getMultiplexer().addProcessor(worldInputListener);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Consts.TPW, Consts.TPH);
        camera.update();

        breakingTiles = new ArrayList<>();
        animationManager = new WorldAnimationManager();

        terrainWidth = in.getInt();
        terrainHeight = in.getInt();

        game.setWeather(WeatherType.get(in.getByte()));

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
        player = new MainClientPlayer(this, pID, loc, ExpiGame.get().getCharacter());

        rain = new Rain(player, terrain, terrainWidth, terrainHeight);

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
        if(game.getWeather() == WeatherType.RAIN) {
            rain.update();
        }
    }

    public void draw(SpriteBatch batch) {

        batch.setProjectionMatrix(camera.combined);

        // tiles
        float renderOffsetX = camera.zoom * Consts.TPW / 2 + 2;
        float renderOffsetY = camera.zoom * Consts.TPH / 2 + 2;
        int left = (int) Math.max(camera.position.x - renderOffsetX, 0);
        int right = (int) Math.min(camera.position.x + renderOffsetX, terrainWidth);
        int bottom = (int) Math.max(camera.position.y - renderOffsetY, 0);
        int top = (int) Math.min(camera.position.y + renderOffsetY, terrainHeight);

        boolean stabilityViewActive = game.isBuildViewActive();

        for(int i =  left; i < right; i++) {
            for(int j = bottom; j < top; j++) {
                ClientTile t = terrain[i][j];

                if(t.hasBackWall()) {
                    setProperColor(batch, t);
                    batch.draw(TileTex.BACK_WALL.getTex(), i, j, 1, 1);
                }

                if(t.getMaterial() == Material.AIR) continue;

                // stability view
                if(stabilityViewActive) {
                    if(t.getStability() < 1 || t.getStability() >= stabColors.length) {
                        batch.setColor(Color.WHITE);
                        GuiRes.DEBUG.getDrawable().draw(batch, i, j, 1, 1);
                        continue;
                    }
                    batch.setColor(stabColors[t.getStability() - 1]);
                    batch.draw(TileTex.WHITE_TILE.getTex(), i, j, 1, 1);
                    continue;
                }

                // normal view
                setProperColor(batch, t);
                batch.draw(t.getMaterial().getTex(), i, j, 1, 1);
            }
        }
        batch.setColor(Color.WHITE);

        // breaking anim
        for(BreakingTile bt : breakingTiles) {
            batch.draw(TileTexAnim.TILE_BREAK.getAnim().getKeyFrame(bt.state), bt.x, bt.y, 1, 1);
        }

        // entities
        for(ClientEntity e : entities) {
            if(!e.isActive()) continue;
            setProperColor(batch, e.getCenterTile());
            e.draw(batch);
        }

        // water
        for(int i =  left; i < right; i++) {
            for(int j = bottom; j < top; j++) {
                ClientTile t = terrain[i][j];
                if(t.getWaterLevel() == 0) continue;
                setProperColor(batch, t);
                waterManager.draw(batch, t, i, j);
            }
        }
        animationManager.draw(batch);

        batch.setColor(Color.WHITE);

        if(game.getWeather() == WeatherType.RAIN) {
            rain.draw(batch, left, right, top, bottom);
        }
    }

    private void setProperColor(SpriteBatch batch, ClientTile t) {
        float b = Consts.MAX_LIGHT_LEVEL_INVERTED * t.getLight(game.getTime(), game.getWeather());
        batch.setColor(b,b,b,1);
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
            Material type = Material.get(in.getByte());
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

        Material type = Material.get((byte) in.getInt());
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
