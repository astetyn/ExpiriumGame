package com.astetyne.expirium.server.core.world.calculator;

import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.event.TileChangeEvent;
import com.astetyne.expirium.server.core.world.tile.Material;
import com.astetyne.expirium.server.core.world.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class BackWallCalculator {

    private static final int radius = 50;

    private final ExpiServer server;
    private final Tile[][] terrain;
    private final int w, h;
    private final boolean[][] visitMap;
    private final IntVector2 tempMiddle;

    public BackWallCalculator(ExpiServer server, Tile[][] terrain, int w, int h) {
        this.server = server;
        this.terrain = terrain;
        this.w = w;
        this.h = h;
        visitMap = new boolean[radius*2+1][radius*2+1];
        tempMiddle = new IntVector2(0, 0);
    }

    public void onTileChange(TileChangeEvent e) {

        Tile t = e.getTile();

        int x = t.getX();
        int y = t.getY();

        if(t.getMaterial().isWall()) {

            List<Tile> affectedTiles = new ArrayList<>();

            tempMiddle.set(x, y);

            boolean b1, b2, b3, b4;
            b1 = b2 = b3 = b4 = false;

            if(t.getX() != 0 && !terrain[x-1][y].getMaterial().isWall()) {
                clearMap();
                b1 = isAreaClosed(terrain[x-1][y]);
            }
            if(t.getX() != w-1 && !terrain[x+1][y].getMaterial().isWall()) {
                clearMap();
                b2 = isAreaClosed(terrain[x+1][y]);
            }
            if(t.getY() != 0 && !terrain[x][y-1].getMaterial().isWall()) {
                clearMap();
                b3 = isAreaClosed(terrain[x][y-1]);
            }
            if(t.getY() != h-1 && !terrain[x][y+1].getMaterial().isWall()) {
                clearMap();
                b4 = isAreaClosed(terrain[x][y+1]);
            }

            if(b1) {
                createBackWallsFlood(x-1, y, affectedTiles);
            }
            if(b2) {
                createBackWallsFlood(x+1, y, affectedTiles);
            }
            if(b3) {
                createBackWallsFlood(x, y-1, affectedTiles);
            }
            if(b4) {
                createBackWallsFlood(x, y+1, affectedTiles);
            }

            for(Player ep : server.getPlayers()) {
                ep.getNetManager().putBackWallPacket(affectedTiles);
            }

        }else if(e.getFromMat().isWall()) {

            List<Tile> affectedTiles = new ArrayList<>();

            if(t.getX() != 0 && terrain[x-1][y].hasBackWall()) {
                clearMap();
                if(isAreaClosed(terrain[x-1][y])) {
                    t.setBackWall(true);
                    affectedTiles.add(t);
                }else {
                    clearBackWallsFlood(x-1, y, affectedTiles);
                }
            }
            if(t.getX() != w-1 && !terrain[x+1][y].getMaterial().isWall()) {
                clearMap();
                if(isAreaClosed(terrain[x+1][y])) {
                    t.setBackWall(true);
                    affectedTiles.add(t);
                }else {
                    clearBackWallsFlood(x+1, y, affectedTiles);
                }
            }
            if(t.getY() != 0 && !terrain[x][y-1].getMaterial().isWall()) {
                clearMap();
                if(isAreaClosed(terrain[x][y-1])) {
                    t.setBackWall(true);
                    affectedTiles.add(t);
                }else {
                    clearBackWallsFlood(x, y-1, affectedTiles);
                }
            }
            if(t.getY() != h-1 && !terrain[x][y+1].getMaterial().isWall()) {
                clearMap();
                if(isAreaClosed(terrain[x][y+1])) {
                    t.setBackWall(true);
                    affectedTiles.add(t);
                }else {
                    clearBackWallsFlood(x, y+1, affectedTiles);
                }
            }

            for(Player ep : server.getPlayers()) {
                ep.getNetManager().putBackWallPacket(affectedTiles);
            }

            for(Tile at : affectedTiles) {
                if(at.getMaterial().isHouseOnly()) {
                    server.getWorld().changeMaterial(at, Material.AIR, true, Source.NATURAL);
                }
            }
        }
    }

    private void clearMap() {
        for(int i = 0; i < visitMap.length; i++) {
            for(int j = 0; j < visitMap[0].length; j++) {
                visitMap[i][j] = false;
            }
        }
    }

    private void createBackWallsFlood(int x, int y, List<Tile> list) {

        if(x < 0 || x == w || y < 0 || y == h) return;

        Tile t = terrain[x][y];
        if(t.hasBackWall() || t.getMaterial().isWall()) return;

        t.setBackWall(true);

        list.add(t);
        createBackWallsFlood(x-1, y, list);
        createBackWallsFlood(x+1, y, list);
        createBackWallsFlood(x, y-1, list);
        createBackWallsFlood(x, y+1, list);
    }

    private void clearBackWallsFlood(int x, int y, List<Tile> list) {

        if(x < 0 || x == w || y < 0 || y == h) return;

        Tile t = terrain[x][y];

        if(!t.hasBackWall()) return;
        t.setBackWall(false);

        list.add(t);

        clearBackWallsFlood(x-1, y, list);
        clearBackWallsFlood(x+1, y, list);
        clearBackWallsFlood(x, y-1, list);
        clearBackWallsFlood(x, y+1, list);

    }

    private boolean isAreaClosed(Tile t) {

        int x = t.getX();
        int y = t.getY();

        if(Math.abs(tempMiddle.x - x) > radius || Math.abs(tempMiddle.y - y) > radius) return false;

        if(wasVisited(x, y)) return true;

        if(t.getMaterial().isWall()) return true;

        markVisited(x, y);

        if(x == 0 || x == w-1 || y == 0 || y == h-1) return false;

        return isAreaClosed(terrain[x - 1][y]) && isAreaClosed(terrain[x + 1][y]) &&
                isAreaClosed(terrain[x][y - 1]) && isAreaClosed(terrain[x][y + 1]);
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
