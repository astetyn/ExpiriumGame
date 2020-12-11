package com.astetyne.main.net.server;

import com.astetyne.main.Constants;
import com.astetyne.main.net.netobjects.SWorldChunk;
import com.astetyne.main.world.TileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class ServerWorld implements Serializable {

    private SWorldChunk[] chunks;
    String worldName;
    private long seed;

    public ServerWorld(String worldName) {
        this(worldName, (long)(Math.random()*10000));
    }

    public ServerWorld(String worldName, long seed) {

        this.worldName = worldName;
        this.seed = seed;

        FileHandle file = Gdx.files.local("world/"+worldName+".txt");

        if(!file.exists()) {

            generateWorld();

        }else {

            try {
                ObjectInputStream ois = new ObjectInputStream(file.read());

                ServerWorld savedWorld = (ServerWorld) ois.readObject();
                chunks = savedWorld.getChunks();
                seed = savedWorld.getSeed();

            }catch(IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    public void generateWorld() {

        System.out.println("generating world...");

        chunks = new SWorldChunk[Constants.CHUNKS_NUMBER];

        int chunkWidth = Constants.TILES_WIDTH_CHUNK;
        int chunkHeight = Constants.TILES_HEIGHT_CHUNK;

        for(int c = 0; c < chunks.length; c++) {

            TileType[][] terrain = new TileType[chunkHeight][chunkWidth];

            for(int i = 0; i < chunkHeight; i++) {
                for(int j = 0; j < chunkWidth; j++) {
                    terrain[i][j] = TileType.AIR;
                    if(i<=10) {
                        terrain[i][j] = TileType.STONE;
                    }
                }
            }
            chunks[c] = new SWorldChunk(c, terrain);
        }
        System.out.println("generating world done!");

    }

    public SWorldChunk getChunk(int id) {
        return chunks[id];
    }

    public void saveWorld() {

    }

    public SWorldChunk[] getChunks() {
        return chunks;
    }

    public String getWorldName() {
        return worldName;
    }

    public long getSeed() {
        return seed;
    }
}
