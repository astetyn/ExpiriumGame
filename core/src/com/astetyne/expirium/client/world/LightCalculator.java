package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.tiles.Tile;
import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LightCalculator {

    private final Tile[][] terrain;
    private final int w, h;
    private final HashSet<Tile> lightSources;

    public LightCalculator(Tile[][] terrain) {
        this.terrain = terrain;
        w = terrain.length;
        h = terrain[0].length;
        lightSources = new HashSet<>();
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

        int newLight = t.getSkyLight()-3;

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

    private void destroyLight(int x, int y, List<IntVector2> legitLights, List<IntVector2> filler) {

        Tile t = terrain[x][y];

        if(lightSources.contains(t) || t.getLocalLight() == 0) return;

        if(isLegit(x, y)) {
            legitLights.add(new IntVector2(x, y));
            return;
        }

        t.setLocalLight((byte)0);

        if(x != 0) { //left
            filler.add(new IntVector2(x-1, y));
        }
        if(x != w-1) { //right
            filler.add(new IntVector2(x+1, y));
        }
        if(y != h-1) { //top
            filler.add(new IntVector2(x, y+1));
        }
        if(y != 0) { //bottom
            filler.add(new IntVector2(x, y-1));
        }

    }

    private boolean isLegit(int x, int y) {

        int l = terrain[x][y].getLocalLight();

        if(x != 0 && terrain[x-1][y].getLocalLight() > l) { //left
            return true;
        }
        if(x != w-1 && terrain[x+1][y].getLocalLight() > l) { //right
            return true;
        }
        if(y != h-1 && terrain[x][y+1].getLocalLight() > l) { //top
            return true;
        }
        if(y != 0 && terrain[x][y-1].getLocalLight() > l) { //bottom
            return true;
        }
        return false;

    }

    public void onTileChange(TileType from, TileType to, int x, int y) {

        Tile t = terrain[x][y];

        for(int i = h-1; i >= 0; i--) {
            Tile t2 = terrain[x][i];
            t2.setSkyLight(Consts.MAX_LIGHT_LEVEL);
            calcTileSkyLights(t2, x, i);
            if(!t2.getTypeFront().getSolidity().isSoft()) break;
        }

        if(lightSources.contains(t)) {
            lightSources.remove(t);
            List<IntVector2> legitLights = new ArrayList<>();

            List<IntVector2> queue = new ArrayList<>();
            List<IntVector2> queueTemp = new ArrayList<>();

            destroyLight(x, y, legitLights, queue);

            while(queue.size() > 0) {
                for(IntVector2 loc : queue) {
                    destroyLight(loc.x, loc.y, legitLights, queueTemp);
                }
                queue.clear();
                queue.addAll(queueTemp);
                queueTemp.clear();
            }

            //todo: skontrolovat obsah legitLights
            for(IntVector2 loc : legitLights) {
                calcTileLocalLights(loc.x, loc.y);
            }
        }

        if(to == TileType.CAMPFIRE_BIG) {
            t.setLocalLight((byte)10);
            calcTileLocalLights(x, y);
            lightSources.add(t);
        }else if(to == TileType.CAMPFIRE_SMALL) {
            t.setLocalLight((byte)5);
            calcTileLocalLights(x, y);
            lightSources.add(t);
        }
    }
}
