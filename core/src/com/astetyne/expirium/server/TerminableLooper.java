package com.astetyne.expirium.server;

public abstract class TerminableLooper implements Runnable {

    private final Object runningLock;
    protected boolean running;

    public TerminableLooper() {
        runningLock = new Object();
        running = true;
    }

    public boolean isRunning() {
        synchronized(runningLock) {
            return running;
        }
    }

    public void stop() {
        synchronized(runningLock) {
            running = false;
        }
    }

}
