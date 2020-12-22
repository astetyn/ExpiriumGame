package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.main.utils.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerGateway extends TerminableLooper {

    private ServerSocket server;

    @Override
    public void run() {

        try {

            server = new ServerSocket(Constants.SERVER_PORT);

            while(isRunning()) {

                System.out.println("Listening for incoming clients...");
                Socket client = server.accept();
                new Thread(new ServerPlayerGateway(client)).start();

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
