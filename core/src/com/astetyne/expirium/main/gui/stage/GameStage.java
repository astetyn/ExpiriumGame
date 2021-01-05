package com.astetyne.expirium.main.gui.stage;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.widget.HotBarSlot;
import com.astetyne.expirium.main.gui.widget.SwitchArrow;
import com.astetyne.expirium.main.gui.widget.ThumbStick;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.Utils;
import com.astetyne.expirium.server.api.world.inventory.ChosenSlot;
import com.astetyne.expirium.server.api.world.inventory.InvInteractType;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import static com.astetyne.expirium.main.utils.Utils.fromCMToPercW;

public class GameStage extends Stage implements ExpiStage {

    private final Table rootTable, itemSelectTable, debugInfoTable, playerStatsTable;

    private final Label fpsLabel, locationLabel, entityLabel, warnLabel, healthStat, hungerStat, tempStat;
    private final Image healthImage, hungerImage, tempImage;
    private final HotBarSlot toolSlot, materialSlot, consumableSlot;
    private HotBarSlot focusedSlot, lastFocused;
    public final ThumbStick moveTS, breakTS;
    private final SwitchArrow switchArrowUp, switchArrowDown;
    private final Image inventoryButton, consumeButton;

    public GameStage() {

        super(new StretchViewport(1000,1000), ExpiGame.get().getBatch());

        toolSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE, "Tools", ChosenSlot.TOOL_SLOT);
        materialSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE, "Mats", ChosenSlot.MATERIAL_SLOT);
        consumableSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE, "Misc", ChosenSlot.CONSUMABLE_SLOT);

        toolSlot.setFocus(true);
        lastFocused = toolSlot;
        focusedSlot = toolSlot;

        switchArrowUp = new SwitchArrow(Res.SWITCH_ARROW_STYLE, InvInteractType.SWITCH_UP, false);
        switchArrowDown = new SwitchArrow(Res.SWITCH_ARROW_STYLE, InvInteractType.SWITCH_DOWN, true);
        inventoryButton = new Image(Res.INVENTORY);
        consumeButton = new Image(Res.CAMPFIRE_ITEM);

        inventoryButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ExpiGame.get().getClientGateway().getManager().putInvInteractPacket(InvInteractType.OPEN_INV);
                GameScreen.get().showInvStage();
            }
        });

        moveTS = new ThumbStick(Res.THUMB_STICK_STYLE);
        breakTS = new ThumbStick(Res.THUMB_STICK_STYLE);

        rootTable = new Table();
        itemSelectTable = new Table();
        debugInfoTable = new Table();
        playerStatsTable = new Table();

        fpsLabel = new Label("", Res.LABEL_STYLE);
        locationLabel = new Label("", Res.LABEL_STYLE);
        entityLabel = new Label("", Res.LABEL_STYLE);
        warnLabel = new Label("pre-alpha", Res.LABEL_STYLE);
        warnLabel.setColor(1,0.1f,0.1f,1);

        healthImage = new Image(Res.CROSS_ICON);
        hungerImage = new Image(Res.CROSS_ICON);
        tempImage = new Image(Res.CROSS_ICON);

        healthStat = new Label("0%", Res.LABEL_STYLE);
        hungerStat = new Label("0%", Res.LABEL_STYLE);
        tempStat = new Label("0%", Res.LABEL_STYLE);

        itemSelectTable.add(switchArrowUp).padBottom(10).colspan(3);
        itemSelectTable.row();
        itemSelectTable.add(toolSlot).padRight(10);
        itemSelectTable.add(materialSlot).padRight(10);
        itemSelectTable.add(consumableSlot);
        itemSelectTable.row();
        itemSelectTable.add(switchArrowDown).padTop(10).colspan(3);

        if(Consts.DEBUG) {
            debugInfoTable.add(fpsLabel).left();
            debugInfoTable.row();
            debugInfoTable.add(locationLabel).left();
            debugInfoTable.row();
            debugInfoTable.add(entityLabel).left();
            debugInfoTable.row();
        }
        debugInfoTable.add(warnLabel).left();

        float iconSize = 20;
        playerStatsTable.add(healthStat).padTop(10);
        playerStatsTable.add(healthImage).height(iconSize).width(iconSize);
        playerStatsTable.row();
        playerStatsTable.add(hungerStat).padTop(10);
        playerStatsTable.add(hungerImage).height(iconSize).width(iconSize);
        playerStatsTable.row();
        playerStatsTable.add(tempStat).padTop(10);
        playerStatsTable.add(tempImage).height(iconSize).width(iconSize);
        playerStatsTable.row();

        rootTable.setBounds(0, 0, 1000, 1000);

        if(Consts.DEBUG) rootTable.debugCell();
        rootTable.setDebug(true);

        build();

        setRoot(rootTable);
        getRoot().setVisible(false);

    }

    @Override
    public void act() {

        if(!getRoot().isVisible()) return;

        fpsLabel.setText("fps: "+Gdx.graphics.getFramesPerSecond());
        Vector2 loc = GameScreen.get().getWorld().getPlayer().getLocation();
        locationLabel.setText("x: "+((int)loc.x)+" y: "+((int)loc.y));
        entityLabel.setText("entities: "+ GameScreen.get().getWorld().getEntitiesID().keySet().size());

        if(!lastFocused.isFocused()) {
            build();
            lastFocused = focusedSlot;
        }

        super.act();
    }

    public void feedHotSlots(PacketInputStream in) {
        ChosenSlot chs = ChosenSlot.getSlot(in.getByte());
        System.out.println(chs);
        switch(chs) {
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
        toolSlot.setItemStack(new ItemStack(Item.getType(in.getInt()), in.getInt()));
        materialSlot.setItemStack(new ItemStack(Item.getType(in.getInt()), in.getInt()));
        consumableSlot.setItemStack(new ItemStack(Item.getType(in.getInt()), in.getInt()));
    }

    public ItemStack getItemInHand() {
        return focusedSlot.getItemStack();
    }

    private void build() {

        rootTable.clear();
        rootTable.add(debugInfoTable).align(Align.topLeft);
        rootTable.add();
        rootTable.add(playerStatsTable).align(Align.topRight);
        rootTable.row();
        rootTable.add(inventoryButton).width(64).height(Utils.percFromW(64)).align(Align.right).colspan(3).padTop(50);
        rootTable.row().expand();
        rootTable.add(moveTS).align(Align.bottomLeft).padBottom(fromCMToPercW(1)).padLeft(fromCMToPercW(1)).uniformX();
        rootTable.add(itemSelectTable).align(Align.bottom).padBottom(20);

        if(toolSlot.isFocused()) {
            rootTable.add(breakTS).align(Align.bottomRight).padBottom(fromCMToPercW(1)).padRight(fromCMToPercW(1)).uniformX();
        }else if(materialSlot.isFocused()) {
            ImageButton but = GameScreen.get().getWorld().getPlayer().getTilePlacer().getStabilityButton();
            rootTable.add(but).align(Align.bottomRight).padBottom(fromCMToPercW(1)).padRight(fromCMToPercW(1)).uniformX();
        }else if(consumableSlot.isFocused()) {
            rootTable.add(consumeButton).align(Align.bottomRight).padBottom(fromCMToPercW(1)).padRight(fromCMToPercW(1)).uniformX();
        }
    }

    public void setVisible(boolean visible) {
        getRoot().setVisible(visible);
    }

    public HotBarSlot getFocusedSlot() {
        return focusedSlot;
    }

    @Override
    public boolean isDimmed() {
        return false;
    }
}
