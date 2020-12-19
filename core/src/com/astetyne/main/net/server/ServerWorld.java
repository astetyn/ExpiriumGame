package com.astetyne.main.net.server;

import com.astetyne.main.net.client.actions.TileBreakActionC;
import com.astetyne.main.net.client.actions.TilePlaceActionCS;
import com.astetyne.main.net.netobjects.STile;
import com.astetyne.main.net.netobjects.SWorldChunk;
import com.astetyne.main.utils.Constants;
import com.astetyne.main.utils.Utils;
import com.astetyne.main.world.Noise;
import com.astetyne.main.world.TileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class ServerWorld implements Serializable {

    private SWorldChunk[] chunks;
    String worldName;
    private final long seed;

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

        System.out.println("Generating world...");

        chunks = new SWorldChunk[Constants.CHUNKS_NUMBER];

        int chunkWidth = Constants.T_W_CH;
        int chunkHeight = Constants.T_H_CH;

        for(int c = 0; c < chunks.length; c++) {

            STile[][] terrain = new STile[chunkHeight][chunkWidth];

            for(int j = 0; j < chunkWidth; j++) {

                int h = (int) (50 + Noise.noise((c*chunkWidth+j) / 32.0f, 0, 0) * 20);

                for(int i = 0; i < chunkHeight; i++) {
                    if(i == h) {
                        terrain[i][j] = new STile(TileType.GRASS);
                    }else if(i < h && i > h-5) {
                        terrain[i][j] = new STile(TileType.DIRT);
                    }else if(i < h) {
                        terrain[i][j] = new STile(TileType.STONE);
                    }else {
                        terrain[i][j] = new STile(TileType.AIR);
                    }
                }
            }
            chunks[c] = new SWorldChunk(c, terrain);
        }
        System.out.println("Generating world done!");
        System.out.println(Utils.sizeof(new SWorldChunk(0, chunks[0].getTerrain())));
    }

    private void recalculateStability(STile tile) {

        //todo: prepocitat stabilitu pre cely svet

    }

    public void onTileBreak(TileBreakActionC tba) {
        //todo: prepocitat stabilitu okolitych policok a podla toho vygenerovat TileBreakActions
    }

    public void tryToPlaceTile(TilePlaceActionCS tpa) {

        //todo: prepocitat stabilitu ci je mozne tam policko postavit

        //in case of success
        STile t = chunks[tpa.getChunkID()].getTerrain()[tpa.getY()][tpa.getX()];
        t.setType(tpa.getPlacedItem().initDefaultData().getType());
        GameServer.getServer().getTickLooper().getTickActions().add(tpa);

    }

    public Vector2 getSaveLocation() {
        int i = 0;
        while(i != Constants.T_H_CH && chunks[0].getTerrain()[i][10].getType() != TileType.AIR) {
            i++;
        }
        return new Vector2(10, i+2);
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
