package com.astetyne.expirium.server.net;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.ExpiColor;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.core.entity.Entity;
import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class PacketOutputStream {

    protected OutputStream out;
    private final ByteBuffer buffer1;
    private final ByteBuffer buffer2;
    private ByteBuffer writeBuffer;
    private ByteBuffer readBuffer;

    private int activePacketCounter;
    private int lastPacketCounter;

    public PacketOutputStream(OutputStream out) {
        this.out = out;
        buffer1 = ByteBuffer.allocate(Consts.BUFFER_SIZE);
        buffer2 = ByteBuffer.allocate(Consts.BUFFER_SIZE);
        writeBuffer = buffer1;
        readBuffer = buffer2;

        activePacketCounter = 0;
        lastPacketCounter = 0;
        writeBuffer.putInt(0);
        writeBuffer.putInt(0);
        writeBuffer.putLong(0);
    }

    public void startPacket(int packetID) {
        activePacketCounter++;
        writeBuffer.putShort((short) packetID);
    }

    public void putByte(int b) {
        writeBuffer.put((byte) b);
    }

    public void putBoolean(boolean b) {
        writeBuffer.put((byte) (b ? 1 : 0));
    }

    public void putInt(int i) {
        writeBuffer.putInt(i);
    }

    public void putShort(short s) {
        writeBuffer.putShort(s);
    }

    public void putLong(long l) {
        writeBuffer.putLong(l);
    }

    public void putFloat(float f) {
        writeBuffer.putFloat(f);
    }

    public void putString(String s) {
        writeBuffer.putInt(s.length());
        for(char c : s.toCharArray()) {
            writeBuffer.putChar(c);
        }
    }

    public void putShortString(String s) {
        putByte(s.length());
        for(char c : s.toCharArray()) {
            writeBuffer.putChar(c);
        }
    }

    public void putVector(Vector2 vec) {
        putFloat(vec.x);
        putFloat(vec.y);
    }

    public void putIntVector(IntVector2 vec) {
        putInt(vec.x);
        putInt(vec.y);
    }

    public void putColor(ExpiColor c) {
        putByte(c.ordinal());
    }

    public void putEntity(Entity e) {
        putInt(e.getType().getID());
        putShort(e.getId());
        putFloat(e.getLocation().x);
        putFloat(e.getLocation().y);
        e.writeInitClientMeta(this);
    }

    public void flush() throws IOException {
        readBuffer.putInt(0, readBuffer.position());
        readBuffer.putInt(4, lastPacketCounter);
        readBuffer.putLong(8, System.currentTimeMillis());
        //System.out.println("packets: "+lastPacketCounter+" pos: "+readBuffer.position()+" packet counter: "+lastPacketCounter);
        //System.out.println("Flushing: "+readBuffer.position());
        out.write(readBuffer.array(), 0, readBuffer.position());
        out.flush();
    }

    public void reset() {
        buffer1.clear();
        buffer2.clear();
        writeBuffer = buffer1;
        readBuffer = buffer2;
        activePacketCounter = 0;
        lastPacketCounter = 0;
        writeBuffer.putInt(0);
        writeBuffer.putInt(0);
        writeBuffer.putLong(0);
        readBuffer.putInt(0);
        readBuffer.putInt(0);
        readBuffer.putLong(0);
    }

    // Call this method when you are done with writing and reading.
    public void swap() {

        readBuffer.clear();

        if(readBuffer == buffer1) {
            readBuffer = buffer2;
            writeBuffer = buffer1;
        }else {
            readBuffer = buffer1;
            writeBuffer = buffer2;
        }
        writeBuffer.putInt(0);
        writeBuffer.putInt(0);
        writeBuffer.putLong(0);
        lastPacketCounter = activePacketCounter;
        activePacketCounter = 0;
    }

    public float occupied() {
        return (float) writeBuffer.position() / readBuffer.capacity();
    }

}
