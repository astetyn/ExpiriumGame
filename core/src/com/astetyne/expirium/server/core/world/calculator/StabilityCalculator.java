package com.astetyne.expirium.server.core.world.calculator;

import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.event.TileChangeEvent;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.tiles.ExpiTile;

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

    private final ExpiServer server;
    private final ExpiTile[][] worldTerrain;
    private final int w, h;

    public StabilityCalculator(ExpiServer server, ExpiWorld world) {
        this.server = server;
        this.worldTerrain = world.getTerrain();
        this.h = worldTerrain.length;
        this.w = worldTerrain[0].length;
    }

    public void generateStability() {

        for(int j = 0; j < w; j++) {
            worldTerrain[0][j].setStability(worldTerrain[0][j].getType().getMaxStability());
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

    public void onTileChange(TileChangeEvent e) {

        if(e.getSource() == Source.STABILITY) return;

        ExpiTile t = e.getTile();

        HashSet<ExpiTile> affectedTiles = updateStability(t);

        for(ExpiTile t2 : affectedTiles) {
            if(t2.getStability() == 0) {
                boolean withDrop = Math.random() < 2.0 / affectedTiles.size();
                server.getWorld().changeTile(t2, TileType.AIR, withDrop, null, Source.STABILITY);
            }
        }

        affectedTiles.add(t);

        for(ExpiPlayer pp : server.getPlayers()) {
            pp.getNetManager().putStabilityPacket(affectedTiles);
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

        if(newStability > t.getStability()) {

            t.setStability(newStability);
            recalculateStabilityForNearbyTiles(t, affectedTiles);
            if(t.getY()-1 != h) recalculateStabilityForNearbyTiles(worldTerrain[t.getY()+1][t.getX()], affectedTiles);

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

        TileType oldType = t.getType();

        t.setType(checkType);
        int actualS = getActualStability(t);
        t.setType(oldType);
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
        if(y != h-1 && x != 0) checkStrongConnection(worldTerrain[y+1][x-1], pack);
        // top right tile - for magic triangle
        if(y != h-1 && x != w-1) checkStrongConnection(worldTerrain[y+1][x+1], pack);

    }

    private void checkStrongConnection(ExpiTile t, StabilityPack pack) {
        if(t.getType() == TileType.AIR) return;
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
        if(x != 0 && !worldTerrain[y][x-1].isLabile() && !t.getType().getSolidity().isVert())
            maxAvailStab = Math.max(maxAvailStab, worldTerrain[y][x-1].getStability()-1);
        // top tile
        if(y != h-1 && !worldTerrain[y+1][x].isLabile() && !t.getType().getSolidity().isVert())
            maxAvailStab = Math.max(maxAvailStab, worldTerrain[y+1][x].getStability()-2);
        // right tile
        if(x != w-1 && !worldTerrain[y][x+1].isLabile() && !t.getType().getSolidity().isVert())
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
                TileType type1 = t1.getType();
                TileType type2 = t2.getType();
                TileType type3 = t3.getType();
                if(s1 == s2 && s2 == s3 && s1 != 0) {
                    maxAvailStab = Math.max(maxAvailStab, s1 + 1);
                }else if(type1 != TileType.AIR && type1 == type2 && type2 == type3) {
                    maxAvailStab = Math.max(maxAvailStab, Math.min(s1, Math.min(s2, s3))+1);
                }
            }
        }
        return Math.min(t.getType().getMaxStability(), maxAvailStab);
    }

    /** This method will add all nearby tiles which have less stability than their real stability and set the new the stability.*/
    private void recalculateStabilityForNearbyTiles(ExpiTile t, HashSet<ExpiTile> changed) {

        int x = t.getX();
        int y = t.getY();

        floodHigherStability(x-1, y, changed);
        floodHigherStability(x+1, y, changed);
        floodHigherStability(x, y-1, changed);
        floodHigherStability(x, y+1, changed);

        // magic triangle
        floodHigherStability(x+1, y+1, changed);
        floodHigherStability(x-1, y+1, changed);

    }

    private void floodHigherStability(int x, int y, HashSet<ExpiTile> changed) {

        if(x < 0 || x == w || y < 0 || y == h) return;

        ExpiTile t = worldTerrain[y][x];

        if(t.getType() == TileType.AIR) return;
        int realStability = getActualStability(t);
        if(realStability > t.getStability()) {
            t.setStability(realStability);
            changed.add(t);
            recalculateStabilityForNearbyTiles(t, changed);
        }
    }

}
