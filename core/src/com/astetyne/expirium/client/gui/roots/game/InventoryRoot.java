package com.astetyne.expirium.client.gui.roots.game;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.gui.widget.InventoryGrid;
import com.astetyne.expirium.client.gui.widget.RecipeDetailTable;
import com.astetyne.expirium.client.gui.widget.RecipeListTable;
import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class InventoryRoot extends Table implements GameRootable {

    private final GameScreen game;

    private final InventoryGrid storage;
    private final Cell<InventoryGrid> storageCell;

    private final ScrollPane recipeList;
    private final RecipeDetailTable recipeDetail;
    private final Image returnButton;

    public InventoryRoot(GameScreen game) {

        this.game = game;

        if(Consts.DEBUG) setDebug(true);

        recipeDetail = new RecipeDetailTable();
        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle(Res.FRAME_ROUND_GRAY_TRANSP, null, null, null, null);
        recipeList = new ScrollPane(new RecipeListTable(recipeDetail), style);
        recipeList.setScrollingDisabled(true, false);

        returnButton = new Image(Res.CROSS_ICON);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setRoot(new GameRoot(game));
            }
        });

        storage = new InventoryGrid(Consts.INV_MAX_SIZE, Consts.INV_MAX_SIZE, game.getPlayerData().getMainData(), game.getPlayerData().getDefaultExtraCells());

        storage.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GridItemStack is = storage.getItemAt(x, y);
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
        add(recipeList).growY().width(500).pad(20,0,20,0);
        add(recipeDetail).growY().width(450).pad(20, 20, 20, 20).align(Align.top);
        add(returnButton).width(Utils.percFromH(100)).height(100).pad(20, 50, 0, 0).align(Align.topRight);

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

    @Override
    public boolean canInteractWithWorld() {
        return false;
    }
}
