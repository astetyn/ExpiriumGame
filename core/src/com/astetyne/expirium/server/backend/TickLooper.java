package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.TickListener;

import java.util.List;

public class TickLooper extends TerminableLooper {

    private final Object tickLock;
    private final List<ExpiPlayer> players;
    private final int tps;

    public TickLooper(int tps) {
        this.tps = tps;
        tickLock = new Object();
        players = GameServer.get().getPlayers();
    }

    @Override
    public void run() {

        try {

            while(isRunning()) {

                GameServer.get().getServerGateway().resolveJoiningAndLeavingPlayers();

                for(ExpiPlayer ep : players) {
                    ep.getNetManager().processIncomingPackets();
                }

                GameServer.get().onTick();

                List<TickListener> list = GameServer.get().getEventManager().getTickListeners();
                for(int i = list.size() - 1; i >= 0; i--) {
                    list.get(i).onTick();
                }

                // wakes up all clients threads and send new actions
                synchronized(tickLock) {
                    tickLock.notifyAll();
                }

                //noinspection BusyWait
                Thread.sleep(1000 / Consts.SERVER_DEFAULT_TPS);
            }

        }catch(InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Tick looper ended.");
    }

    public Object getTickLock() {
        return tickLock;
    }

    @Override
    public void end() {
        super.end();
    }

    public int getTps() {
        return tps;
    }
}
