package com.astetyne.main.net.client;

import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.net.TerminableLooper;
import com.astetyne.main.net.client.actions.JoinRequestActionC;
import com.astetyne.main.net.netobjects.MessageAction;
import com.astetyne.main.net.server.ServerActionsPacket;
import com.astetyne.main.utils.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientGateway extends TerminableLooper {

    private Socket socket;
    private final List<MessageAction> clientActions;
    private final List<MessageAction> serverActions;
    private final ExpiriumGame game;
    private String ipAddress;

    public ClientGateway() {
        clientActions = new ArrayList<>();
        serverActions = new ArrayList<>();
        game = ExpiriumGame.get();
        ipAddress = "127.0.0.1";
    }

    @Override
    public void run() {

        System.out.println("Client connecting...");

        try {

            socket = new Socket();
            socket.setPerformancePreferences(0,10,0);
            socket.connect(new InetSocketAddress(ipAddress, Constants.SERVER_PORT), 10000);

        } catch(IOException e) {
            // cant connect to the server
            System.out.println("Exception during connecting to server.");
            game.getCurrentStage().onServerFail();
            return;
        }

        try {

            System.out.println("Connection established.");

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            JoinRequestActionC jra = new JoinRequestActionC(game.getPlayerName());
            ClientActionsPacket capInit = new ClientActionsPacket(Collections.<MessageAction>singletonList(jra));
            oos.writeObject(capInit);

            socket.setSoTimeout(5000);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            while(isRunning()) {

                socket.setSoTimeout(5000);
                ServerActionsPacket sap = (ServerActionsPacket) ois.readObject();

                synchronized(serverActions) {
                    serverActions.addAll(sap.getList());
                }

                List<MessageAction> copy;
                synchronized(clientActions) {
                    copy = new ArrayList<>(clientActions);
                    clientActions.clear();
                }

                /*System.out.println("Client packet size: "+ Utils.sizeof(new ClientActionsPacket(copy)));
                System.out.println("Client packet elements: "+ copy.size());*/
                oos.writeObject(new ClientActionsPacket(copy));

                game.notifyServerUpdate();

            }

        }catch(IOException | ClassNotFoundException e) {
            // exception during playing on server
            System.out.println("Exception during messaging with server.");
            game.getCurrentStage().onServerFail();
        }

    }

    @Override
    public void end() {
        super.end();
        try {
            if(socket != null) {
                socket.close();
            }
        }catch(IOException ignored) {
        }
        System.out.println("Client successfully closed socket.");
    }

    public void addAction(MessageAction action) {
        synchronized(clientActions) {
            clientActions.add(action);
        }
    }

    // call this only once per server tick as list will be cleared after the call
    public List<MessageAction> getServerActions() {
        synchronized(serverActions) {
            List<MessageAction> copy = new ArrayList<>(serverActions);
            serverActions.clear();
            return copy;
        }
    }

    public void setIpAddress(String address) {
        ipAddress = address;
    }

}
