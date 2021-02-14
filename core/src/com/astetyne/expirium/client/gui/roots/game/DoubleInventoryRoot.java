package com.astetyne.expirium.client.gui.roots.game;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.data.StorageGridData;
import com.astetyne.expirium.client.gui.widget.StorageGrid;
import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class DoubleInventoryRoot extends Table implements GameRootable {

    private final StorageGrid storage1, storage2;
    private final Cell<StorageGrid> storageCell1, storageCell2;
    private final Image returnButton;
    private boolean fromMain;

    public DoubleInventoryRoot(PacketInputStream in) {

        StorageGridData secondData = GameScreen.get().getPlayerData().getSecondData();
        secondData.rows = in.getInt();
        secondData.columns = in.getInt();

        storage1 = new StorageGrid(GameScreen.get().getPlayerData().getMainData(), true);
        storage2 = new StorageGrid(secondData, false);

        returnButton = new Image(Res.CROSS_ICON);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen.get().setRoot(new com.astetyne.expirium.client.gui.roots.game.GameRoot());
            }
        });

        // width is here just for making sure, the inv label will not expand table when text is wider
        storageCell1 = add(storage1).width(800).growY().padRight(100).fill();
        storageCell2 = add(storage2).width(800).growY().fill();

        if(Consts.DEBUG) setDebug(true);

        returnButton.setBounds(1880, 890, 100, Utils.percFromW(100));
        addActor(returnButton);

        setTouchable(Touchable.enabled);

        addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GridItemStack is = storage1.getItemAt(x - storageCell1.getActorX(), y - storageCell1.getActorY());
                GridItemStack is2 = storage2.getItemAt(x - storageCell2.getActorX(), y - storageCell2.getActorY());
                if(is != null) {
                    storage1.getGrid().setSelectedItem(is);
                    storage1.getGrid().updateVec(x, y);
                    fromMain = true;
                    storage1.setZIndex(100);
                    return true;
                }else if(is2 != null) {
                    storage2.getGrid().setSelectedItem(is2);
                    storage2.getGrid().updateVec(x, y);
                    fromMain = false;
                    storage2.setZIndex(100);
                    return true;
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                IntVector2 finalPos = new IntVector2(-1, -1);
                IntVector2 pos = storage1.getGridPos(x - storageCell1.getActorX(), y - storageCell1.getActorY());
                IntVector2 pos2 = storage2.getGridPos(x - storageCell2.getActorX(), y - storageCell2.getActorY());
                boolean toMain = false;
                if(pos.x != -1) {
                    toMain = true;
                    finalPos = pos;
                }else if(pos2.x != -1) {
                    finalPos = pos2;
                }
                GridItemStack selItem;
                if(fromMain) {
                    selItem = storage1.getGrid().getSelectedItem();
                }else {
                    selItem = storage2.getGrid().getSelectedItem();
                }
                ExpiGame.get().getClientGateway().getManager().putInvItemMoveReqPacket(fromMain, selItem.getGridPos(), toMain, finalPos);
                storage1.getGrid().setSelectedItem(null);
                storage2.getGrid().setSelectedItem(null);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                storage1.getGrid().updateVec(x, y);
                storage2.getGrid().updateVec(x, y);
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
        storage1.refreshLabels();
        storage2.refreshLabels();
    }

    @Override
    public boolean canInteractWithWorld() {
        return false;
    }
}
