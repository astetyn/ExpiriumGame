package com.astetyne.expirium.main.gui.stage;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.widget.StorageGrid;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.main.utils.Utils;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class DoubleInventoryStage extends Stage implements ExpiStage {

    private final StorageGrid storage1, storage2;
    private final Cell<StorageGrid> storageCell1, storageCell2;
    private final Table rootTable;
    private final Image returnButton;
    private boolean fromMain;

    public DoubleInventoryStage() {
        super(new StretchViewport(1000, 1000), ExpiGame.get().getBatch());

        storage1 = new StorageGrid(GameScreen.get().getInvStage().getMainData(), true);
        storage2 = new StorageGrid(GameScreen.get().getInvStage().getSecondData(), false);

        returnButton = new Image(Res.CROSS_ICON);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen.get().showGameStage();
            }
        });

        rootTable = new Table();
        rootTable.setBounds(0, 0, 1000, 1000);

        storageCell1 = rootTable.add(storage1).padRight(100);
        storageCell2 = rootTable.add(storage2);
        rootTable.add(returnButton).width(Utils.percFromH(100)).height(100).align(Align.topRight);

        rootTable.setTouchable(Touchable.enabled);

        rootTable.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //System.out.println("x: "+x+" y: "+y);
                ItemStack is = storage1.getItemAt(x - storageCell1.getActorX(), y - storageCell1.getActorY());
                ItemStack is2 = storage2.getItemAt(x - storageCell2.getActorX(), y - storageCell2.getActorY());
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
                IntVector2 finalPos = null;
                IntVector2 pos = storage1.getGridPos(x - storageCell1.getActorX(), y - storageCell1.getActorY());
                IntVector2 pos2 = storage2.getGridPos(x - storageCell2.getActorX(), y - storageCell2.getActorY());
                boolean toMain = false;
                if(pos.x != -1) {
                    toMain = true;
                    finalPos = pos;
                }else if(pos2.x != -1) {
                    finalPos = pos2;
                }
                ItemStack selItem;
                if(fromMain) {
                    selItem = storage1.getGrid().getSelectedItem();
                }else {
                    selItem = storage2.getGrid().getSelectedItem();
                }
                //System.out.println("FM: "+fromMain+" P1: "+selItem.getGridPos()+" TM: "+toMain+" P2: "+finalPos);
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

        setRoot(rootTable);
        getRoot().setVisible(false);
    }

    public void open(PacketInputStream in) {
        int r = in.getInt();
        int c = in.getInt();
        storage2.getGrid().getData().rows = r;
        storage2.getGrid().getData().columns = c;
        storage2.rebuild();
        GameScreen.get().showDoubleInvStage();
    }

    public void setVisible(boolean b) {
        getRoot().setVisible(b);
        if(b) {
            rootTable.setTouchable(Touchable.enabled);
        }else {
            rootTable.setTouchable(Touchable.disabled);
        }
    }

    @Override
    public boolean isDimmed() {
        return true;
    }

    public void onFeedUpdate() {
        storage1.onFeedUpdate();
        storage2.onFeedUpdate();
    }

}
