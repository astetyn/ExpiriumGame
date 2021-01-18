package com.astetyne.expirium.client.tiles;

import com.astetyne.expirium.client.screens.GameScreen;

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

    public byte getTimeCompensatedSkyLight() {
        float dayTime = GameScreen.get().getDayTime();
        byte moonLight = 2;
        if(dayTime >= 0 && dayTime < 25) return (byte) Math.max(skyLight / 25f * dayTime, moonLight);
        else if(dayTime >= 25 && dayTime < 600) return skyLight;
        else if(dayTime >= 600 && dayTime < 625) return (byte) Math.max(skyLight / 25f * (625 - dayTime), moonLight);
        else return moonLight;
    }

    /**
     * This method calculates with time compensated sky light and local lights.
     *
     * @return Real light level, which can be displayed on screen.
     */
    public byte getLight() {
        return (byte) Math.max(getTimeCompensatedSkyLight(), localLight);
    }
}
