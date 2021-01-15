package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.server.api.world.tiles.ExpiTile;

import java.util.HashSet;

/**
 * Basic rules of this system:
 * <p>- anything except AIR can not have 0 stability
 * <p>- everything marked as LABILE will have 1 stability
 * <p>- magic triangle is legit only when all bottom tiles have the same stability
 * <p>- World generator can produce incorrect stability-situations, but they must meet these rules. So all these cases
 * must be handled in generateStability().
 */
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
            worldTerrain[0][j].setStability(worldTerrain[0][j].getTypeFront().getMaxStability());
        }

        for(int i = 1; i < h; i++) {
            for(int j = 0; j < w; j++) {
                if(worldTerrain[i][j].isLabile()) {
                    worldTerrain[i][j].setStability(1);
                    continue;
                }
                worldTerrain[i][j].setStability(getActualStability(worldTerrain[i][j]));
            }
        }
        for(int i = 1; i < h; i++) {
            for(int j = w-1; j >= 0; j--) {
                if(worldTerrain[i][j].isLabile()) {
                    worldTerrain[i][j].setStability(1);
                    continue;
                }
                worldTerrain[i][j].setStability(getActualStability(worldTerrain[i][j]));
            }
        }
    }

    /** This method will calculate all required stability and return tiles which were affected. Call
     *  this when TileType was changed. You should check if TileType can be changed with canBeAdjusted() first.
     * @param t changed tile from which stability should be recalculated
     * @return all affected tiles
     */
    public HashSet<ExpiTile> updateStability(ExpiTile t) {

        int newStability = getActualStability(t);

        HashSet<ExpiTile> affectedTiles = new HashSet<>();

        //System.out.println("NS: "+newStability+" OS: "+t.getStability());

        if(newStability > t.getStability()) {

            t.setStability(newStability);
            recalculateStabilityForNearbyTiles(t, affectedTiles);
            if(t.getY()-1 != h)recalculateStabilityForNearbyTiles(worldTerrain[t.getY()+1][t.getX()], affectedTiles);
            //System.out.println("AS:"+t.getStability());

        }else if(newStability < t.getStability()) {

            StabilityPack pack = new StabilityPack();
            findStrongConnections(t, pack);
            for(ExpiTile t2 : pack.strongTiles) {
                recalculateStabilityForNearbyTiles(t2, affectedTiles);
            }
            affectedTiles = pack.changedTiles;
            t.setStability(newStability);
            recalculateStabilityForNearbyTiles(t, affectedTiles);
        }
        return affectedTiles;
    }

    /**
     * @return True if tile can be changed and stability will be correct.
     */
    public boolean canBeChanged(ExpiTile t, TileType checkType) {

        TileType oldType = t.getTypeFront();

        t.setTypeFront(checkType);
        int actualS = getActualStability(t);
        t.setTypeFront(oldType);
        return actualS != 0;
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

        // top left tile - for magic triangle
        if(y != h-1) checkStrongConnection(worldTerrain[y+1][x-1], pack);
        // top right tile - for magic triangle
        if(y != h-1) checkStrongConnection(worldTerrain[y+1][x+1], pack);

    }

    private void checkStrongConnection(ExpiTile t, StabilityPack pack) {
        if(t.getTypeFront() == TileType.AIR) return;
        if(t.getStability() > getActualStability(t) && t.getY() != 0) {
            findStrongConnections(t, pack);
        }else {
            pack.strongTiles.add(t);
        }
    }

    /** This method will return new stability of the tile based on nearby tiles.*/
    private int getActualStability(ExpiTile t) {

        int x = t.getX();
        int y = t.getY();

        int maxAvailStab = 0;

        // left tile
        if(x != 0 && !worldTerrain[y][x-1].isLabile() && !t.getTypeFront().getSolidity().isVert())
            maxAvailStab = Math.max(maxAvailStab, worldTerrain[y][x-1].getStability()-1);
        // top tile
        if(y != h-1 && !worldTerrain[y+1][x].isLabile() && !t.getTypeFront().getSolidity().isVert())
            maxAvailStab = Math.max(maxAvailStab, worldTerrain[y+1][x].getStability()-2);
        // right tile
        if(x != w-1 && !worldTerrain[y][x+1].isLabile() && !t.getTypeFront().getSolidity().isVert())
            maxAvailStab = Math.max(maxAvailStab, worldTerrain[y][x+1].getStability()-1);
        // bottom tile
        if(y != 0 && !worldTerrain[y-1][x].isLabile())
            maxAvailStab = Math.max(maxAvailStab, worldTerrain[y-1][x].getStability());

        //magic triangle
        if(x > 0 && x < w-1 && y > 0) {
            ExpiTile t1 = worldTerrain[y-1][x-1];
            ExpiTile t2 = worldTerrain[y-1][x];
            ExpiTile t3 = worldTerrain[y-1][x+1];

            if(!t1.isLabile() && !t2.isLabile() && !t3.isLabile()) {
                int s1 = t1.getStability();
                int s2 = t2.getStability();
                int s3 = t3.getStability();
                if(s1 == s2 && s2 == s3 && s1 != 0) {
                    maxAvailStab = Math.max(maxAvailStab, s1 + 1);
                }
            }
        }
        return Math.min(t.getTypeFront().getMaxStability(), maxAvailStab);
    }

    /** This method will add all nearby tiles which have less stability than their real stability and set the new the stability.*/
    private void recalculateStabilityForNearbyTiles(ExpiTile t, HashSet<ExpiTile> changed) {

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
        if(t.getTypeFront() == TileType.AIR) return;
        int realStability = getActualStability(t);
        if(realStability > t.getStability()) {
            t.setStability(realStability);
            changed.add(t);
            recalculateStabilityForNearbyTiles(t, changed);
        }
    }

}
