package com.astetyne.expirium.server.api.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.*;

public class WorldFileManager {

    private static final String worldsPath = "worlds/";

    private final ExpiWorld world;

    public WorldFileManager(ExpiWorld world) {
        this.world = world;
    }

    public void loadWorld(String name) throws WorldLoadingException {

        FileHandle file = Gdx.files.local(worldsPath+name);

        if(!file.exists()) {
            throw new WorldLoadingException("World does not exists.");
        }

        DataInputStream in = new DataInputStream(new BufferedInputStream(file.read()));
        try {
            world.readData(in);
            in.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void saveWorld(String name) throws IOException {

        FileHandle file = Gdx.files.local(worldsPath+name);

        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(file.write(false)));
        world.writeData(out);
        try {
            out.close();
        }catch(IOException e) {
            e.printStackTrace();
        }

    }

}
