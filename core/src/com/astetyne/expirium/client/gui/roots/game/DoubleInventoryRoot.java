package com.astetyne.expirium.client.gui.roots.game;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.data.*;
import com.astetyne.expirium.client.gui.widget.InventoryGrid;
import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class DoubleInventoryRoot extends WidgetGroup implements GameRootable {

    private final static int storageX1 = 100, storageX2 = 850;

    private final GameScreen game;

    private final InventoryGrid inv1, inv2;
    private final Image returnButton;
    private boolean fromMain;

    private final Label[] invVariableLabels;

    public DoubleInventoryRoot(GameScreen game, PacketInputStream in) {

        this.game = game;

        SecondGridData secondData = game.getPlayerData().getSecondData();

        int rows = in.getByte();
        int columns = in.getByte();
        secondData.label = in.getString();

        int size = in.getByte();
        ExtraCell[] extraCells = new ExtraCell[size];
        for(int i = 0; i < size; i++) {
            int index = in.getByte();
            int y = index / columns;
            int x = index - y * columns;
            extraCells[i] = new ExtraCell(x, y, ExtraCellTexture.get(in.getByte()));
        }

        secondData.secondInvVariables.clear();
        int numberOfVariables = in.getByte();
        for(int i = 0; i < numberOfVariables; i++) {
            secondData.secondInvVariables.add(new InvVariable(InvVariableType.get(in.getByte())));
        }
        invVariableLabels = new Label[numberOfVariables];

        inv1 = new InventoryGrid(5, 5, game.getPlayerData().getMainData(), game.getPlayerData().getDefaultExtraCells());
        inv2 = new InventoryGrid(rows, columns, secondData, extraCells);

        returnButton = new Image(Res.CROSS_ICON);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setRoot(new GameRoot(game));
            }
        });

        inv1.setBounds(storageX1, 0, Consts.INV_TILE_WIDTH * Consts.INV_MAX_SIZE, Consts.SCREEN_HEIGHT);
        addActor(inv1);
        inv2.setBounds(storageX2, 0, Consts.INV_TILE_WIDTH * Consts.INV_MAX_SIZE, Consts.SCREEN_HEIGHT);
        addActor(inv2);

        if(Consts.DEBUG) debugAll();

        returnButton.setBounds(1880, 890, 100, Utils.percFromW(100));
        addActor(returnButton);

        float gap = 10;
        float iconWidth = 80;
        float iconHeight = Utils.percFromW(iconWidth);

        Image backTable = new Image(Res.FRAME_ROUND_GRAY);
        float tableHeight = numberOfVariables * (iconHeight + gap) + 100;
        backTable.setBounds(1555, (Consts.SCREEN_HEIGHT - tableHeight)/2, 350, tableHeight);
        addActor(backTable);

        for(int i = 0; i < numberOfVariables; i++) {
            InvVariable var = secondData.secondInvVariables.get(i);
            Image img = new Image(var.type.getIconTex());
            Label lab = new Label("", Res.LABEL_STYLE);
            lab.setColor(var.type.getColor().getColor());
            invVariableLabels[i] = lab;

            img.setBounds(1600, (Consts.SCREEN_HEIGHT + tableHeight)/2 - (i+1)*iconHeight - 50, iconWidth, iconHeight);
            lab.setBounds(1600 + iconWidth + 20, img.getY(), 300, iconHeight);
            addActor(img);
            addActor(lab);
        }

        addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GridItemStack is = inv1.getItemAt(x - storageX1, y);
                GridItemStack is2 = inv2.getItemAt(x - storageX2, y);
                if(is != null) {
                    inv1.getGrid().setSelectedItem(is);
                    inv1.getGrid().updateVec(x, y);
                    fromMain = true;
                    inv1.setZIndex(100);
                    return true;
                }else if(is2 != null) {
                    inv2.getGrid().setSelectedItem(is2);
                    inv2.getGrid().updateVec(x, y);
                    fromMain = false;
                    inv2.setZIndex(100);
                    return true;
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                IntVector2 finalPos = new IntVector2(-1, -1);
                IntVector2 pos = inv1.getGridPos(x - storageX1, y);
                IntVector2 pos2 = inv2.getGridPos(x - storageX2, y);
                boolean toMain = false;
                if(pos.x != -1) {
                    toMain = true;
                    finalPos = pos;
                }else if(pos2.x != -1) {
                    finalPos = pos2;
                }
                GridItemStack selItem;
                if(fromMain) {
                    selItem = inv1.getGrid().getSelectedItem();
                }else {
                    selItem = inv2.getGrid().getSelectedItem();
                }
                ExpiGame.get().getClientGateway().getManager().putInvItemMoveReqPacket(fromMain, selItem.getGridPos(), toMain, finalPos);
                inv1.getGrid().setSelectedItem(null);
                inv2.getGrid().setSelectedItem(null);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                inv1.getGrid().updateVec(x, y);
                inv2.getGrid().updateVec(x, y);
            }
        });
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public boolean isDimmed() {
        return true;
    }

    @Override
    public void refresh() {
        inv1.refreshLabels();
        inv2.refreshLabels();
        for(int i = 0; i < invVariableLabels.length; i++) {
            invVariableLabels[i].setText(" = "+game.getPlayerData().getSecondData().secondInvVariables.get(i).text);
        }
    }

    @Override
    public boolean canInteractWithWorld() {
        return false;
    }
}
