package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.world.generator.WorldLoadingException;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.*;

public class WorldFileManager {

    private static final String worldsPath = "worlds/";
    private static final String worldDataPath = "/data/";
    private static final String playersPath = "/players/";

    private final ExpiServer server;
    private final String worldName;
    private long lastSaveTime;

    public WorldFileManager(ExpiServer server, String worldName) {
        this.server = server;
        this.worldName = worldName;
        lastSaveTime = System.currentTimeMillis();
    }

    /**
     * Do not forget to close the stream when you are finished with loading.
     */
    public DataInputStream loadGameServer() throws WorldLoadingException {
        FileHandle file = Gdx.files.local(worldsPath+worldName+worldDataPath);
        if(!file.exists()) throw new WorldLoadingException("World does not exists.");
        return new DataInputStream(new BufferedInputStream(file.read()));
    }

    public void onTick() {

        if(lastSaveTime + Consts.SAVE_INTERVAL < System.currentTimeMillis()) {
            saveGameServer();
            lastSaveTime = System.currentTimeMillis();
        }

    }

    public void saveGameServer() {

        System.out.println("Saving server...");

        try {

            FileHandle file = Gdx.files.local(worldsPath + worldName + worldDataPath);

            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(file.write(false)));
            server.writeData(out);
            out.close();

            for(ExpiPlayer p : server.getPlayers()) {
                savePlayer(p);
            }
        }catch(IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saving done.");

    }

    public DataInputStream getPlayerDataStream(String name) {
        FileHandle file = Gdx.files.local(worldsPath+worldName+playersPath+name);
        if(!file.exists()) return null;
        return new DataInputStream(new BufferedInputStream(file.read()));
    }

    public void savePlayer(ExpiPlayer p) throws IOException {
        FileHandle file = Gdx.files.local(worldsPath+worldName+playersPath+p.getName());
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(file.write(false)));
        p.writeData(out);
        out.close();
    }

}
