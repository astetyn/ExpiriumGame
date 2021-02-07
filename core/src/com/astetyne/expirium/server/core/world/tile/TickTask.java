package com.astetyne.expirium.server.core.world.tile;

public class TickTask implements Comparable<TickTask> {

    public Runnable runnable;
    public long tick;

    public TickTask(Runnable runnable, long tick) {
        this.runnable = runnable;
        this.tick = tick;
    }

    @Override
    public int compareTo(TickTask other){
        return Long.compare(tick, other.tick);
    }

}
