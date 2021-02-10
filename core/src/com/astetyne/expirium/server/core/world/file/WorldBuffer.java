package com.astetyne.expirium.server.core.world.file;

import com.astetyne.expirium.client.tiles.Material;

import java.nio.ByteBuffer;

public class WorldBuffer {

    private final ByteBuffer buffer;

    public WorldBuffer(int size) {
        buffer = ByteBuffer.allocate(size);
    }

    public void writeByte(byte b) {
        buffer.put(b);
    }

    public void writeBoolean(boolean b) {
        buffer.put(b ? (byte)1 : (byte)0);
    }

    public void writeShort(short s) {
        buffer.putShort(s);
    }

    public void writeInt(int i) {
        buffer.putInt(i);
    }

    public void writeLong(long l) {
        buffer.putLong(l);
    }

    public void writeChar(char c) {
        buffer.putChar(c);
    }

    public void writeFloat(float f) {
        buffer.putFloat(f);
    }

    public void writeDouble(double d) {
        buffer.putDouble(d);
    }

    public void writeString(String s) {
        buffer.putInt(s.length());
        for(char c : s.toCharArray()) {
            buffer.putChar(c);
        }
    }

    public void writeMaterial(Material material) {
        buffer.putInt(material.getID());
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
