package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.tiles.Tile;
import com.astetyne.expirium.client.utils.Consts;

public class LightCalculator {

    private final Tile[][] terrain;
    private final int w, h;

    public LightCalculator(Tile[][] terrain) {
        this.terrain = terrain;
        w = terrain.length;
        h = terrain[0].length;
    }

    public void recalcSkyLights() {
        for(int x = 0; x < w; x++) {
            for(int y = h-1; y >= 0; y--) {
                Tile t = terrain[x][y];
                t.setSkyLight(Consts.MAX_LIGHT_LEVEL);
                calcTileSkyLights(t, x, y);
                if(!t.getTypeFront().getSolidity().isSoft()) break;
            }
        }
    }

    private void calcTileSkyLights(Tile t, int x, int y) {

        int newLight = t.getSkyLight()-1;

        if(x != 0) { // left
            Tile t2 = terrain[x-1][y];
            if(t2.getSkyLight() < newLight) {
                t2.setSkyLight((byte) newLight);
                calcTileSkyLights(t2, x-1, y);
            }
        }
        if(x != w-1) { // right
            Tile t2 = terrain[x+1][y];
            if(t2.getSkyLight() < newLight) {
                t2.setSkyLight((byte) newLight);
                calcTileSkyLights(t2, x+1, y);
            }
        }
        if(y != h-1) { // top
            Tile t2 = terrain[x][y+1];
            if(t2.getSkyLight() < newLight) {
                t2.setSkyLight((byte) newLight);
                calcTileSkyLights(t2, x, y+1);
            }
        }
        if(y != 0) { // bottom
            Tile t2 = terrain[x][y-1];
            if(t2.getSkyLight() < newLight) {
                t2.setSkyLight((byte) newLight);
                calcTileSkyLights(t2, x, y-1);
            }
        }
    }

    public void onTileChange(int x) {
        for(int i = h-1; i >= 0; i--) {
            Tile t = terrain[x][i];
            t.setSkyLight(Consts.MAX_LIGHT_LEVEL);
            calcTileSkyLights(t, x, i);
            if(!t.getTypeFront().getSolidity().isSoft()) break;
        }
    }
}
