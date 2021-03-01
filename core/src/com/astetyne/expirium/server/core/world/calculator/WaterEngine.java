package com.astetyne.expirium.server.core.world.calculator;

import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.tile.Tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class WaterEngine {

    public final static byte maxLevel = 5;
    private final static int updateTicks = 4;

    private final World world;
    private final Tile[][] terrain;
    private final int w, h;
    private final PriorityQueue<WaterTask> scheduledWaterMoves;
    private final PriorityQueue<WaterTask> scheduledWaterEvaporation;
    private final HashMap<Tile, WaterTask> scheduledEvaporations;
    private final HashSet<Tile> updatedTiles;

    public WaterEngine(World world, Tile[][] terrain, int w, int h) {
        this.world = world;
        this.terrain = terrain;
        this.w = w;
        this.h = h;
        scheduledWaterMoves = new PriorityQueue<>();
        scheduledWaterEvaporation = new PriorityQueue<>();
        scheduledEvaporations = new HashMap<>();
        updatedTiles = new HashSet<>();
    }

    public void onTick() {

        while(!scheduledWaterMoves.isEmpty()) {
            WaterTask task = scheduledWaterMoves.peek();
            if(task.tick <= world.getTick()) {
                updateWater(task.t);
                scheduledWaterMoves.remove();
            }else {
                break;
            }
        }

        while(!scheduledWaterEvaporation.isEmpty()) {
            WaterTask task = scheduledWaterEvaporation.peek();
            if(task.tick <= world.getTick()) {
                tryToEvaporate(task.t);
                scheduledWaterEvaporation.remove();
            }else {
                break;
            }
        }

        if(updatedTiles.size() > 0) {
            for(Player p : world.getServer().getPlayers()) {
                p.getNetManager().putWaterPacket(updatedTiles);
            }
            updatedTiles.clear();
        }

    }

    public void updateWater(Tile t) {

        // here we assume that world borders can not be broken and thus we do not need to care about edge cases

        if(t.getMaterial().isWatertight()) return;

        int x = t.getX();
        int y = t.getY();
        byte wl = t.getWaterLevel();

        int increment = Math.random() > 0.5 ? 1 : -1;

        if(wl != 0) {
            // bottom
            Tile bottom = terrain[x][y - 1];
            tryToFillUnder(t, bottom);

            // bottom sides
            tryToFillUnderSide(t, increment);
            tryToFillUnderSide(t, -increment);
        }
        tryToBalanceSide(t, increment);
        tryToBalanceSide(t, -increment);
    }

    private void tryToFillUnder(Tile source, Tile dest) {
        if(dest.getMaterial().isWatertight() || dest.getWaterLevel() == maxLevel || source.getWaterLevel() == 0) return;
        int toFill = Math.min(maxLevel - dest.getWaterLevel(), source.getWaterLevel());
        increaseWaterLevel(dest, toFill);
        increaseWaterLevel(source, -toFill);
    }

    private void tryToBalanceSide(Tile source, int increment) {

        int x = source.getX();
        int y = source.getY();
        byte wl = source.getWaterLevel();

        while(x != 0 && x != w-1) {
            x += increment;
            Tile dest = terrain[x][y];

            if(dest.getMaterial().isWatertight()) break;

            // check if can be balanced
            if(Math.abs(wl - dest.getWaterLevel()) >= 2) {
                if(wl > dest.getWaterLevel()) { // found tile with less water
                    increaseWaterLevel(dest, 1);
                    increaseWaterLevel(source, -1);
                }else { // found tile with more water
                    increaseWaterLevel(dest, -1);
                    increaseWaterLevel(source, 1);
                }
                break;
            }

            // cant be balanced and water does not continue, break
            if(dest.getWaterLevel() == 0) break;

            // check if destination is waiting falling water (prevention of horizontal water stacking)
            Tile tileUnderDest = terrain[x][y-1];
            if(!tileUnderDest.getMaterial().isWatertight() && tileUnderDest.getWaterLevel() != maxLevel) {
                willNeedUpdate(source);
                break;
            }
        }
    }

    private void tryToFillUnderSide(Tile source, int increment) {

        int x = source.getX();
        int y = source.getY() - 1;

        while(x != 0 && x != w-1 && source.getWaterLevel() >= 1) {
            x += increment;
            Tile dest = terrain[x][y];
            if(dest.getWaterLevel() <= maxLevel-2) break;
            tryToFillUnder(source, dest);
        }
    }

    private void willNeedUpdate(Tile t) {
        scheduledWaterMoves.add(new WaterTask(t, world.getTick() + updateTicks));
    }

    public void setWaterLevel(Tile t, int level) {
        level = Math.min(level, maxLevel);
        increaseWaterLevel(t, level - t.getWaterLevel());
    }

    public void increaseWaterLevel(Tile t, int amount) {
        t.increaseWaterLevel(amount);
        if(t.getY() != h-1) { // update for tile above
            willNeedUpdate(terrain[t.getX()][t.getY() + 1]);
        }
        updatedTiles.add(t);
        willNeedUpdate(t);
        if(canBeEvaporated(t)) {
            int randTicks = (int) (1 * (Math.random() * 30 + 10));
            WaterTask wt = new WaterTask(t, world.getTick() + randTicks);
            if(scheduledEvaporations.containsKey(t)) {
                scheduledWaterEvaporation.remove(scheduledEvaporations.get(t));
            }
            scheduledWaterEvaporation.add(wt);
            scheduledEvaporations.put(t, wt);
        }
    }

    private boolean canBeEvaporated(Tile t) {
        int x = t.getX();
        int y = t.getY();
        if(t.getWaterLevel() >= 4) return false;
        if(y != h-1 && terrain[x][y+1].getMaterial().isWatertight()) return false;
        if(y != 0 && terrain[x][y-1].getWaterLevel() > 0) return false;
        return true;
    }

    private void tryToEvaporate(Tile t) {
        if(!canBeEvaporated(t) || t.getWaterLevel() == 0) return;
        scheduledEvaporations.remove(t);
        increaseWaterLevel(t, -1);
    }

    static class WaterTask implements Comparable<WaterTask> {

        public Tile t;
        public long tick;

        public WaterTask(Tile t, long tick) {
            this.t = t;
            this.tick = tick;
        }

        @Override
        public int compareTo(WaterTask other){
            return Long.compare(tick, other.tick);
        }

    }
}
