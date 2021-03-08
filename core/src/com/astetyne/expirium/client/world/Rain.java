package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.entity.MainClientPlayer;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.tiles.ClientTile;
import com.astetyne.expirium.client.utils.Consts;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Rain {

    private final static int spawnInterval = 8; // new drop every x millis
    private final static float velY = -25; // meters per second
    private final static int radius = (int) (Consts.MIN_ZOOM * Consts.TPW / 2) + 4;
    private final static int recalcInterval = 1000; // how often should be surface recalculated in millis

    private final MainClientPlayer player;
    private final int width, height;
    private final ClientTile[][] terrain;
    private final int[] surface;
    private final List<RainDrop> drops;
    private long lastSpawn, lastRecalc;
    private final int maxIndex;

    public Rain(MainClientPlayer player, ClientTile[][] terrain, int width, int height) {
        this.player = player;
        this.width = width;
        this.height = height;
        this.terrain = terrain;
        surface = new int[radius*2];
        drops = new ArrayList<>();
        lastSpawn = System.currentTimeMillis();
        lastRecalc = 0;
        maxIndex = radius*2-1;
    }

    public void update() {

        float delta = Gdx.graphics.getDeltaTime();
        float yAdd = delta * velY;

        long currentTime = System.currentTimeMillis();
        Vector2 center = player.getCenter();
        int precalcX = (int) center.x - radius;

        // this is the fastest method I was able to achieve
        Iterator<RainDrop> it = drops.iterator();
        while(it.hasNext()) {
            RainDrop drop = it.next();
            int x = Math.max(drop.xTile - precalcX, 0);
            x = Math.min(x, maxIndex);
            drop.y += yAdd;
            if(drop.y < surface[x]) it.remove();
        }

        int toSpawn = Math.min((int)((currentTime - lastSpawn)/spawnInterval), 10); // max number of new drops to prevent death loop
        for(int i = 0; i < toSpawn; i++) {
            drops.add(new RainDrop((float) (center.x - radius + Math.random() * (radius*2)), (float) (center.y + 20 + Math.random() * 10)));
            lastSpawn = currentTime;
        }

        if(lastRecalc + recalcInterval < currentTime) {
            recalcSurface();
            lastRecalc = currentTime;
        }
    }

    public void draw(SpriteBatch batch, int left, int right, int top, int bottom) {
        for(RainDrop drop : drops) {
            float x = drop.x;
            float y = drop.y;
            if(x >= left && x <= right && y >= bottom && y <= top) {
                batch.draw(Res.RAIN_DROP, x, y, 0.1f, 0.8f);
            }
        }
    }

    private void recalcSurface() {
        Vector2 center = player.getCenter();
        int left =  Math.max((int)(center.x - radius), 0);
        int right =  Math.min((int)(center.x + radius), width - 1);
        for(int x = left; x < right; x++) {
            for(int y = height - 1; y >= 0; y--) {
                ClientTile t = terrain[x][y];
                if(t.getMaterial().isWatertight() || t.getWaterLevel() > 0) {
                    surface[x - left] = y + 1;
                    break;
                }
            }
        }
    }

    public static class RainDrop {

        public final float x;
        public float y;
        public final int xTile;

        public RainDrop(float x, float y) {
            this.x = x;
            xTile = (int)x;
            this.y = y;
        }
    }

}
