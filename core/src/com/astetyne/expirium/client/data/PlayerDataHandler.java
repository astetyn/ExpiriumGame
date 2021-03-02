package com.astetyne.expirium.client.data;

import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.entity.player.LivingEffect;
import com.astetyne.expirium.server.net.PacketInputStream;

import java.util.ArrayList;
import java.util.List;

public class PlayerDataHandler {

    // inventory
    private final StorageGridData mainData, secondData;
    private final HotSlotsData hotSlotsData;
    private final ThumbStickData thumbStickData1, thumbStickData2;

    // living stats
    private byte health, food;
    private final List<LivingEffect> activeEffects;

    private final GameScreen game;

    public PlayerDataHandler(GameScreen game) {

        this.game = game;

        mainData = new StorageGridData();
        secondData = new StorageGridData();
        hotSlotsData = new HotSlotsData();

        mainData.rows = Consts.PLAYER_INV_ROWS;
        mainData.columns = Consts.PLAYER_INV_COLUMNS;

        thumbStickData1 = new ThumbStickData();
        thumbStickData2 = new ThumbStickData();

        activeEffects = new ArrayList<>();
    }

    public void feedLivingStats(PacketInputStream in) {
        activeEffects.clear();
        health = in.getByte();
        food = in.getByte();
        byte effectsSize = in.getByte();
        for(int i = 0; i < effectsSize; i++) {
            LivingEffect effect = LivingEffect.get(in.getByte());
            activeEffects.add(effect);
        }
        game.getActiveRoot().refresh();
    }

    public void feedInventory(PacketInputStream in) {
        mainData.feed(in);
        secondData.feed(in);
        game.getActiveRoot().refresh();
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

    public byte getHealth() {
        return health;
    }

    public byte getFood() {
        return food;
    }

    public ThumbStickData getThumbStickData1() {
        return thumbStickData1;
    }

    public ThumbStickData getThumbStickData2() {
        return thumbStickData2;
    }

    public List<LivingEffect> getActiveEffects() {
        return activeEffects;
    }
}
