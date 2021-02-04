package com.astetyne.expirium.client.tiles;

import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Consts;

public class Tile {

    private TileType type;
    private byte stability;
    private byte skyLight, localLight;
    private boolean backWall;

    public Tile(TileType type, byte stability) {
        this.type = type;
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

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
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
        float moonLightFactor = 0.2f;

        int srs = Consts.SUNRISE_START;
        float srd = Consts.SUNRISE_END - Consts.SUNRISE_START;
        int sre = Consts.SUNRISE_END;

        int sss = Consts.SUNSET_START;
        float ssd = Consts.SUNSET_END - Consts.SUNSET_START;
        int sse = Consts.SUNSET_END;

        // sunrise
        if(dayTime >= srs && dayTime < sre) return (byte) Math.max(skyLight / srd * (dayTime - srs), skyLight * moonLightFactor);

        //daylight
        else if(dayTime >= sre && dayTime < sss) return skyLight;

        //sunset
        else if(dayTime >= sss && dayTime < sse) return (byte) Math.max(skyLight / ssd * (sse - dayTime), skyLight * moonLightFactor);

        //night
        else return (byte) (skyLight * moonLightFactor);
    }

    /**
     * This method calculates with time compensated sky light and local lights.
     *
     * @return Real light level, which can be displayed on screen.
     */
    public byte getLight() {
        return (byte) Math.max(getTimeCompensatedSkyLight(), localLight);
    }

    public boolean hasBackWall() {
        return backWall;
    }

    public void setBackWall(boolean backWall) {
        this.backWall = backWall;
    }
}
