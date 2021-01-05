package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.IntVector2;
import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class PacketInputStream {

    protected InputStream in;
    private final ByteBuffer buffer1;
    private final ByteBuffer buffer2;
    private ByteBuffer writeBuffer;
    private ByteBuffer readBuffer;
    private int availablePackets;
    private int lastAvailablePackets;
    private int ping;
    private int lastPing;

    public PacketInputStream(InputStream in) {
        this.in = in;
        buffer1 = ByteBuffer.allocate(Consts.BUFFER_SIZE);
        buffer2 = ByteBuffer.allocate(Consts.BUFFER_SIZE);
        writeBuffer = buffer1;
        readBuffer = buffer2;
    }

    public byte getByte() {
        return readBuffer.get();
    }

    public boolean getBoolean() {
        return readBuffer.get() == 1;
    }

    public int getInt() {
        return readBuffer.getInt();
    }

    public float getFloat() {
        return readBuffer.getFloat();
    }

    public String getString() {
        int len = readBuffer.getInt();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < len; i++) {
            sb.append(readBuffer.getChar());
        }
        return sb.toString();
    }

    public Vector2 getVector() {
        return new Vector2(readBuffer.getFloat(), readBuffer.getFloat());
    }

    public IntVector2 getIntVector() {
        return new IntVector2(readBuffer.getInt(), readBuffer.getInt());
    }

    public int fillBuffer() throws IOException {
        int readBytes =  in.read(writeBuffer.array(), 0, writeBuffer.capacity());
        availablePackets = writeBuffer.getInt();
        ping = (int) (System.currentTimeMillis() - writeBuffer.getLong());
        //System.out.println("Reading: "+readBytes);
        return readBytes;
    }

    public void reset() {
        buffer1.clear();
        buffer2.clear();
        writeBuffer = buffer1;
        readBuffer = buffer2;
        availablePackets = 0;
        lastAvailablePackets = 0;
        ping = 0;
        lastPing = 0;
    }

    public void swap() {

        readBuffer.clear();

        if(readBuffer == buffer1) {
            readBuffer = buffer2;
            writeBuffer = buffer1;
        }else {
            readBuffer = buffer1;
            writeBuffer = buffer2;
        }

        lastAvailablePackets = availablePackets;
        availablePackets = 0;
        lastPing = ping;
    }

    public int getAvailablePackets() {
        return lastAvailablePackets;
    }

    public int getPing() {
        return ping;
    }

    public void skip(int i) {
        readBuffer.position(readBuffer.position() + i);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("First buff 10: ");
        for(int i = 0; i < 10; i++) {
            s.append(buffer1.array()[i]);
            s.append(" ");
        }
        s.append("\n Second buff 10: ");
        for(int i = 0; i < 10; i++) {
            s.append(buffer2.array()[i]);
            s.append(" ");
        }
        return s.toString();
    }
}
