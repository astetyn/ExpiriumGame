package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.event.TickListener;

import java.util.List;

public class TickLooper extends TerminableLooper {

    private final Object tickLock;
    private final int tps;
    private final ExpiServer server;
    private long lastLagWarningTime;

    public TickLooper(int tps, ExpiServer server) {
        this.server = server;
        this.tps = tps;
        tickLock = new Object();
        lastLagWarningTime = 0;
    }

    @Override
    public void run() {

        long startT, diff;

        while(isRunning()) {

            startT = System.currentTimeMillis();

            server.getServerGateway().resolveJoiningAndLeavingPlayers();

            for(ExpiPlayer ep : server.getPlayers()) {
                ep.getNetManager().processIncomingPackets();
            }

            float delta = 1f / Consts.SERVER_DEFAULT_TPS;

            server.getWorld().onTick(delta);

            List<TickListener> list = server.getEventManager().getTickListeners();
            for(int i = list.size() - 1; i >= 0; i--) {
                //todo: delta is ideal, not real
                list.get(i).onTick(delta);
            }

            server.getFileManager().onTick();

            server.onTick(); // this must be last since it can close the server (dispose world)

            // wakes up all clients threads and send new actions
            synchronized(tickLock) {
                tickLock.notifyAll();
            }

            diff = System.currentTimeMillis() - startT;

            long waitMillis = (1000 / tps) - diff;

            if(waitMillis < 0) {
                if(lastLagWarningTime + 5000 < System.currentTimeMillis()) {
                    System.out.println("TickLooper: Can't keep up! Server is overloaded.");
                    lastLagWarningTime = System.currentTimeMillis();
                }
                continue; // no thread sleep, no wait
            }

            try {
                //noinspection BusyWait
                Thread.sleep(waitMillis);
            }catch(InterruptedException ignored) {
                server.faultClose();
                break;
            }
        }
        System.out.println("Tick looper ended.");
    }

    public Object getTickLock() {
        return tickLock;
    }

    public int getAverageTps() {
        return 0; //todo not implemented yet
    }
}
