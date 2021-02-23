package com.astetyne.expirium.server.core.world.calculator;

import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.event.TileChangeEvent;
import com.astetyne.expirium.server.core.world.tile.Material;
import com.astetyne.expirium.server.core.world.tile.Solidity;
import com.astetyne.expirium.server.core.world.tile.Tile;

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
    private final Tile[][] terrain;
    private final int w, h;

    public StabilityCalculator(ExpiServer server, Tile[][] terrain, int w, int h) {
        this.server = server;
        this.terrain = terrain;
        this.h = h;
        this.w = w;
    }

    public void generateStability() {

        for(int i = 0; i < w; i++) {
            terrain[i][0].setStability(terrain[i][0].getMaterial().getMaxStability());
        }

        for(int i = 0; i < w; i++) {
            for(int j = 1; j < h; j++) {
                terrain[i][j].setStability(getActualStability(terrain[i][j]));
            }
        }
        for(int i = w-1; i >= 0; i--) {
            for(int j = 1; j < h; j++) {
                terrain[i][j].setStability(getActualStability(terrain[i][j]));
            }
        }
    }

    public void onTileChange(TileChangeEvent e) {

        if(e.getSource() == Source.STABILITY) return;

        Tile t = e.getTile();

        HashSet<Tile> affectedTiles = updateStability(t);

        for(Tile t2 : affectedTiles) {
            if(t2.getStability() == 0) {
                boolean withDrop = Math.random() < 4.0 / affectedTiles.size();
                server.getWorld().changeMaterial(t2, Material.AIR, withDrop, Source.STABILITY);
            }
        }

        affectedTiles.add(t);

        for(Player pp : server.getPlayers()) {
            pp.getNetManager().putStabilityPacket(affectedTiles);
        }

    }

    /** This method will calculate all required stability and return tiles which were affected. Call
     *  this when TileType was changed. You should check if TileType can be changed with canBeAdjusted() first.
     * @param t changed tile from which stability should be recalculated
     * @return all affected tiles
     */
    public HashSet<Tile> updateStability(Tile t) {

        int newStability = getActualStability(t);

        HashSet<Tile> affectedTiles = new HashSet<>();

        if(newStability > t.getStability()) {

            t.setStability(newStability);
            recalculateStabilityForNearbyTiles(t, affectedTiles);
            if(t.getY()-1 != h) recalculateStabilityForNearbyTiles(terrain[t.getX()][t.getY()+1], affectedTiles);

        }else if(newStability < t.getStability()) {

            StabilityPack pack = new StabilityPack();
            findStrongConnections(t, pack);
            for(Tile t2 : pack.strongTiles) {
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
    public boolean canBeChanged(Tile t, Material material) {

        Material oldMat = t.getMaterial();

        t.setMaterial(material);
        int actualS = getActualStability(t);
        t.setMaterial(oldMat);
        return actualS != 0;
    }

    private void findStrongConnections(Tile t, StabilityPack pack) {

        int x = t.getX();
        int y = t.getY();

        t.setStability(0);
        pack.changedTiles.add(t);

        // left tile
        if(x != 0) checkStrongConnection(terrain[x-1][y], pack);
        // top tile
        if(y != h-1) checkStrongConnection(terrain[x][y+1], pack);
        // right tile
        if(x != w-1) checkStrongConnection(terrain[x+1][y], pack);
        // bottom tile
        if(y != 0) checkStrongConnection(terrain[x][y-1], pack);

        // top left tile - for magic triangle
        if(y != h-1 && x != 0) checkStrongConnection(terrain[x-1][y+1], pack);
        // top right tile - for magic triangle
        if(y != h-1 && x != w-1) checkStrongConnection(terrain[x+1][y+1], pack);

    }

    private void checkStrongConnection(Tile t, StabilityPack pack) {
        if(t.getMaterial() == Material.AIR) return;
        if(t.getStability() > getActualStability(t) && t.getY() != 0) {
            findStrongConnections(t, pack);
        }else {
            pack.strongTiles.add(t);
        }
    }

    /** This method will return new stability of the tile based on nearby tiles.*/
    private int getActualStability(Tile t) {

        int x = t.getX();
        int y = t.getY();

        int maxAvailStab = 0;

        // left tile
        if(x != 0 && !terrain[x-1][y].getMaterial().getSolidity().isLabile() && !t.getMaterial().getSolidity().isVert())
            maxAvailStab = Math.max(maxAvailStab, terrain[x-1][y].getStability()-1);
        // top tile
        if(y != h-1 && !terrain[x][y+1].getMaterial().getSolidity().isLabile() && !t.getMaterial().getSolidity().isVert())
            maxAvailStab = Math.max(maxAvailStab, terrain[x][y+1].getStability()-2);
        // right tile
        if(x != w-1 && !terrain[x+1][y].getMaterial().getSolidity().isLabile() && !t.getMaterial().getSolidity().isVert())
            maxAvailStab = Math.max(maxAvailStab, terrain[x+1][y].getStability()-1);
        // bottom tile
        if(y != 0 && (!terrain[x][y-1].getMaterial().getSolidity().isLabile() ||
                terrain[x][y-1].getMaterial().getSolidity() == Solidity.ONLY_VERT))
            maxAvailStab = Math.max(maxAvailStab, terrain[x][y-1].getStability());

        //magic triangle
        if(x > 0 && x < w-1 && y > 0) {
            Tile t1 = terrain[x-1][y-1];
            Tile t2 = terrain[x][y-1];
            Tile t3 = terrain[x+1][y-1];
            Material mat1 = t1.getMaterial();
            Material mat2 = t2.getMaterial();
            Material mat3 = t3.getMaterial();

            if(!mat1.getSolidity().isLabile() && !mat2.getSolidity().isLabile() && !mat3.getSolidity().isLabile()) {
                int s1 = t1.getStability();
                int s2 = t2.getStability();
                int s3 = t3.getStability();
                if(s1 == s2 && s2 == s3 && s1 != 0) {
                    maxAvailStab = Math.max(maxAvailStab, s1 + 1);
                }else if(mat1 != Material.AIR && mat1 == mat2 && mat2 == mat3) {
                    maxAvailStab = Math.max(maxAvailStab, Math.min(s1, Math.min(s2, s3))+1);
                }
            }
        }
        return Math.min(t.getMaterial().getMaxStability(), maxAvailStab);
    }

    /** This method will add all nearby tiles which have less stability than their real stability and set the new the stability.*/
    private void recalculateStabilityForNearbyTiles(Tile t, HashSet<Tile> changed) {

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

    private void floodHigherStability(int x, int y, HashSet<Tile> changed) {

        if(x < 0 || x == w || y < 0 || y == h) return;

        Tile t = terrain[x][y];

        if(t.getMaterial() == Material.AIR) return;
        int realStability = getActualStability(t);
        if(realStability > t.getStability()) {
            t.setStability(realStability);
            changed.add(t);
            recalculateStabilityForNearbyTiles(t, changed);
        }
    }

}
