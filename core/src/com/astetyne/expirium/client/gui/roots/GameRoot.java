package com.astetyne.expirium.client.gui.roots;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.data.HotSlotsData;
import com.astetyne.expirium.client.data.PlayerDataHandler;
import com.astetyne.expirium.client.gui.widget.HotBarSlot;
import com.astetyne.expirium.client.gui.widget.SwitchArrow;
import com.astetyne.expirium.client.gui.widget.ThumbStick;
import com.astetyne.expirium.client.resources.GuiRes;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.api.world.inventory.ChosenSlot;
import com.astetyne.expirium.server.api.world.inventory.UIInteractType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class GameRoot extends WidgetGroup implements ExpiRoot {

    private final Table hotSlotsTable, debugInfoTable, playerStatsTable;

    private final Label fpsLabel, locationLabel, entityLabel, callsLabel, buffersLabel, versionLabel, healthStat, foodStat;
    private final Image healthImage, foodImage;
    private final HotBarSlot toolSlot, materialSlot, consumableSlot;
    private HotBarSlot focusedSlot, lastFocused;
    public final ThumbStick moveTS, breakTS;
    private final SwitchArrow switchArrowUp, switchArrowDown;
    private final Image inventoryButton, consumeButton, buildViewButton, settingsButton;
    private Actor activeLeftActor;

    public GameRoot() {

        if(Consts.DEBUG) setDebug(true);

        toolSlot = new HotBarSlot("Tools", ChosenSlot.TOOL_SLOT);
        materialSlot = new HotBarSlot("Mats", ChosenSlot.MATERIAL_SLOT);
        consumableSlot = new HotBarSlot("Misc", ChosenSlot.CONSUMABLE_SLOT);

        toolSlot.setFocus(true);
        lastFocused = toolSlot;
        focusedSlot = toolSlot;

        switchArrowUp = new SwitchArrow(Res.SWITCH_ARROW_STYLE, UIInteractType.SWITCH_UP, false);
        switchArrowDown = new SwitchArrow(Res.SWITCH_ARROW_STYLE, UIInteractType.SWITCH_DOWN, true);
        inventoryButton = new Image(GuiRes.INV.getDrawable());
        consumeButton = new Image(GuiRes.USE_ICON.getDrawable());
        buildViewButton = new Image(GuiRes.WARNING_ICON.getDrawable());
        settingsButton = new Image(GuiRes.SETTINGS_ICON.getDrawable());

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen.get().setRoot(new SettingsRoot());
            }
        });

        inventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ExpiGame.get().getClientGateway().getManager().putUIInteractPacket(UIInteractType.OPEN_INV);
                GameScreen.get().setRoot(new InventoryRoot());
            }
        });

        buildViewButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen.get().toggleBuildViewActive();
            }
        });

        consumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ExpiGame.get().getNetManager().putUIInteractPacket(UIInteractType.CONSUME_BUTTON);
            }
        });

        moveTS = new ThumbStick(GameScreen.get().getPlayerData().getThumbStickData1(), Res.THUMB_STICK_STYLE);
        breakTS = new ThumbStick(GameScreen.get().getPlayerData().getThumbStickData2(), Res.THUMB_STICK_STYLE);

        hotSlotsTable = new Table();
        debugInfoTable = new Table();
        playerStatsTable = new Table();

        fpsLabel = new Label("", Res.LABEL_STYLE);
        locationLabel = new Label("", Res.LABEL_STYLE);
        entityLabel = new Label("", Res.LABEL_STYLE);
        callsLabel = new Label("", Res.LABEL_STYLE);
        buffersLabel = new Label("", Res.LABEL_STYLE);
        versionLabel = new Label(ExpiGame.version, Res.LABEL_STYLE);
        versionLabel.setColor(1,0.1f,0.1f,1);
        versionLabel.setAlignment(Align.topLeft);

        healthImage = new Image(GuiRes.HEALTH_ICON.getDrawable());
        foodImage = new Image(GuiRes.FOOD_ICON.getDrawable());

        healthStat = new Label("0%", Res.LABEL_STYLE);
        foodStat = new Label("0%", Res.LABEL_STYLE);

        hotSlotsTable.add(switchArrowUp).padBottom(10).colspan(3);
        hotSlotsTable.row();
        hotSlotsTable.add(toolSlot).padRight(20).width(120).height(Utils.percFromW(120));
        hotSlotsTable.add(materialSlot).padRight(20).width(120).height(Utils.percFromW(120));;
        hotSlotsTable.add(consumableSlot).width(120).height(Utils.percFromW(120));;
        hotSlotsTable.row();
        hotSlotsTable.add(switchArrowDown).padTop(10).colspan(3);

        if(Consts.DEBUG) {
            debugInfoTable.add(fpsLabel).left().height(50).padTop(10);
            debugInfoTable.row();
            debugInfoTable.add(locationLabel).left().height(50);
            debugInfoTable.row();
            debugInfoTable.add(entityLabel).left().height(50);
            debugInfoTable.row();
            debugInfoTable.add(callsLabel).left().height(50);
            debugInfoTable.row();
            debugInfoTable.add(buffersLabel).left().height(50);
            debugInfoTable.row();
        }

        float iconSize = 60;
        playerStatsTable.add(healthStat).padTop(10).height(50);
        playerStatsTable.add(healthImage).width(iconSize).height(Utils.percFromW(iconSize)).padTop(10).padRight(10);
        playerStatsTable.row();
        playerStatsTable.add(foodStat).padTop(10).height(50);
        playerStatsTable.add(foodImage).width(iconSize).height(Utils.percFromW(iconSize)).padTop(10).padRight(10);
        playerStatsTable.row();
        if(Consts.DEBUG) playerStatsTable.setDebug(true);

        settingsButton.setBounds(10, 890, 100, 100);
        addActor(settingsButton);
        versionLabel.setBounds(140, 890, 200, 100);
        addActor(versionLabel);
        debugInfoTable.setBounds(0, 300, 500, 400);
        if(Consts.DEBUG) debugInfoTable.setDebug(true);
        debugInfoTable.align(Align.topLeft);
        addActor(debugInfoTable);
        playerStatsTable.setBounds(1800, 850, 200, 150);
        playerStatsTable.align(Align.topRight);
        addActor(playerStatsTable);
        inventoryButton.setBounds(1870, 750, 100, 100);
        addActor(inventoryButton);
        moveTS.setBounds(50, 50, 200, 200);
        addActor(moveTS);
        hotSlotsTable.setBounds(700, 50, 600, 500);
        hotSlotsTable.align(Align.bottom);
        addActor(hotSlotsTable);

        breakTS.setBounds(1750, 50, 200, 200);
        addActor(breakTS);
        activeLeftActor = breakTS;

        consumeButton.setBounds(1750, 50, 200, 200);
        buildViewButton.setBounds(1750, 50, 200, 200);

        refresh();

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        fpsLabel.setText("fps: "+Gdx.graphics.getFramesPerSecond());
        Vector2 loc = GameScreen.get().getWorld().getPlayer().getLocation();
        locationLabel.setText("x: "+((int)loc.x)+" y: "+((int)loc.y));
        entityLabel.setText("entities: "+ GameScreen.get().getWorld().getEntitiesID().keySet().size());
        callsLabel.setText(ExpiGame.get().getBatch().totalRenderCalls);
        ExpiGame.get().getBatch().totalRenderCalls = 0;
        String out = "out: "+Math.round(ExpiGame.get().getClientGateway().getOut().occupied() * 1000) / 10f+"%";
        String in = "in: "+Math.round(ExpiGame.get().getClientGateway().getIn().occupied() * 1000) / 10f+"%";
        buffersLabel.setText(out+" "+in);

        super.draw(batch, parentAlpha);
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public boolean isDimmed() {
        return false;
    }

    @Override
    public void refresh() {

        HotSlotsData data = GameScreen.get().getPlayerData().getHotSlotsData();

        switch(data.getChosenSlot()) {
            case TOOL_SLOT:
                if(toolSlot.isFocused()) break;
                toolSlot.setFocus(true);
                materialSlot.setFocus(false);
                consumableSlot.setFocus(false);
                focusedSlot = toolSlot;
                removeActor(activeLeftActor);
                addActor(breakTS);
                activeLeftActor = breakTS;
                break;
            case MATERIAL_SLOT:
                if(materialSlot.isFocused()) break;
                toolSlot.setFocus(false);
                materialSlot.setFocus(true);
                consumableSlot.setFocus(false);
                focusedSlot = materialSlot;
                removeActor(activeLeftActor);
                addActor(buildViewButton);
                activeLeftActor = buildViewButton;
                break;
            case CONSUMABLE_SLOT:
                if(consumableSlot.isFocused()) break;
                toolSlot.setFocus(false);
                materialSlot.setFocus(false);
                consumableSlot.setFocus(true);
                focusedSlot = consumableSlot;
                removeActor(activeLeftActor);
                addActor(consumeButton);
                activeLeftActor = consumeButton;
                break;
        }
        toolSlot.setItemStack(data.getIs1());
        materialSlot.setItemStack(data.getIs2());
        consumableSlot.setItemStack(data.getIs3());

        if(!lastFocused.isFocused()) {
            lastFocused = focusedSlot;
        }

        PlayerDataHandler playerData = GameScreen.get().getPlayerData();

        healthStat.setText((int)Math.ceil(playerData.getHealth())+" %");
        foodStat.setText((int)Math.ceil(playerData.getFood())+" %");
    }

    @Override
    public boolean canInteractWithWorld() {
        return true;
    }
}
