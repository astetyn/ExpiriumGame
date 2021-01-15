package com.astetyne.expirium.client.data;

import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.net.PacketInputStream;

public class PlayerDataHandler {

    // inventory
    private final StorageGridData mainData, secondData;
    private final HotSlotsData hotSlotsData;
    private final ThumbStickData thumbStickData1, thumbStickData2;

    // living stats
    private float health, food, temperature;

    public PlayerDataHandler() {
        mainData = new StorageGridData();
        secondData = new StorageGridData();
        hotSlotsData = new HotSlotsData();

        mainData.rows = Consts.PLAYER_INV_ROWS;
        mainData.columns = Consts.PLAYER_INV_COLUMNS;

        thumbStickData1 = new ThumbStickData();
        thumbStickData2 = new ThumbStickData();
    }

    public void feedLivingStats(PacketInputStream in) {
        health = in.getFloat();
        food = in.getFloat();
        temperature = in.getFloat();
        GameScreen.get().getActiveRoot().refresh();
    }

    public void feedInventory(PacketInputStream in) {
        mainData.feed(in);
        secondData.feed(in);
        GameScreen.get().getActiveRoot().refresh();
    }

    public StorageGridData getMainData() {
        return mainData;
    }

    public StorageGridData getSecondData() {
        return secondData;
    }

    public HotSlotsData getHotSlotsData() {
        return hotSlotsData;
    }

    public float getHealth() {
        return health;
    }

    public float getFood() {
        return food;
    }

    public float getTemperature() {
        return temperature;
    }

    public ThumbStickData getThumbStickData1() {
        return thumbStickData1;
    }

    public ThumbStickData getThumbStickData2() {
        return thumbStickData2;
    }
}
