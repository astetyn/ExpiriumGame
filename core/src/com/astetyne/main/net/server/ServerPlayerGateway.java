package com.astetyne.main.net.server;

import com.astetyne.main.net.TerminableLooper;
import com.astetyne.main.net.client.actions.ClientAction;
import com.astetyne.main.net.client.ClientActionsPacket;
import com.astetyne.main.net.server.actions.ServerAction;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerPlayerGateway extends TerminableLooper {

    private final GameServer gameServer;
    private final Socket client;
    private final List<ServerAction> serverActions;
    private final List<ClientAction> clientActions;
    private final Object joinLock;

    public ServerPlayerGateway(Socket client) {
        this.gameServer = GameServer.getServer();
        this.client = client;
        serverActions = new ArrayList<>();
        clientActions = new ArrayList<>();
        joinLock = new Object();
    }

    @Override
    public void run() {

        try {

            System.out.println("new client connected");

            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());

            ClientActionsPacket initCap = (ClientActionsPacket) ois.readObject();
            synchronized(clientActions) {
                clientActions.addAll(initCap.getList());
            }

            gameServer.playerPreJoinAsync(this);

            synchronized(joinLock) {
                joinLock.wait(); // wait until main looper recognize new player and populate init actions
            }

            while(isRunning()) {

                List<ServerAction> copy;
                synchronized(serverActions) {
                    copy = new ArrayList<>(serverActions);
                    serverActions.clear();
                }

                // send S actions packet
                oos.writeObject(new ServerActionsPacket(copy));

                // listen for C actions packet
                ClientActionsPacket cap = (ClientActionsPacket) ois.readObject();
                synchronized(clientActions) {
                    clientActions.addAll(cap.getList());
                }

                synchronized(gameServer.getTickLooper().getTickLock()) {
                    gameServer.getTickLooper().getTickLock().wait();
                }
            }

        }catch(IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void addServerAction(ServerAction action) {
        synchronized(serverActions) {
            serverActions.add(action);
        }
    }

    public void addServerActions(List<ServerAction> actions) {
        synchronized(serverActions) {
            serverActions.addAll(actions);
        }
    }

    // call this only once per server tick as list will be cleared after the call
    public List<ClientAction> getClientActions() {
        synchronized(clientActions) {
            List<ClientAction> copy = new ArrayList<>(clientActions);
            clientActions.clear();
            return copy;
        }
    }

    public Object getJoinLock() {
        return joinLock;
    }
}
