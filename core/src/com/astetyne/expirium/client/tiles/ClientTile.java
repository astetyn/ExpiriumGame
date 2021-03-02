package com.astetyne.expirium.client.tiles;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.world.WeatherType;
import com.astetyne.expirium.server.core.world.tile.Material;

public class ClientTile {

    private Material type;
    private byte stability;
    private byte skyLight, localLight;
    private boolean backWall;
    private byte waterLevel;

    public ClientTile(Material type, byte stability) {
        this.type = type;
        this.stability = stability;
        skyLight = 0;
        localLight = 0;
        waterLevel = 0;
    }

    public int getStability() {
        return stability;
    }

    public void setStability(byte stability) {
        this.stability = stability;
    }

    public Material getMaterial() {
        return type;
    }

    public void setMaterial(Material type) {
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

    public byte getTimeCompensatedSkyLight(int time) {

        float moonLightFactor = 0.3f;

        int srs = Consts.SUNRISE_START;
        float srd = Consts.SUNRISE_END - Consts.SUNRISE_START;
        int sre = Consts.SUNRISE_END;

        int sss = Consts.SUNSET_START;
        float ssd = Consts.SUNSET_END - Consts.SUNSET_START;
        int sse = Consts.SUNSET_END;

        // sunrise
        if(time >= srs && time < sre) return (byte) Math.max(skyLight / srd * (time - srs), skyLight * moonLightFactor);

        //daylight
        else if(time >= sre && time < sss) return skyLight;

        //sunset
        else if(time >= sss && time < sse) return (byte) Math.max(skyLight / ssd * (sse - time), skyLight * moonLightFactor);

        //night
        else return (byte) (skyLight * moonLightFactor);
    }

    /**
     * This method calculates with time compensated sky light and local lights.
     *
     * @return Real light level, which can be displayed on screen.
     */
    public int getLight(int time, WeatherType weatherType) {
        byte skyLight = getTimeCompensatedSkyLight(time);
        if(weatherType == WeatherType.RAIN) {
            skyLight -= 2;
            skyLight = (byte) Math.max(skyLight, 0);
        }
        return Math.max(skyLight, localLight);
    }

    public boolean hasBackWall() {
        return backWall;
    }

    public void setBackWall(boolean backWall) {
        this.backWall = backWall;
    }

    public byte getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(byte waterLevel) {
        this.waterLevel = waterLevel;
    }
}
