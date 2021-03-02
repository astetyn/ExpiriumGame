package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.entity.MainClientPlayer;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.tiles.ClientTile;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Rain {

    private final static int spawnInterval = 2; // new drop every x millis
    private final static float velY = -20 * 0.001f; // meters per millisecond
    private final static int radius = 50;
    private final static int recalcInterval = 1000;

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

        long currentTime = System.currentTimeMillis();
        Vector2 center = player.getCenter();

        Iterator<RainDrop> it = drops.iterator();
        while(it.hasNext()) {
            RainDrop drop = it.next();
            int x = Math.max((int)drop.x - (int)center.x + radius, 0);
            x = Math.min(x, maxIndex);
            float y = drop.spawnY + (currentTime - drop.spawnTime) * velY;
            if(y < surface[x]) it.remove();
        }

        long diff = Math.min(currentTime - lastSpawn - spawnInterval, 1000);
        while(diff > 0) {
            drops.add(new RainDrop((float) (center.x - radius + Math.random() * (radius*2)), (float) (center.y + 20 + Math.random() * 10)));
            diff -= spawnInterval;
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
            float y = drop.spawnY + (System.currentTimeMillis() - drop.spawnTime) * velY;
            if(x >= left && x <= right && y >= bottom && y <= top) {
                batch.draw(Res.DAMAGE_OVERLAP, x, y, 0.1f, 0.2f);
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

        public final long spawnTime;
        public final float x, spawnY;

        public RainDrop(float x, float spawnY) {
            this.x = x;
            this.spawnY  = spawnY;
            spawnTime = System.currentTimeMillis();
        }
    }

}
