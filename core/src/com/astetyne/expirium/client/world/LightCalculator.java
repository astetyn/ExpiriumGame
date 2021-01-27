package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.tiles.Tile;
import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.resources.TileFix;

import java.util.HashMap;
import java.util.HashSet;

public class LightCalculator {

    private final Tile[][] terrain;
    private final int w, h;
    private final HashMap<Tile, LightSource> lightSources;
    private final HashSet<LightSource> invalidedSources;

    public LightCalculator(Tile[][] terrain) {
        this.terrain = terrain;
        w = terrain.length;
        h = terrain[0].length;
        lightSources = new HashMap<>();
        invalidedSources = new HashSet<>();
    }

    public void recalcSkyLights() {
        for(int x = 0; x < w; x++) {
            recalcSkyLightHardColumn(x);
        }
        for(int x = 0; x < w; x++) {
            recalcSkyLightSoftColumn(x);
        }
    }

    private void recalcSkyLightHardColumn(int c) {

        // direct sun
        int firstNotDirect = -1;
        for(int i = h - 1; i >= 0; i--) {
            Tile t = terrain[c][i];
            t.setSkyLight(Consts.MAX_LIGHT_LEVEL);
            if(t.getTypeFront().getTileFix() != TileFix.SOFT) {
                firstNotDirect = i-1;
                break;
            }
        }

        // tiles under
        for(int i = firstNotDirect; i >= 0; i--) {
            Tile t = terrain[c][i];
            t.setSkyLight((byte) (Math.max(Consts.MAX_LIGHT_LEVEL - (firstNotDirect - i + 1) * Consts.SKY_LIGHT_DECREASE, 0)));
        }
    }

    private void recalcSkyLightSoftColumn(int c) {
        for(int i = h - 1; i >= 0; i--) {
            Tile t = terrain[c][i];
            if(t.getSkyLight() == 0) continue;
            spreadSkyLightFrom(t, c, i);
        }
    }

    private void spreadSkyLightFrom(Tile t, int x, int y) {

        int newLight = t.getSkyLight()-Consts.SKY_LIGHT_DECREASE;

        if(x != 0) { // left
            Tile t2 = terrain[x-1][y];
            if(t2.getSkyLight() < newLight) {
                t2.setSkyLight((byte) newLight);
                spreadSkyLightFrom(t2, x-1, y);
            }
        }
        if(x != w-1) { // right
            Tile t2 = terrain[x+1][y];
            if(t2.getSkyLight() < newLight) {
                t2.setSkyLight((byte) newLight);
                spreadSkyLightFrom(t2, x+1, y);
            }
        }
        if(y != h-1) { // top
            Tile t2 = terrain[x][y+1];
            if(t2.getSkyLight() < newLight) {
                t2.setSkyLight((byte) newLight);
                spreadSkyLightFrom(t2, x, y+1);
            }
        }
        if(y != 0) { // bottom
            Tile t2 = terrain[x][y-1];
            if(t2.getSkyLight() < newLight) {
                t2.setSkyLight((byte) newLight);
                spreadSkyLightFrom(t2, x, y-1);
            }
        }
    }

    private void calcTileLocalLights(int x, int y) {

        Tile t = terrain[x][y];

        int newLight = t.getLocalLight()-1;

        if(x != 0) { // left
            Tile t2 = terrain[x-1][y];
            if(t2.getLocalLight() < newLight) {
                t2.setLocalLight((byte) newLight);
                calcTileLocalLights(x-1, y);
            }
        }
        if(x != w-1) { // right
            Tile t2 = terrain[x+1][y];
            if(t2.getLocalLight() < newLight) {
                t2.setLocalLight((byte) newLight);
                calcTileLocalLights(x+1, y);
            }
        }
        if(y != h-1) { // top
            Tile t2 = terrain[x][y+1];
            if(t2.getLocalLight() < newLight) {
                t2.setLocalLight((byte) newLight);
                calcTileLocalLights(x, y+1);
            }
        }
        if(y != 0) { // bottom
            Tile t2 = terrain[x][y-1];
            if(t2.getLocalLight() < newLight) {
                t2.setLocalLight((byte) newLight);
                calcTileLocalLights(x, y-1);
            }
        }
    }

    public void onTileChange(TileType from, TileType to, int x, int y) {

        Tile t = terrain[x][y];

        int left = Math.max(x - Consts.MAX_LIGHT_LEVEL / Consts.SKY_LIGHT_DECREASE, 0);
        int right = Math.min(x + Consts.MAX_LIGHT_LEVEL / Consts.SKY_LIGHT_DECREASE, w-1);

        for(int c = left; c < right; c++) {
            recalcSkyLightHardColumn(c);
        }
        for(int c = Math.max(left-1,0); c < Math.min(right+1, w-1); c++) {
            recalcSkyLightSoftColumn(c);
        }

        if(lightSources.containsKey(t)) {
            invalidedSources.add(lightSources.get(t));
            lightSources.remove(t);
        }

        if(to == TileType.CAMPFIRE_BIG) {
            lightSources.put(t, new LightSource((byte)10, new IntVector2(x, y)));
        }else if(to == TileType.CAMPFIRE_SMALL) {
            lightSources.put(t, new LightSource((byte)5, new IntVector2(x, y)));
        }

        recalcAllLights();
    }

    private void recalcAllLights() {

        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                terrain[x][y].setLocalLight((byte)0);
            }
        }

        for(LightSource source : invalidedSources) {

            int x = source.getLoc().x;
            int y = source.getLoc().y;
            int radius = source.getRadius();

            int left = Math.max(x-radius, 0);
            int right = Math.min(x+radius, w);
            int top = Math.min(y+radius, h);
            int bottom = Math.max(y-radius, 0);

            for(int i = left; i < right; i++) {
                for(int j = bottom; j < top; j++) {
                    terrain[i][j].setLocalLight((byte)0);
                }
            }
        }

        invalidedSources.clear();

        for(LightSource source : lightSources.values()) {
            int x = source.getLoc().x;
            int y = source.getLoc().y;
            byte radius = source.getRadius();

            terrain[x][y].setLocalLight(radius);
            calcTileLocalLights(x, y);
        }

    }
}
