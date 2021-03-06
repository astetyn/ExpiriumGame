package com.astetyne.expirium.server.core.world.file;

import com.astetyne.expirium.server.core.world.tile.Material;

import java.nio.ByteBuffer;

public class WorldBuffer {

    private ByteBuffer buffer;

    public WorldBuffer(int initSize) {
        buffer = ByteBuffer.allocate(initSize);
    }

    public void writeByte(byte b) {
        checkCapacity(1);
        buffer.put(b);
    }

    public void writeBoolean(boolean b) {
        checkCapacity(1);
        buffer.put(b ? (byte)1 : (byte)0);
    }

    public void writeShort(short s) {
        checkCapacity(2);
        buffer.putShort(s);
    }

    public void writeInt(int i) {
        checkCapacity(4);
        buffer.putInt(i);
    }

    public void writeLong(long l) {
        checkCapacity(8);
        buffer.putLong(l);
    }

    public void writeChar(char c) {
        checkCapacity(2);
        buffer.putChar(c);
    }

    public void writeFloat(float f) {
        checkCapacity(4);
        buffer.putFloat(f);
    }

    public void writeDouble(double d) {
        checkCapacity(8);
        buffer.putDouble(d);
    }

    public void writeString(String s) {
        checkCapacity(4 + s.length()*2);
        buffer.putInt(s.length());
        for(char c : s.toCharArray()) {
            buffer.putChar(c);
        }
    }

    public void writeMaterial(Material material) {
        checkCapacity(4);
        buffer.putInt(material.ordinal());
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    private void checkCapacity(int newBytes) {
        while(buffer.position() + newBytes > buffer.capacity()) {
            ByteBuffer bb = ByteBuffer.allocate(buffer.capacity() * 2);
            bb.put(buffer.array(), 0, buffer.position());
            buffer = bb;
        }
    }
}
