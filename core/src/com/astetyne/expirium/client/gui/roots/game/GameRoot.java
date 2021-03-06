package com.astetyne.expirium.client.gui.roots.game;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.data.HotSlotsData;
import com.astetyne.expirium.client.data.PlayerDataHandler;
import com.astetyne.expirium.client.gui.widget.HotBarSlot;
import com.astetyne.expirium.client.gui.widget.MoveThumbStick;
import com.astetyne.expirium.client.gui.widget.SwitchArrow;
import com.astetyne.expirium.client.gui.widget.ThumbStick;
import com.astetyne.expirium.client.resources.GuiRes;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.core.entity.player.LivingEffect;
import com.astetyne.expirium.server.core.world.inventory.ChosenSlot;
import com.astetyne.expirium.server.core.world.inventory.UIInteractType;
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

public class GameRoot extends WidgetGroup implements GameRootable {

    private final GameScreen game;

    private final Table debugInfoTable, playerStatsTable;

    private final Label fpsLabel, locationLabel, entityLabel, callsLabel, buffersLabel, versionLabel, healthStat, foodStat;
    private final Image healthImage, foodImage;
    private final HotBarSlot toolSlot, materialSlot, consumableSlot;
    private HotBarSlot focusedSlot, lastFocused;
    public final MoveThumbStick moveTS;
    public final ThumbStick breakTS;
    private final SwitchArrow switchArrowLeft, switchArrowRight;
    private final Image inventoryButton, consumeButton, buildViewButton, settingsButton;
    private Actor activeLeftActor;

    public GameRoot(GameScreen game) {

        this.game = game;

        if(Consts.DEBUG) setDebug(true);

        toolSlot = new HotBarSlot("Tools", ChosenSlot.TOOL_SLOT);
        materialSlot = new HotBarSlot("Mats", ChosenSlot.MATERIAL_SLOT);
        consumableSlot = new HotBarSlot("Food", ChosenSlot.CONSUMABLE_SLOT);

        toolSlot.setFocus(true);
        lastFocused = toolSlot;
        focusedSlot = toolSlot;

        switchArrowLeft = new SwitchArrow(Res.ARROW, UIInteractType.SWITCH_UP, false);
        switchArrowRight = new SwitchArrow(Res.ARROW, UIInteractType.SWITCH_DOWN, true);
        inventoryButton = new Image(GuiRes.INV.getDrawable());
        consumeButton = new Image(GuiRes.USE_ICON.getDrawable());
        buildViewButton = new Image(GuiRes.WARNING_ICON.getDrawable());
        settingsButton = new Image(GuiRes.SETTINGS_ICON.getDrawable());

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setRoot(new SettingsRoot(game));
            }
        });

        inventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ExpiGame.get().getClientGateway().getManager().putUIInteractPacket(UIInteractType.OPEN_INV);
                game.setRoot(new InventoryRoot(game));
            }
        });

        buildViewButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.toggleBuildViewActive();
            }
        });

        consumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ExpiGame.get().getNetManager().putUIInteractPacket(UIInteractType.CONSUME_BUTTON);
            }
        });

        moveTS = new MoveThumbStick(game.getPlayerData().getThumbStickData1(), Res.MOVE_THUMB_STICK_STYLE);
        breakTS = new ThumbStick(game.getPlayerData().getThumbStickData2(), Res.THUMB_STICK_STYLE);

        debugInfoTable = new Table();
        playerStatsTable = new Table();

        fpsLabel = new Label("", Res.LABEL_STYLE);
        locationLabel = new Label("", Res.LABEL_STYLE);
        entityLabel = new Label("", Res.LABEL_STYLE);
        callsLabel = new Label("", Res.LABEL_STYLE);
        buffersLabel = new Label("", Res.LABEL_STYLE);
        versionLabel = new Label(Consts.VERSION_TEXT, Res.LABEL_STYLE);
        versionLabel.setColor(1,0.1f,0.1f,1);
        versionLabel.setAlignment(Align.topLeft);

        healthImage = new Image(GuiRes.HEALTH_ICON.getDrawable());
        foodImage = new Image(GuiRes.FOOD_ICON.getDrawable());

        healthStat = new Label("0%", Res.LABEL_STYLE);
        foodStat = new Label("0%", Res.LABEL_STYLE);

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

        settingsButton.setBounds(10, 890, 100, Utils.percFromW(100));
        addActor(settingsButton);
        versionLabel.setBounds(140, 890, 200, 100);
        addActor(versionLabel);
        debugInfoTable.setBounds(0, 300, 500, 400);
        debugInfoTable.align(Align.topLeft);
        addActor(debugInfoTable);
        playerStatsTable.setBounds(1800, 850, 200, 150);
        playerStatsTable.align(Align.topRight);
        addActor(playerStatsTable);
        inventoryButton.setBounds(1870, 660, 100, Utils.percFromW(100));
        addActor(inventoryButton);
        moveTS.setBounds(120, 60, 320, Utils.percFromW(320));
        addActor(moveTS);

        int slotW = 120;
        int slotH = (int) Utils.percFromW(slotW);
        int arrowW = 100;
        int arrowH = (int) Utils.percFromW(arrowW);
        int gapBars = 20;
        int gapArrows = 40;
        int totalW = 2*arrowW + 2*gapBars + 2*gapArrows + 3*slotW;
        int leftB = 1000 - totalW/2;

        switchArrowLeft.setBounds(leftB, 50, arrowW, arrowH);
        toolSlot.setBounds(leftB + arrowW + gapArrows, 50, slotW, slotH);
        materialSlot.setBounds(leftB + arrowW + gapArrows + gapBars + slotW, 50, slotW, slotH);
        consumableSlot.setBounds(leftB + arrowW + gapArrows + 2*gapBars + 2*slotW, 50, slotW, slotH);
        switchArrowRight.setBounds(leftB + arrowW + 2*gapArrows + 2*gapBars + 3*slotW, 50, arrowW, arrowH);

        addActor(switchArrowLeft);
        addActor(toolSlot);
        addActor(materialSlot);
        addActor(consumableSlot);
        addActor(switchArrowRight);

        breakTS.setBounds(1540, 80, 320, Utils.percFromW(320));
        addActor(breakTS);
        activeLeftActor = breakTS;

        consumeButton.setBounds(1750, 50, 200, Utils.percFromW(200));
        buildViewButton.setBounds(1750, 50, 200, Utils.percFromW(200));

        refresh();

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if(Consts.DEBUG) {
            fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());
            Vector2 loc = game.getWorld().getPlayer().getLocation();
            locationLabel.setText("x: " + ((int) loc.x) + " y: " + ((int) loc.y));
            entityLabel.setText("entities: " + game.getWorld().getEntitiesID().keySet().size());
            callsLabel.setText(ExpiGame.get().getBatch().totalRenderCalls);
            ExpiGame.get().getBatch().totalRenderCalls = 0;
            String out = "out: " + Math.round(ExpiGame.get().getClientGateway().getOut().occupied() * 1000) / 10f + "%";
            String in = "in: " + Math.round(ExpiGame.get().getClientGateway().getIn().occupied() * 1000) / 10f + "%";
            buffersLabel.setText(out + " " + in);
        }

        for(int i = 0; i < game.getPlayerData().getActiveEffects().size(); i++) {
            LivingEffect effect = game.getPlayerData().getActiveEffects().get(i);
            float iconSize = 80;
            batch.draw(effect.getTex(), 2000 - (i+1)*iconSize - i*20, 770, iconSize, Utils.percFromW(iconSize));
        }

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

        PlayerDataHandler playerData = game.getPlayerData();
        HotSlotsData data = playerData.getHotSlotsData();

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

        healthStat.setText(playerData.getHealth()+" %");
        foodStat.setText(playerData.getFood()+" %");
    }

    @Override
    public boolean canInteractWithWorld() {
        return true;
    }
}
