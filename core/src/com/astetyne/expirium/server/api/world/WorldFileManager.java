package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.*;

public class WorldFileManager {

    private static final String worldsPath = "worlds/";
    private static final String playersPath = "/players/";

    private final String worldName;

    public WorldFileManager(String worldName) {
        this.worldName = worldName;
    }

    public DataInputStream loadWorld() throws WorldLoadingException {

        FileHandle file = Gdx.files.local(worldsPath+ worldName);

        if(!file.exists()) throw new WorldLoadingException("World does not exists.");

        return new DataInputStream(new BufferedInputStream(file.read()));
    }

    public void saveWorld(ExpiWorld world) throws IOException {

        FileHandle file = Gdx.files.local(worldsPath+ worldName);

        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(file.write(false)));
        world.writeData(out);
        out.close();
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
