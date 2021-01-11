package com.astetyne.expirium.main.gui.roots;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.gui.widget.RecipeDetailTable;
import com.astetyne.expirium.main.gui.widget.RecipeList;
import com.astetyne.expirium.main.gui.widget.RecipeListTable;
import com.astetyne.expirium.main.gui.widget.StorageGrid;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.main.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class InventoryRoot extends Table implements ExpiRoot {

    private final StorageGrid storage;
    private final Cell<StorageGrid> storageCell;

    private final RecipeList recipeList;
    private final RecipeDetailTable recipeDetail;

    public InventoryRoot() {

        if(Consts.DEBUG) setDebug(true);

        recipeDetail = new RecipeDetailTable();

        recipeList = new RecipeList(new RecipeListTable(recipeDetail));

        storage = new StorageGrid(GameScreen.get().getInventoryHandler().getMainData(), true);

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

        storageCell = add(storage).width(800).height(Utils.percFromW(800));
        add(recipeList).growY().width(400).pad(20,0,20,0);
        add(recipeDetail).growY().width(400).pad(20, 20, 20, 20).align(Align.top);

        storage.setZIndex(100);
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
        storage.refreshLabels();
    }
}
