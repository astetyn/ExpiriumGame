package com.astetyne.expirium.server.core.world.calculator;

import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.event.TileChangeEvent;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.tiles.ExpiTile;

import java.util.ArrayList;
import java.util.List;

public class InteriorWallCalculator {

    private static final int radius = 50;

    private final ExpiServer server;
    private final ExpiTile[][] terrain;
    private final int w, h;
    private final boolean[][] visitMap;
    private IntVector2 tempMiddle;

    public InteriorWallCalculator(ExpiServer server, ExpiWorld world) {
        this.server = server;
        this.terrain = world.getTerrain();
        w = world.getTerrainWidth();
        h = world.getTerrainHeight();
        visitMap = new boolean[radius*2+1][radius*2+1];
        tempMiddle = new IntVector2(0, 0);
    }

    public void onTileChange(TileChangeEvent e) {

        ExpiTile t = e.getTile();

        int x = t.getX();
        int y = t.getY();

        if(t.getType().isWall()) {

            tempMiddle.set(x, y);

            boolean b1, b2, b3, b4;
            b1 = b2 = b3 = b4 = false;

            if(t.getX() != 0 && !terrain[y][x-1].getType().isWall()) {
                clearMap();
                b1 = isAreaClosed(terrain[y][x-1]);
            }
            if(t.getX() != w-1 && !terrain[y][x+1].getType().isWall()) {
                clearMap();
                b2 = isAreaClosed(terrain[y][x+1]);
            }
            if(t.getY() != 0 && !terrain[y-1][x].getType().isWall()) {
                clearMap();
                b3 = isAreaClosed(terrain[y-1][x]);
            }
            if(t.getY() != h-1 && !terrain[y+1][x].getType().isWall()) {
                clearMap();
                b4 = isAreaClosed(terrain[y+1][x]);
            }

            System.out.println("closed: "+b1+" "+b2+" "+b3+" "+b4);

            List<IntVector2> backWalls = new ArrayList<>();

            // isAreaClosed are here just for filling map with true values
            if(b1) {
                isAreaClosed(terrain[y][x-1]);
                IntVector2 loc = transformToMap(x-1, y);
                getBackWallsFrom(loc.x, loc.y, backWalls);
            }
            if(b2) {
                isAreaClosed(terrain[y][x+1]);
                IntVector2 loc = transformToMap(x+1, y);
                getBackWallsFrom(loc.x, loc.y, backWalls);
            }
            if(b3) {
                isAreaClosed(terrain[y-1][x]);
                IntVector2 loc = transformToMap(x, y-1);
                getBackWallsFrom(loc.x, loc.y, backWalls);
            }
            if(b4) {
                isAreaClosed(terrain[y+1][x]);
                IntVector2 loc = transformToMap(x, y+1);
                getBackWallsFrom(loc.x, loc.y, backWalls);
            }

            for(IntVector2 loc : backWalls) {
                server.getWorld().changeTile(terrain[loc.y][loc.x], TileType.SOFT_WOODEN_WALL, false, null, Source.OTHER);
            }

        }else if(e.getFrom().isWall()){

            if(t.getX() != 0 && terrain[y][x-1].hasBackWall()) {
                clearMap();
                if(isAreaClosed(terrain[y][x-1])) {
                    //todo
                }
            }
            if(t.getX() != w-1 && !terrain[y][x+1].getType().isWall()) {
                clearMap();
                b2 = isAreaClosed(terrain[y][x+1]);
            }
            if(t.getY() != 0 && !terrain[y-1][x].getType().isWall()) {
                clearMap();
                b3 = isAreaClosed(terrain[y-1][x]);
            }
            if(t.getY() != h-1 && !terrain[y+1][x].getType().isWall()) {
                clearMap();
                b4 = isAreaClosed(terrain[y+1][x]);
            }

        }

    }

    /** Flood clear backWall from all reached tiles. */
    private void clearFlood(int x, int y) {

        if(x != 0 && terrain[y][x-1].hasBackWall()) {
            terrain[y][x-1].setBackWall(false);
            clearFlood(x-1, y);
        }
        if(x != w-1 && !terrain[y][x+1].getType().isWall()) {
            terrain[y][x+1].setBackWall(false);
            clearFlood(x+1, y);
        }
        if(y != 0 && !terrain[y-1][x].getType().isWall()) {
            terrain[y-1][x].setBackWall(false);
            clearFlood(x, y-1);
        }
        if(y != h-1 && !terrain[y+1][x].getType().isWall()) {
            terrain[y+1][x].setBackWall(false);
            clearFlood(x, y+1);
        }

    }

    private void clearMap() {
        for(int i = 0; i < visitMap.length; i++) {
            for(int j = 0; j < visitMap[0].length; j++) {
                visitMap[i][j] = false;
            }
        }
    }

    // x and y are map coordinates
    private void getBackWallsFrom(int x, int y, List<IntVector2> list) {

        // is this check required? area should be closed...
        if(x < 0 || x >= visitMap.length || y < 0 || y >= visitMap[0].length) return;

        if(!visitMap[x][y]) return;
        visitMap[x][y] = false;

        list.add(transformFromMap(x, y));
        getBackWallsFrom(x-1, y, list);
        getBackWallsFrom(x+1, y, list);
        getBackWallsFrom(x, y-1, list);
        getBackWallsFrom(x, y+1, list);

    }

    private boolean isAreaClosed(ExpiTile t) {

        int x = t.getX();
        int y = t.getY();

        if(Math.abs(tempMiddle.x - x) > radius || Math.abs(tempMiddle.y - y) > radius) return false;

        if(wasVisited(x, y)) return true;

        if(t.getType().isWall()) return true;

        markVisited(x, y);

        if(x == 0 || x == w-1 || y == 0 || y == h-1) return false;

        return isAreaClosed(terrain[y][x - 1]) && isAreaClosed(terrain[y][x + 1]) &&
                isAreaClosed(terrain[y - 1][x]) && isAreaClosed(terrain[y + 1][x]);
    }

    private void markVisited(int x, int y) {
        IntVector2 loc = transformToMap(x, y);
        visitMap[loc.x][loc.y] = true;
    }

    private boolean wasVisited(int x, int y) {
        IntVector2 loc = transformToMap(x, y);
        return visitMap[loc.x][loc.y];
    }

    private IntVector2 transformToMap(int x, int y) {

        IntVector2 vec = new IntVector2(x, y);

        if(vec.x >= tempMiddle.x) {
            vec.x = radius + vec.x - tempMiddle.x;
        }else {
            vec.x = radius - (tempMiddle.x - vec.x);
        }
        if(vec.y >= tempMiddle.y) {
            vec.y = radius + vec.y - tempMiddle.y;
        }else {
            vec.y = radius - (tempMiddle.y - vec.y);
        }
        return vec;
    }

    private IntVector2 transformFromMap(int x, int y) {

        IntVector2 vec = new IntVector2(x, y);

        if(vec.x >= radius) {
            vec.x = tempMiddle.x + vec.x - radius;

        }else {
            vec.x = tempMiddle.x - (radius - vec.x);
        }
        if(vec.y >= radius) {
            vec.y = tempMiddle.y + vec.y - radius;
        }else {
            vec.y = tempMiddle.y - (radius - vec.y);
        }
        return vec;
    }

}
