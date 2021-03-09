package com.astetyne.expirium.server.core.world.file;

import com.astetyne.expirium.client.resources.PlayerCharacter;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.WeatherType;
import com.astetyne.expirium.server.core.world.generator.WorldGenerator;
import com.astetyne.expirium.server.net.ServerPlayerGateway;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
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

        int width = Consts.WORLD_BIOME_WIDTH * Consts.WORLD_BIOMES_NUMBER;
        int height = Consts.WORLD_HEIGHT;
        long seed = (long) (Math.random() * Long.MAX_VALUE);
        long tick = Consts.TICKS_IN_HOUR * 7;

        WorldGenerator gen = new WorldGenerator(width, height, seed);
        gen.generateWorld();

        WorldBuffer wb = new WorldBuffer(2097152);// 4194304

        wb.writeInt(width);
        wb.writeInt(height);
        wb.writeLong(seed);

        wb.writeBoolean(true);

        gen.writeData(wb);

        wb.writeByte((byte) WeatherType.SUN.ordinal()); // weather type
        wb.writeLong(tick + Consts.TICKS_IN_HOUR * (24 + (int) (Math.random() * 48))); // weather change tick

        wb.writeInt(0); // number of entities

        saveQuickInfo(tick, true);

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
        return new DataInputStream(new BufferedInputStream(file.read(), (int) file.length()+1));
    }

    public void saveServer() {

        saveQuickInfo(server.getWorld().getTick(), server.getPlayers().get(0).isLivingFirstLife());

        WorldBuffer wb = new WorldBuffer(2097152);
        server.writeData(wb);
        saveAsync(wb);

        for(Player p : server.getPlayers()) {
            savePlayer(p);
        }

    }

    public Player loadPlayer(ServerPlayerGateway gateway, String name, PlayerCharacter character) {
        FileHandle file = Gdx.files.local(path+playersPath+name);

        try {
            InputStream is;

            if(!file.exists()) {
                WorldBuffer buff = new WorldBuffer(2048);
                Vector2 loc = server.getWorld().getSpawnLocation();
                Player.writeDefaultData(buff, loc);
                is = new ByteArrayInputStream(buff.getBuffer().array());
            }else {
                is = new BufferedInputStream(file.read());
            }

            DataInputStream in = new DataInputStream(is);

            Player p = new Player(server, gateway, name, character, in);
            in.close();
            p.createBodyFixtures();
            server.getPlayers().add(p);
            for(Player p2 : server.getPlayers()) {
                if(p == p2) continue;
                p2.getNetManager().putEntitySpawnPacket(p);
            }
            return p;
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void savePlayer(Player p) {
        try {

            WorldBuffer wb = new WorldBuffer(2048);
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
            out.writeInt(Consts.VERSION);
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
