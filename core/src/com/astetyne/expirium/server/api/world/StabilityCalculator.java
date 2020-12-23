package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.backend.StabilityPack;

import java.util.HashSet;

public class StabilityCalculator {

    private final ExpiTile[][] worldTerrain;
    private final int w, h;

    public StabilityCalculator(ExpiTile[][] worldTerrain) {
        this.worldTerrain = worldTerrain;
        this.h = worldTerrain.length;
        this.w = worldTerrain[0].length;
    }

    public void generateStability() {

        for(int j = 0; j < w; j++) {
            worldTerrain[0][j].setStability(10);
        }

        for(int i = 1; i < h; i++) {
            for(int j = 0; j < w; j++) {
                worldTerrain[i][j].setStability(getActualStability(worldTerrain[i][j]));
            }
        }
        for(int i = 1; i < h; i++) {
            for(int j = w-1; j >= 0; j--) {
                worldTerrain[i][j].setStability(getActualStability(worldTerrain[i][j]));
            }
        }

    }

    // This method will set the given tiles stability to 0 and return all affected tiles.
    public HashSet<ExpiTile> clearStabilityAndRecalculate(ExpiTile tile) {

        StabilityPack pack = new StabilityPack();

        findStrongConnections(tile, pack);

        HashSet<ExpiTile> ignored = new HashSet<>();
        for(ExpiTile t : pack.strongTiles) {
            recalculateStabilityForNearbyTiles(t, ignored);
        }
        return pack.changedTiles;
    }

    private void findStrongConnections(ExpiTile t, StabilityPack pack) {

        int x = t.getX();
        int y = t.getY();

        t.setStability(0);
        pack.changedTiles.add(t);

        // left tile
        if(x != 0) checkStrongConnection(worldTerrain[y][x-1], pack);
        // top tile
        if(y != h-1) checkStrongConnection(worldTerrain[y+1][x], pack);
        // right tile
        if(x != w-1) checkStrongConnection(worldTerrain[y][x+1], pack);
        // bottom tile
        if(y != 0) checkStrongConnection(worldTerrain[y-1][x], pack);

    }

    private void checkStrongConnection(ExpiTile t, StabilityPack pack) {
        if(t.getType() == TileType.AIR) return;
        if(t.getStability() <= getActualStability(t)) {
            pack.strongTiles.add(t);
        }else {
            findStrongConnections(t, pack);
        }
    }

    // This method will return new stability of the tile based on nearby tiles.
    public int getActualStability(ExpiTile t) {

        int x = t.getX();
        int y = t.getY();

        int maxAvailStab = 0;

        // left tile
        if(x != 0) maxAvailStab = Math.max(maxAvailStab, worldTerrain[y][x-1].getStability()-1);
        // top tile
        if(y != h-1) maxAvailStab = Math.max(maxAvailStab, worldTerrain[y+1][x].getStability()-2);
        // right tile
        if(x != w-1) maxAvailStab = Math.max(maxAvailStab, worldTerrain[y][x+1].getStability()-1);
        // bottom tile
        if(y != 0) maxAvailStab = Math.max(maxAvailStab, worldTerrain[y-1][x].getStability());

        //magic triangle
        if(x > 0 && x < w-1 && y > 0) {
            int s1 = worldTerrain[y-1][x-1].getStability();
            int s2 = worldTerrain[y-1][x].getStability();
            int s3 = worldTerrain[y-1][x+1].getStability();
            if(s1 == s2 && s2 == s3 && s1 != 0) {
                maxAvailStab = Math.max(maxAvailStab, s1+1);
            }
        }
        return Math.min(t.getType().getStability(), maxAvailStab);
    }

    public void recalculateStabilityForNearbyTiles(ExpiTile t, HashSet<ExpiTile> changed) {

        int x = t.getX();
        int y = t.getY();

        // left tile
        if(x != 0) checkRealStability(worldTerrain[y][x-1], changed);
        // top tile
        if(y != h-1) checkRealStability(worldTerrain[y+1][x], changed);
        // right tile
        if(x != w-1) checkRealStability(worldTerrain[y][x+1], changed);
        // bottom tile
        if(y != 0) checkRealStability(worldTerrain[y-1][x], changed);

    }

    private void checkRealStability(ExpiTile t, HashSet<ExpiTile> changed) {
        if(t.getType() == TileType.AIR) return;
        int realStability = getActualStability(t);
        if(realStability > t.getStability()) {
            t.setStability(realStability);
            changed.add(t);
            recalculateStabilityForNearbyTiles(t, changed);
        }
    }

}
