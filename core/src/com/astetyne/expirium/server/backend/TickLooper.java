package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.TickListener;

import java.util.List;

public class TickLooper extends TerminableLooper {

    private final Object tickLock;
    private final int tps;
    private final ExpiServer server;

    public TickLooper(int tps, ExpiServer server) {
        this.server = server;
        this.tps = tps;
        tickLock = new Object();
    }

    @Override
    public void run() {

        while(isRunning()) {

            server.getServerGateway().resolveJoiningAndLeavingPlayers();

            for(ExpiPlayer ep : server.getPlayers()) {
                ep.getNetManager().processIncomingPackets();
            }

            server.getWorld().onTick();

            float delta = 1f / Consts.SERVER_DEFAULT_TPS;
            List<TickListener> list = server.getEventManager().getTickListeners();
            for(int i = list.size() - 1; i >= 0; i--) {
                //todo: this is ideal, not real
                list.get(i).onTick(delta);
            }

            server.onTick(); // this must be last since it can close the server (dispose world)

            // wakes up all clients threads and send new actions
            synchronized(tickLock) {
                tickLock.notifyAll();
            }

            try {
                //noinspection BusyWait
                Thread.sleep(1000 / tps);
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
