package com.astetyne.server.backend;

public abstract class TerminableLooper implements Runnable {

    private final Object runningLock;
    private boolean running;

    public TerminableLooper() {
        runningLock = new Object();
        running = true;
    }

    public boolean isRunning() {
        synchronized(runningLock) {
            return running;
        }
    }

    public void end() {
        synchronized(runningLock) {
            running = false;
        }
    }

}
