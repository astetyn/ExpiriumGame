package com.astetyne.main.net.client;

import com.astetyne.main.Constants;
import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.net.TerminableLooper;
import com.astetyne.main.net.client.actions.ClientAction;
import com.astetyne.main.net.client.actions.JoinRequestActionC;
import com.astetyne.main.net.server.ServerActionsPacket;
import com.astetyne.main.net.server.actions.ServerAction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientGateway extends TerminableLooper {

    private Socket socket;
    private final ExpiriumGame expiriumGame;
    private final List<ClientAction> clientActions;
    private final List<ServerAction> serverActions;

    public ClientGateway(ExpiriumGame expiriumGame) {
        this.expiriumGame = expiriumGame;
        clientActions = new ArrayList<>();
        serverActions = new ArrayList<>();
    }

    @Override
    public void run() {

        try {

            System.out.println("client connecting...");

            socket = new Socket(ExpiriumGame.getGame().getClientIpAddress(), Constants.SERVER_PORT);
            //socket = new Socket("192.168.0.114", Constants.SERVER_PORT);

            System.out.println("connection done");

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            JoinRequestActionC jra = new JoinRequestActionC("palko");
            ClientActionsPacket capInit = new ClientActionsPacket(Collections.<ClientAction>singletonList(jra));
            oos.writeObject(capInit);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            while(isRunning()) {

                ServerActionsPacket sap = (ServerActionsPacket) ois.readObject();

                synchronized(serverActions) {
                    serverActions.addAll(sap.getList());
                }

                List<ClientAction> copy;
                synchronized(clientActions) {
                    copy = new ArrayList<>(clientActions);
                    clientActions.clear();
                }
                oos.writeObject(new ClientActionsPacket(copy));

                expiriumGame.notifyServerTickLock();

            }

        }catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void end() {
        super.end();
        try {
            socket.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void addAction(ClientAction action) {
        synchronized(clientActions) {
            clientActions.add(action);
        }
    }

    // call this only once per server tick as list will be cleared after the call
    public List<ServerAction> getServerActions() {
        synchronized(serverActions) {
            List<ServerAction> copy = new ArrayList<>(serverActions);
            serverActions.clear();
            return copy;
        }
    }

}
