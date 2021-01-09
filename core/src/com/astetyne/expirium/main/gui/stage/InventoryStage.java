package com.astetyne.expirium.main.gui.stage;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.gui.widget.RecipeDetailTable;
import com.astetyne.expirium.main.gui.widget.RecipeList;
import com.astetyne.expirium.main.gui.widget.RecipeListTable;
import com.astetyne.expirium.main.gui.widget.StorageGrid;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.items.StorageGridData;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.main.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class InventoryStage extends Stage implements ExpiStage {

    private final StorageGridData mainData, secondData;

    private final StorageGrid storage;
    private final Cell<StorageGrid> storageCell;

    private final RecipeList recipeList;
    private final RecipeDetailTable recipeDetail;
    private final Table rootTable;

    public InventoryStage() {
        super(new StretchViewport(1000, 1000), ExpiGame.get().getBatch());

        mainData = new StorageGridData();
        secondData = new StorageGridData();

        recipeDetail = new RecipeDetailTable();

        recipeList = new RecipeList(new RecipeListTable(recipeDetail));

        mainData.rows = Consts.PLAYER_INV_ROWS;
        mainData.columns = Consts.PLAYER_INV_COLUMNS;

        rootTable = new Table();
        rootTable.setBounds(0, 0, 1000, 1000);

        if(Consts.DEBUG) rootTable.setDebug(true);

        storage = new StorageGrid(mainData, true);

        storage.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ItemStack is = storage.getItemAt(x, y);
                if(is != null) {
                    storage.getGrid().setSelectedItem(is);
                    storage.getGrid().updateVec(storageCell.getActorX() + x, storageCell.getActorY() + y);
                    return true;
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                IntVector2 pos = storage.getGridPos(x, y);
                ExpiGame.get().getClientGateway().getManager().putInvItemMoveReqPacket(true, storage.getGrid().getSelectedItem().getGridPos(), true, pos);
                storage.getGrid().setSelectedItem(null);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                storage.getGrid().updateVec(storageCell.getActorX() + x, storageCell.getActorY() + y);
            }

        });

        storageCell = rootTable.add(storage).width(400).height(Utils.percFromW(400));
        rootTable.add(recipeList).growY().width(200).pad(20,0,20,0);
        rootTable.add(recipeDetail).growY().width(200).pad(20, 20, 20, 20).align(Align.top);

        storage.setZIndex(100);

        setRoot(rootTable);
        getRoot().setVisible(false);
    }

    @Override
    public void act() {
        if(!getRoot().isVisible()) return;
        super.act();
    }

    public void setVisible(boolean visible) {
        getRoot().setVisible(visible);
    }

    public StorageGridData getMainData() {
        return mainData;
    }

    public StorageGridData getSecondData() {
        return secondData;
    }

    @Override
    public boolean isDimmed() {
        return true;
    }

    public void onFeedUpdate() {
        storage.onFeedUpdate();
    }
}
