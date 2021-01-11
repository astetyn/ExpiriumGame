package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.server.api.Saveable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WorldSettings implements Saveable {

    public String name;
    public int width, height;
    public long seed;

    public WorldSettings() {}

    public WorldSettings(String name) {
        this.name = name;
    }

    public WorldSettings(String name, int width, int height, long seed) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.seed = seed;
    }

    @Override
    public void readData(DataInputStream in) throws IOException {
        width = in.readInt();
        height = in.readInt();
        seed = in.readLong();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeInt(width);
        out.writeInt(height);
        out.writeLong(seed);
    }
}
