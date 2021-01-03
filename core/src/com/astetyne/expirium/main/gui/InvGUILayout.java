package com.astetyne.expirium.main.gui;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemRecipe;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.items.inventory.Inventory;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class InvGUILayout extends GUILayout {

    private final Table rootTable, gridTable, recipeList, recipeDetail, requiredItems;
    private final Image returnButton;
    private final ScrollPane scrollProductsList, scrollRequiredItems;
    private ItemRecipe selectedRecipe;

    public InvGUILayout() {

        returnButton = new Image(Res.CROSS_ICON);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameStage.get().setActiveGuiLayout(GameStage.get().getGameGuiLayout());
            }
        });

        rootTable = new Table();
        rootTable.setBounds(0, 0, 1000, 1000);

        gridTable = new Table();
        recipeList = new Table();
        recipeDetail = new Table();
        requiredItems = new Table();

        //if(Constants.DEBUG) rootTable.setDebug(true);
        if(Consts.DEBUG) gridTable.setDebug(true);
        if(Consts.DEBUG) recipeDetail.setDebug(true);

        scrollProductsList = new ScrollPane(recipeList);
        scrollProductsList.setScrollingDisabled(true, false);

        scrollRequiredItems = new ScrollPane(requiredItems);
        scrollRequiredItems.setScrollingDisabled(true, false);

        rootTable.setBackground(new TextureRegionDrawable(Res.WHITE_TILE));

    }

    @Override
    public void update() {

    }

    @Override
    public Table getRootTable() {
        return rootTable;
    }

    @Override
    public void build(Stage stage) {

        Inventory inv = GameStage.get().getInv();

        Image weightImage = new Image(Res.WHITE_TILE);

        gridTable.clear();
        gridTable.add(inv.getInventoryGrid()).width(400).height(Utils.percFromW(400)).colspan(2);
        gridTable.row();
        gridTable.add(weightImage).width(30).height(Utils.percFromW(30)).align(Align.left).padTop(20);
        gridTable.add(inv.getInventoryGrid().getWeightLabel()).expandX().align(Align.left).pad(20, 20, 0,0);

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
                    build(stage);
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
                ExpiriumGame.get().getClientGateway().getManager().putInvItemMakeReqPacket(selectedRecipe);
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

        stage.clear();
        stage.addActor(rootTable);

    }

    @Override
    public void resize(int w, int h) {}

    @Override
    public boolean isDimmed() {
        return true;
    }
}
