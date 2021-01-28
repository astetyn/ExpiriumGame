package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.Saveable;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.world.generator.WorldLoadingException;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.*;

public class WorldFileManager {

    private static final String worldsPath = "worlds/";
    private static final String worldDataPath = "/data/";
    private static final String playersPath = "/players/";

    private final String worldName;

    public WorldFileManager(String worldName) {
        this.worldName = worldName;
    }

    /**
     * Do not forget to close the stream when you are finished with loading.
     */
    public DataInputStream loadGameServer() throws WorldLoadingException {
        FileHandle file = Gdx.files.local(worldsPath+worldName+worldDataPath);
        if(!file.exists()) throw new WorldLoadingException("World does not exists.");
        return new DataInputStream(new BufferedInputStream(file.read()));
    }

    public void saveGameServer(ExpiServer server, Saveable saveable) throws IOException {

        System.out.println("Saving server...");

        FileHandle file = Gdx.files.local(worldsPath+worldName+worldDataPath);

        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(file.write(false)));
        saveable.writeData(out);
        out.close();

        for(ExpiPlayer p : server.getPlayers()) {
            savePlayer(p);
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
