package com.astetyne.expirium.server.core.world.calculator;

import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.tile.Tile;

import java.util.HashSet;
import java.util.PriorityQueue;

public class WaterEngine {

    private final static byte maxLevel = 5;
    private final static int updateTicks = 4;

    private final ExpiServer server;
    private final Tile[][] terrain;
    private final int w, h;
    private final PriorityQueue<WaterTask> scheduledWaterTicks;
    private final HashSet<Tile> updatedTiles;

    public WaterEngine(ExpiServer server, Tile[][] terrain, int w, int h) {
        this.server = server;
        this.terrain = terrain;
        this.w = w;
        this.h = h;
        scheduledWaterTicks = new PriorityQueue<>();
        updatedTiles = new HashSet<>();
    }

    public void onTick() {

        while(!scheduledWaterTicks.isEmpty()) {
            WaterTask task = scheduledWaterTicks.peek();
            if(task.tick <= server.getWorld().getTick()) {
                updateWater(task.t);
                scheduledWaterTicks.remove();
            }else {
                break;
            }
        }

        for(Player p : server.getPlayers()) {
            p.getNetManager().putWaterPacket(updatedTiles);
        }
        updatedTiles.clear();
    }

    public void createWater(Tile t) {
        t.setWaterLevel(2);
        scheduledWaterTicks.add(new WaterTask(t, server.getWorld().getTick() + updateTicks));
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
        willNeedUpdate(dest);
        willNeedUpdate(source);
        updatedTiles.add(source);
        updatedTiles.add(dest);
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
                willNeedUpdate(source);
                willNeedUpdate(dest);
                updatedTiles.add(source);
                updatedTiles.add(dest);
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
        scheduledWaterTicks.add(new WaterTask(t, server.getWorld().getTick() + updateTicks));
    }

    private void increaseWaterLevel(Tile t, int amount) {
        t.increaseWaterLevel(amount);
        if(t.getY() != h-1) { // update for tile above
            willNeedUpdate(terrain[t.getX()][t.getY() + 1]);
        }
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
