package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
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
        buffer1 = ByteBuffer.allocate(Constants.BUFFER_SIZE);
        buffer2 = ByteBuffer.allocate(Constants.BUFFER_SIZE);
        writeBuffer = buffer1;
        readBuffer = buffer2;

        activePacketCounter = 0;
        lastPacketCounter = 0;
        writeBuffer.putInt(0);
    }

    public void startPacket(int packetID) {
        activePacketCounter++;
        writeBuffer.putInt(packetID);
    }

    public void putByte(byte b) {
        writeBuffer.put(b);
    }

    public void putInt(int i) {
        writeBuffer.putInt(i);
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

    public void putVector(Vector2 vec) {
        putFloat(vec.x);
        putFloat(vec.y);
    }

    public void putIntVector(IntVector2 vec) {
        putInt(vec.x);
        putInt(vec.y);
    }

    public void putEntity(ExpiEntity e) {
        putInt(e.getType().getID());
        putInt(e.getID());
        putFloat(e.getLocation().x);
        putFloat(e.getLocation().y);
        e.writeMeta(this);
    }

    public void putFixture(int id, float x1, float y1, float x2, float y2) {
        putInt(id);
        putFloat(x1);
        putFloat(y1);
        putFloat(x2);
        putFloat(y2);
    }

    public void flush() throws IOException {
        readBuffer.putInt(0, lastPacketCounter);
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
        readBuffer.putInt(0);
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
        lastPacketCounter = activePacketCounter;
        activePacketCounter = 0;
    }

}
