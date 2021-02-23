package com.astetyne.expirium.server;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.entity.player.Player;

public class TickLooper extends TerminableLooper {

    private final Object tickLock;
    private final ExpiServer server;
    private long lastLagWarningTime;

    public TickLooper(ExpiServer server) {
        this.server = server;
        tickLock = new Object();
        lastLagWarningTime = 0;
    }

    @Override
    public void run() {

        long startT, diff;

        while(isRunning()) {

            startT = System.currentTimeMillis();

            server.getServerGateway().resolveJoiningAndLeavingPlayers();

            for(Player ep : server.getPlayers()) {
                ep.getNetManager().processIncomingPackets();
            }

            server.getWorld().onTick();

            /*float delta = 1f / Consts.SERVER_TPS;
            List<TickListener> list = server.getEventManager().getTickListeners();
            for(int i = list.size() - 1; i >= 0; i--) {
                //todo: delta is ideal, not real
                list.get(i).onTick(delta);
            }*/

            server.getFileManager().onTick();

            server.onTick(); // this must be last since it can close the server (dispose world)

            // wakes up all clients threads and send new actions
            synchronized(tickLock) {
                tickLock.notifyAll();
            }

            diff = System.currentTimeMillis() - startT;

            long waitMillis = (1000 / Consts.SERVER_TPS) - diff;

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
