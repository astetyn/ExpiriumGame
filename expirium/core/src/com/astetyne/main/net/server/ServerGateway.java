package com.astetyne.main.net.server;

import com.astetyne.main.Constants;
import com.astetyne.main.net.TerminableLooper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerGateway extends TerminableLooper {

    private ServerSocket server;

    public ServerGateway() {

    }

    @Override
    public void run() {

        try {

            server = new ServerSocket(Constants.SERVER_PORT);

            while(isRunning()) {

                System.out.println("listening for incoming clients...");
                Socket client = server.accept();
                new Thread(new ServerPlayerGateway(client)).start();

            }

        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void end() {
        super.end();
        try {
            server.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
