package com.astetyne.expirium.main.gui.stage;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.widget.StorageGrid;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemRecipe;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class InventoryStage extends Stage implements ExpiStage {

    private final StorageGrid inventoryGrid;
    private final Table rootTable, gridTable, recipeList, recipeDetail, requiredItems;
    private final Image returnButton;
    private final ScrollPane scrollProductsList, scrollRequiredItems;
    private ItemRecipe selectedRecipe;

    public InventoryStage() {
        super(new StretchViewport(1000, 1000), ExpiGame.get().getBatch());

        returnButton = new Image(Res.CROSS_ICON);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen.get().showGameStage();
            }
        });

        rootTable = new Table();
        rootTable.setBounds(0, 0, 1000, 1000);

        gridTable = new Table();
        recipeList = new Table();
        recipeDetail = new Table();
        requiredItems = new Table();

        if(Consts.DEBUG) rootTable.setDebug(true);
        if(Consts.DEBUG) gridTable.setDebug(true);
        if(Consts.DEBUG) recipeDetail.setDebug(true);

        int c = Consts.PLAYER_INV_COLUMNS;
        int r = Consts.PLAYER_INV_ROWS;
        inventoryGrid = new StorageGrid(r, c, Res.STORAGE_GRID_STYLE);

        scrollProductsList = new ScrollPane(recipeList);
        scrollProductsList.setScrollingDisabled(true, false);

        scrollRequiredItems = new ScrollPane(requiredItems);
        scrollRequiredItems.setScrollingDisabled(true, false);

        build();

        setRoot(rootTable);
        getRoot().setVisible(false);
    }

    @Override
    public void act() {
        if(!getRoot().isVisible()) return;
        super.act();
    }

    private void build() {

        Image weightImage = new Image(Res.INV_WEIGHT);

        gridTable.clear();
        gridTable.add(inventoryGrid).width(400).height(Utils.percFromW(400)).colspan(2);
        gridTable.row();
        gridTable.add(weightImage).width(30).height(Utils.percFromW(30)).align(Align.left).padTop(20);
        gridTable.add(inventoryGrid.getWeightLabel()).expandX().align(Align.left).pad(20, 20, 0,0);

        recipeList.clear();
        for(ItemRecipe recipe : ItemRecipe.values()) {
            Table t = new Table();
            if(Consts.DEBUG) t.debugAll();
            Item item = recipe.getProduct().getItem();
            Image icon = new Image(item.getItemTexture());
            Label label = new Label(item.getLabel(), Res.LABEL_STYLE);
            t.add(icon).width(50).height(Utils.percFromW(50)).align(Align.left).pad(10, 5, 10, 5);
            t.add(label).grow();
            t.setBackground(Res.RECIPE_BACK);

            t.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedRecipe = recipe;
                    build();
                }
            });
            recipeList.add(t).width(200);
            recipeList.row();
        }
        recipeList.setBackground(Res.INV_CHOOSE_BACK);

        recipeDetail.clear();
        recipeDetail.setBackground(Res.INV_DETAIL_BACK);
        if(selectedRecipe == null) selectedRecipe = ItemRecipe.getRecipe(0);
        Image imgDetail = new Image(selectedRecipe.getProduct().getItem().getItemTexture());
        TextButton makeButton = new TextButton("Make", Res.TEXT_BUTTON_STYLE);
        makeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ExpiGame.get().getClientGateway().getManager().putInvItemMakeReqPacket(selectedRecipe);
            }
        });
        Label desc = new Label(selectedRecipe.getDescription(), Res.LABEL_STYLE);
        desc.setWrap(true);
        desc.setAlignment(Align.topLeft);
        recipeDetail.add(returnButton).width(Utils.percFromH(100)).height(100).align(Align.topRight);
        recipeDetail.row();
        recipeDetail.add(imgDetail).width(Utils.percFromH(100)).height(100).pad(50,50,50,50);
        recipeDetail.row();
        recipeDetail.add(makeButton).width(120).height(80).padBottom(30);
        recipeDetail.row();

        requiredItems.clear();
        for(ItemStack is : selectedRecipe.getRequiredItems()) {
            requiredItems.add(new Label(is.getAmount() +" "+is.getItem().getLabel(), Res.LABEL_STYLE));
            requiredItems.row();
        }
        recipeDetail.add(scrollRequiredItems).expandX().height(200);
        recipeDetail.row();
        recipeDetail.add(desc).grow().width(180).pad(50, 10, 10, 10);

        rootTable.clear();
        rootTable.add(gridTable).width(500).expandY();
        rootTable.add(scrollProductsList).width(200).pad(20,0,20,0);
        rootTable.add(recipeDetail).width(200).pad(20, 20, 20, 20).align(Align.top);
    }

    public void setVisible(boolean visible) {
        getRoot().setVisible(visible);
    }

    public StorageGrid getInvGrid() {
        return inventoryGrid;
    }

    @Override
    public boolean isDimmed() {
        return true;
    }
}
