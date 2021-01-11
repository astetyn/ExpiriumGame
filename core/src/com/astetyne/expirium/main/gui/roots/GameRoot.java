package com.astetyne.expirium.main.gui.roots;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.PlayerDataHandler;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.widget.HotBarSlot;
import com.astetyne.expirium.main.gui.widget.SwitchArrow;
import com.astetyne.expirium.main.gui.widget.ThumbStick;
import com.astetyne.expirium.main.items.HotSlotsData;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.Utils;
import com.astetyne.expirium.server.api.world.inventory.ChosenSlot;
import com.astetyne.expirium.server.api.world.inventory.UIInteractType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import static com.astetyne.expirium.main.utils.Utils.fromCMToPercW;

public class GameRoot extends Table implements ExpiRoot {

    private final Table itemSelectTable, debugInfoTable, playerStatsTable;

    private final Label fpsLabel, locationLabel, entityLabel, callsLabel, buffersLabel, warnLabel, healthStat, foodStat, tempStat;
    private final Image healthImage, foodImage, tempImage;
    private final HotBarSlot toolSlot, materialSlot, consumableSlot;
    private HotBarSlot focusedSlot, lastFocused;
    public final ThumbStick moveTS, breakTS;
    private final SwitchArrow switchArrowUp, switchArrowDown;
    private final Image inventoryButton, consumeButton;

    public GameRoot() {

        if(Consts.DEBUG) setDebug(true);

        toolSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE, "Tools", ChosenSlot.TOOL_SLOT);
        materialSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE, "Mats", ChosenSlot.MATERIAL_SLOT);
        consumableSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE, "Misc", ChosenSlot.CONSUMABLE_SLOT);

        toolSlot.setFocus(true);
        lastFocused = toolSlot;
        focusedSlot = toolSlot;

        switchArrowUp = new SwitchArrow(Res.SWITCH_ARROW_STYLE, UIInteractType.SWITCH_UP, false);
        switchArrowDown = new SwitchArrow(Res.SWITCH_ARROW_STYLE, UIInteractType.SWITCH_DOWN, true);
        inventoryButton = new Image(Res.INVENTORY);
        consumeButton = new Image(Res.CAMPFIRE_ITEM);

        inventoryButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ExpiGame.get().getClientGateway().getManager().putInvInteractPacket(UIInteractType.OPEN_INV);
                GameScreen.get().setRoot(new InventoryRoot());
            }
        });

        moveTS = new ThumbStick(Res.THUMB_STICK_STYLE);
        breakTS = new ThumbStick(Res.THUMB_STICK_STYLE);

        itemSelectTable = new Table();
        debugInfoTable = new Table();
        playerStatsTable = new Table();

        fpsLabel = new Label("", Res.LABEL_STYLE);
        locationLabel = new Label("", Res.LABEL_STYLE);
        entityLabel = new Label("", Res.LABEL_STYLE);
        callsLabel = new Label("", Res.LABEL_STYLE);
        buffersLabel = new Label("", Res.LABEL_STYLE);
        warnLabel = new Label("pre-alpha", Res.LABEL_STYLE);
        warnLabel.setColor(1,0.1f,0.1f,1);

        healthImage = new Image(Res.HEALTH_ICON);
        foodImage = new Image(Res.FOOD_ICON);
        tempImage = new Image(Res.TEMP_ICON);

        healthStat = new Label("0%", Res.LABEL_STYLE);
        foodStat = new Label("0%", Res.LABEL_STYLE);
        tempStat = new Label("0%", Res.LABEL_STYLE);

        itemSelectTable.add(switchArrowUp).padBottom(10).colspan(3);
        itemSelectTable.row();
        itemSelectTable.add(toolSlot).padRight(20).width(120).height(Utils.percFromW(120));
        itemSelectTable.add(materialSlot).padRight(20).width(120).height(Utils.percFromW(120));;
        itemSelectTable.add(consumableSlot).width(120).height(Utils.percFromW(120));;
        itemSelectTable.row();
        itemSelectTable.add(switchArrowDown).padTop(10).colspan(3);

        if(Consts.DEBUG) {
            debugInfoTable.add(fpsLabel).left().height(50).padTop(40);
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
        debugInfoTable.add(warnLabel).left();

        float iconSize = 60;
        playerStatsTable.add(healthStat).padTop(40).height(50);
        playerStatsTable.add(healthImage).width(iconSize).height(Utils.percFromW(iconSize)).padTop(40);
        playerStatsTable.row();
        playerStatsTable.add(foodStat).padTop(10).height(50);
        playerStatsTable.add(foodImage).width(iconSize).height(Utils.percFromW(iconSize)).padTop(10);
        playerStatsTable.row();
        playerStatsTable.add(tempStat).padTop(10).height(50);
        playerStatsTable.add(tempImage).width(iconSize).height(Utils.percFromW(iconSize)).padTop(10);
        playerStatsTable.row();
        playerStatsTable.setDebug(true);

        build();

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

    private void build() {

        clear();
        add(debugInfoTable).align(Align.topLeft);
        add();
        add(playerStatsTable).align(Align.topRight);
        row();
        add(inventoryButton).width(128).height(Utils.percFromW(128)).align(Align.right).colspan(3).padTop(50);
        row().expand();
        add(moveTS).align(Align.bottomLeft).padBottom(fromCMToPercW(1)).padLeft(fromCMToPercW(1)).uniformX();
        add(itemSelectTable).align(Align.bottom).padBottom(20);

        if(toolSlot.isFocused()) {
            add(breakTS).align(Align.bottomRight).padBottom(fromCMToPercW(1)).padRight(fromCMToPercW(1)).uniformX();
        }else if(materialSlot.isFocused()) {
            ImageButton but = GameScreen.get().getWorld().getPlayer().getTilePlacer().getStabilityButton();
            add(but).align(Align.bottomRight).padBottom(fromCMToPercW(1)).padRight(fromCMToPercW(1)).uniformX();
        }else if(consumableSlot.isFocused()) {
            add(consumeButton).align(Align.bottomRight).padBottom(fromCMToPercW(1)).padRight(fromCMToPercW(1)).uniformX();
        }
    }

    public HotBarSlot getFocusedSlot() {
        return focusedSlot;
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

        HotSlotsData data = GameScreen.get().getInventoryHandler().getHotSlotsData();

        switch(data.getChosenSlot()) {
            case TOOL_SLOT:
                toolSlot.setFocus(true);
                materialSlot.setFocus(false);
                consumableSlot.setFocus(false);
                focusedSlot = toolSlot;
                break;
            case MATERIAL_SLOT:
                toolSlot.setFocus(false);
                materialSlot.setFocus(true);
                consumableSlot.setFocus(false);
                focusedSlot = materialSlot;
                break;
            case CONSUMABLE_SLOT:
                toolSlot.setFocus(false);
                materialSlot.setFocus(false);
                consumableSlot.setFocus(true);
                focusedSlot = consumableSlot;
                break;
        }
        toolSlot.setItemStack(data.getIs1());
        materialSlot.setItemStack(data.getIs2());
        consumableSlot.setItemStack(data.getIs3());

        if(!lastFocused.isFocused()) {
            build();
            lastFocused = focusedSlot;
        }

        PlayerDataHandler playerData = GameScreen.get().getPlayerDataHandler();

        healthStat.setText((int)Math.ceil(playerData.getHealth())+" %");
        foodStat.setText((int)Math.ceil(playerData.getFood())+" %");
        tempStat.setText((int)Math.ceil(playerData.getTemperature())+" °C");

    }
}
