package com.astetyne.expirium.server.backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerGateway extends TerminableLooper {

    private ServerSocket server;
    private final int port;

    public ServerGateway(int port) {
        this.port = port;
    }

    @Override
    public void run() {

        try {

            server = new ServerSocket(port);

            while(isRunning()) {

                System.out.println("Listening for incoming clients...");
                Socket client = server.accept();
                Thread t = new Thread(new ServerPlayerGateway(client));
                t.setName("Server (client) gateway");
                t.start();

            }

        }catch(IOException e) {
            try {
                if(server != null) server.close();
            }catch(IOException ignored) {
            }
            System.out.println("Server GW closed.");
        }
    }

    @Override
    public void end() {
        super.end();
        try {
            server.close();
        }catch(IOException ignored) {
        }
        System.out.println("Server GW closed.");
    }
}
