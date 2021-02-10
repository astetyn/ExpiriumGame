package com.astetyne.expirium.server.core.world.file;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.world.generator.WorldGenerator;
import com.astetyne.expirium.server.net.ServerPlayerGateway;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.*;
import java.nio.ByteBuffer;

public class WorldFileManager {

    private static final String path = "world/";
    private static final String dataPath = "/data.expi";
    private static final String quickInfoPath = "quick.expi";
    private static final String playersPath = "/players/";

    private final ExpiServer server;
    private long lastSaveTime;

    public WorldFileManager(ExpiServer server, boolean createNew) {
        this.server = server;
        lastSaveTime = System.currentTimeMillis();
        if(createNew) createNewWorld();
    }

    public void onTick() {

        if(lastSaveTime + Consts.SAVE_INTERVAL < System.currentTimeMillis()) {
            saveServer();
            lastSaveTime = System.currentTimeMillis();
        }

    }

    private void createNewWorld() {

        int width = 200;
        int height = 500;
        long seed = (long) (Math.random() * Long.MAX_VALUE);

        WorldGenerator gen = new WorldGenerator(width, height, seed);
        gen.generateWorld();

        WorldBuffer wb = new WorldBuffer(1048576);

        wb.writeInt(width);
        wb.writeInt(height);
        wb.writeLong(seed);

        gen.writeData(wb);

        wb.writeInt(0); // number of entities

        saveQuickInfo(0, true);

        saveSync(wb);

        FileHandle playersFH = Gdx.files.local(path+playersPath);
        playersFH.deleteDirectory();

    }

    private void saveAsync(WorldBuffer wb) {
        new Thread(() -> saveSync(wb)).start();
    }

    private void saveSync(WorldBuffer wb) {

        System.out.println("Saving server...");
        long start = System.nanoTime();

        try {

            FileHandle file = Gdx.files.local(path + dataPath);

            ByteBuffer bb = wb.getBuffer();

            BufferedOutputStream out = new BufferedOutputStream(file.write(false), bb.position());
            out.write(bb.array(), 0, bb.position());
            out.close();

        }catch(IOException e) {
            e.printStackTrace();
        }

        long end = System.nanoTime();
        System.out.println("Saving done. Took: "+(end-start)/1000000f+" ms");

    }

    /** Do not forget to close the stream when you are finished with loading.*/
    public DataInputStream getWorldInputStream() {
        FileHandle file = Gdx.files.local(path + dataPath);
        return new DataInputStream(new BufferedInputStream(file.read()));
    }

    public void saveServer() {

        saveQuickInfo(server.getWorld().getTick(), true);

        WorldBuffer wb = new WorldBuffer(1048576);
        server.writeData(wb);
        saveAsync(wb);

        for(ExpiPlayer p : server.getPlayers()) {
            savePlayer(p);
        }

    }

    public ExpiPlayer loadPlayer(ServerPlayerGateway gateway, String name) {
        FileHandle file = Gdx.files.local(path+playersPath+name);

        if(!file.exists()) return new ExpiPlayer(server, server.getWorld().getSpawnLocation(), gateway, name);

        DataInputStream in = new DataInputStream(new BufferedInputStream(file.read()));
        try {
            ExpiPlayer ep = new ExpiPlayer(server, gateway, name, in);
            in.close();
            return ep;
        }catch(IOException e) {
            e.printStackTrace();
            return new ExpiPlayer(server, server.getWorld().getSpawnLocation(), gateway, name);
        }
    }

    public void savePlayer(ExpiPlayer p) {
        try {

            WorldBuffer wb = new WorldBuffer(4096);
            p.writeData(wb);

            FileHandle file = Gdx.files.local(path + playersPath + p.getName());

            BufferedOutputStream out =  new BufferedOutputStream(file.write(false));
            out.write(wb.getBuffer().array(), 0, wb.getBuffer().position());
            out.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void saveQuickInfo(long tick, boolean firstLife) {
        try {

            FileHandle file = Gdx.files.local(path + quickInfoPath);
            DataOutputStream out = new DataOutputStream(file.write(false));
            out.writeLong(tick);
            out.writeBoolean(firstLife);
            out.writeInt(ExpiServer.version);
            out.close();

        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static WorldQuickInfo getQuickInfo() {

        try {

            FileHandle file = Gdx.files.local(path + quickInfoPath);
            if(!file.exists()) return null;
            DataInputStream in = new DataInputStream(new BufferedInputStream(file.read()));

            long tick = in.readLong();
            boolean firstLife = in.readBoolean();
            int version = in.readInt();
            in.close();

            return new WorldQuickInfo(tick, firstLife, version);

        }catch(IOException | GdxRuntimeException e) {
            return null;
        }

    }

}
