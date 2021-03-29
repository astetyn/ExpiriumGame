package com.astetyne.expirium.server.net;

import com.astetyne.expirium.client.net.FailListener;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.ExpiColor;
import com.astetyne.expirium.client.utils.IntVector2;
import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class PacketInputStream {

    protected InputStream in;
    private FailListener failListener;
    private final ByteBuffer buffer1;
    private final ByteBuffer buffer2;
    private ByteBuffer writeBuffer;
    private ByteBuffer readBuffer;
    private int availablePackets;
    private int lastAvailablePackets;
    private int ping;
    private int lastPing;

    public PacketInputStream(InputStream in, FailListener failListener) {
        this.in = in;
        this.failListener = failListener;
        buffer1 = ByteBuffer.allocate(Consts.BUFFER_SIZE);
        buffer2 = ByteBuffer.allocate(Consts.BUFFER_SIZE);
        writeBuffer = buffer1;
        readBuffer = buffer2;
    }

    public byte getByte() {
        if(readBuffer.remaining() < 1) {
            failListener.onFail("underflow");
            return 0;
        }
        return readBuffer.get();
    }

    public char getChar() {
        if(readBuffer.remaining() < 2) {
            failListener.onFail("underflow");
            return 0;
        }
        return readBuffer.getChar();
    }

    public int getInt() {
        if(readBuffer.remaining() < 4) {
            failListener.onFail("underflow");
            return 0;
        }
        return readBuffer.getInt();
    }

    public short getShort() {
        if(readBuffer.remaining() < 2) {
            failListener.onFail("underflow");
            return 0;
        }
        return readBuffer.getShort();
    }

    public long getLong() {
        if(readBuffer.remaining() < 8) {
            failListener.onFail("underflow");
            return 0;
        }
        return readBuffer.getLong();
    }

    public float getFloat() {
        if(readBuffer.remaining() < 4) {
            failListener.onFail("underflow");
            return 0;
        }
        return readBuffer.getFloat();
    }

    public boolean getBoolean() {
        return getByte() == 1;
    }

    public String getString() {
        int len = getInt();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < len; i++) {
            sb.append(getChar());
        }
        return sb.toString();
    }

    public String getShortString() {
        int len = getByte();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < len; i++) {
            sb.append(getChar());
        }
        return sb.toString();
    }

    public Vector2 getVector() {
        return new Vector2(getFloat(), getFloat());
    }

    public IntVector2 getIntVector() {
        return new IntVector2(getInt(), getInt());
    }

    public ExpiColor getColor() {
        return ExpiColor.get(getByte());
    }

    public int fillBuffer() throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(4);
        in.read(bb.array(), 0, 4);
        int reqBytes = bb.getInt() - 4;
        int incomingBytes = reqBytes;
        while(reqBytes != 0) {
            int read = in.read(writeBuffer.array(), incomingBytes - reqBytes, reqBytes);
            if(read == -1) return -1;
            reqBytes -= read;
        }
        availablePackets = writeBuffer.getInt();
        ping = (int) (System.currentTimeMillis() - writeBuffer.getLong());
        return incomingBytes;
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

    public float occupied() {
        return (float) readBuffer.position() / readBuffer.capacity();
    }
}
