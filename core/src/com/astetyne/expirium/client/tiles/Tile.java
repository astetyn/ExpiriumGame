package com.astetyne.expirium.client.tiles;

public class Tile {

    private TileType typeFront, typeBack;
    private byte stability;
    private byte skyLight, localLight;

    public Tile(TileType type, byte stability) {
        this.typeFront = type;
        this.typeBack = TileType.AIR;
        this.stability = stability;
        skyLight = 0;
        localLight = 0;
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

    public byte getSkyLight() {
        return skyLight;
    }

    public void setSkyLight(byte skyLight) {
        this.skyLight = skyLight;
    }

    public byte getLocalLight() {
        return localLight;
    }

    public void setLocalLight(byte localLight) {
        this.localLight = localLight;
    }
}
