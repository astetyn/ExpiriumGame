package com.astetyne.expirium.main;

import com.astetyne.expirium.main.items.HotSlotsData;
import com.astetyne.expirium.main.items.StorageGridData;
import com.astetyne.expirium.main.utils.Consts;

public class InventoryHandler {

    private final StorageGridData mainData, secondData;
    private final HotSlotsData hotSlotsData;

    public InventoryHandler() {
        mainData = new StorageGridData();
        secondData = new StorageGridData();
        hotSlotsData = new HotSlotsData();

        mainData.rows = Consts.PLAYER_INV_ROWS;
        mainData.columns = Consts.PLAYER_INV_COLUMNS;
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
}
