package com.astetyne.expirium.client.tiles;

public class Tile {

    private TileType typeFront, typeBack;
    private byte stability;
    private byte lightR, lightG, lightB;

    public Tile(TileType type, byte stability) {
        this.typeFront = type;
        this.typeBack = TileType.AIR;
        this.stability = stability;
        lightR = lightG = lightB = -1; // 255: full light
    }

    public int getStability() {
        return stability;
    }

    public void setStability(byte stability) {
        this.stability = stability;
    }

    public TileType getTypeFront() {
        return typeFront;
    }

    public void setTypeFront(TileType type) {
        this.typeFront = type;
    }

    public TileType getTypeBack() {
        return typeBack;
    }

    public void setTypeBack(TileType typeBack) {
        this.typeBack = typeBack;
    }

    public void setLight(byte r, byte g, byte b) {
        lightR = r;
        lightG = g;
        lightB = b;
    }

    public byte getR() {
        return lightR;
    }

    public byte getG() {
        return lightG;
    }

    public byte getB() {
        return lightB;
    }
}
