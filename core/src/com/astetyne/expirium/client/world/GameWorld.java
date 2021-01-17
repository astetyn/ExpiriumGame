package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.entity.Entity;
import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.entity.MainPlayer;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.tiles.BreakingTile;
import com.astetyne.expirium.client.tiles.Tile;
import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.api.world.inventory.ChosenSlot;
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

    public static float PPM = 32;

    private final SpriteBatch batch;
    private Tile[][] worldTerrain;
    private final HashMap<Integer, Entity> entitiesID;
    private final List<Entity> entities;
    private MainPlayer player;
    private final OrthographicCamera camera;
    private int terrainWidth, terrainHeight;
    private LightCalculator lightCalculator;

    public GameWorld() {

        this.batch = ExpiGame.get().getBatch();

        entitiesID = new HashMap<>();
        entities = new ArrayList<>();

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
                worldTerrain[i][j] = new Tile(TileType.AIR, (byte)0);
            }
        }

        lightCalculator = new LightCalculator(worldTerrain);

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
            e.move();
        }
        cameraCenter();
    }

    public void render() {

        batch.setProjectionMatrix(camera.combined);

        // tiles
        int renderOffsetX = (int) (Gdx.graphics.getWidth() / (PPM * 2)) + 2;
        int renderOffsetY = (int) (Gdx.graphics.getHeight() / (PPM * 2)) + 2;
        int left = (int) Math.max(camera.position.x - renderOffsetX, 0);
        int right = (int) Math.min(camera.position.x + renderOffsetX, terrainWidth);
        int down = (int) Math.max(camera.position.y - renderOffsetY, 0);
        int up = (int) Math.min(camera.position.y + renderOffsetY, terrainHeight);

        for(int i =  left; i < right; i++) {
            for(int j = down; j < up; j++) {
                Tile t = worldTerrain[i][j];
                if(t.getTypeFront() == TileType.AIR) {
                    continue;
                }
                if(GameScreen.get().getPlayerData().getHotSlotsData().getChosenSlot() == ChosenSlot.MATERIAL_SLOT) {
                    player.getTilePlacer().render(t, i, j);
                    continue;
                }
                batch.draw(t.getTypeFront().getTex(), i, j, 1, 1);
            }
        }

        // breaking anim
        for(BreakingTile bt : BreakingTile.getBreakingTiles().values()) {
            batch.draw(TileTexAnim.TILE_BREAK.getAnim().getKeyFrame(bt.getState()), bt.getLoc().x, bt.getLoc().y, 1, 1);
        }

        // entities + player
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

        // lightning
        // background must be also darkened during night, because light mask will light it
        for(int i =  left; i < right; i++) {
            for(int j = down; j < up; j++) {
                Tile t = worldTerrain[i][j];
                float light = t.getLocalLight();
                //float dayTime = GameScreen.get().getDayTime();
                float dayTime = 0;
                if(dayTime >= 0 && dayTime < 25) {
                    light = Math.max(light, t.getSkyLight() / 25f * dayTime + 2);
                }else if(dayTime >= 25 && dayTime < 600) {
                    light = Math.max(light, t.getSkyLight());
                }else if(dayTime >= 600 && dayTime < 625) {
                    light = Math.max(light, t.getSkyLight() / 25f * (625 - dayTime));
                }else {
                    light = Math.max(light, t.getSkyLight() * 0.1f); // moon sky lightning
                }
                batch.setColor(0, 0, 0, 1f / Consts.MAX_LIGHT_LEVEL * (Consts.MAX_LIGHT_LEVEL - light));
                batch.draw(TileTex.WHITE_TILE.getTex(), i, j, 1, 1);
            }
        }
        batch.setColor(Color.WHITE);

    }

    public void onFeedWorldEvent(PacketInputStream in) {

        int partHeight = in.getInt();
        int partNumber = in.getInt();

        int yOff = partNumber * partHeight;
        for(int i = 0; i < terrainWidth; i++) {
            for(int j = yOff; j < yOff + partHeight; j++) {
                Tile t = worldTerrain[i][j];
                TileType type = TileType.getType(in.getByte());
                byte stability = in.getByte();
                t.setTypeFront(type);
                t.setStability(stability);
            }
        }

        if(yOff + partHeight == terrainHeight) {
            lightCalculator.recalcSkyLights();
        }
    }

    public void onTileChange(PacketInputStream in) {

        TileType type = TileType.getType((byte) in.getInt());
        int x = in.getInt();
        int y = in.getInt();

        Tile t = worldTerrain[x][y];

        TileType oldType = t.getTypeFront();

        t.setTypeFront(type);

        lightCalculator.onTileChange(oldType, type, x, y);

    }

    public void onStabilityChange(PacketInputStream in) {
        int size = in.getInt();
        for(int i = 0; i < size; i++) {
            int x = in.getInt();
            int y = in.getInt();
            byte stability = in.getByte();
            worldTerrain[x][y].setStability(stability);
        }
    }

    public void onBreakingTile(PacketInputStream in) {
        int x = in.getInt();
        int y = in.getInt();
        float state = in.getFloat();
        Tile t = worldTerrain[x][y];
        if(state == -1) {
            BreakingTile.getBreakingTiles().remove(t);
            return;
        }
        if(BreakingTile.getBreakingTiles().containsKey(t)) {
            BreakingTile.getBreakingTiles().get(t).setState(state);
        }else {
            BreakingTile.getBreakingTiles().put(t, new BreakingTile(new IntVector2(x, y), state));
        }
    }

    private void cameraCenter() {
        Vector3 position = camera.position;
        Vector2 pCenter = player.getCenter();
        position.x = Math.max(camera.position.x  + (pCenter.x - camera.position.x) * .1f, camera.viewportWidth / 2);
        position.y = Math.max(camera.position.y  + (pCenter.y - camera.position.y) * .1f, camera.viewportHeight / 2);
        position.x = Math.min(position.x, terrainWidth - camera.viewportWidth / 2);
        position.y = Math.min(position.y, terrainHeight - camera.viewportHeight / 2);
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
}
