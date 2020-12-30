package com.astetyne.expirium.main.gui;

import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.items.ItemRecipe;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.items.inventory.Inventory;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class InvGUILayout extends GUILayout {

    private final Table rootTable, gridTable, recipeList, recipeDetail;
    private final Image returnButton;
    private final ScrollPane scrollProductsList;
    private ItemRecipe selectedRecipe;

    public InvGUILayout() {

        returnButton = new Image(Res.CROSS_TEXTURE);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameStage.get().setActiveGuiLayout(GameStage.get().getGameGuiLayout());
            }
        });

        rootTable = new Table();
        rootTable.setBounds(0, 0, 1000, 1000);
        if(Constants.DEBUG) rootTable.setDebug(true);

        gridTable = new Table();
        recipeList = new Table();
        recipeDetail = new Table();

        if(Constants.DEBUG) gridTable.setDebug(true);

        scrollProductsList = new ScrollPane(recipeList);
        scrollProductsList.setScrollingDisabled(true, false);

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

        stage.clear();
        stage.addActor(rootTable);

        rootTable.clear();

        String s = inv.getInventoryGrid().getTotalWeight()+"/"+inv.getInventoryGrid().getMaxWeight();
        Label weightLabel = new Label(s, Res.LABEL_STYLE);

        gridTable.clear();
        gridTable.add(inv.getInventoryGrid()).width(400).height(Utils.percFromW(400)).pad(0, 10, 0, 10);
        gridTable.row();
        gridTable.add(weightLabel).align(Align.left).pad(10, 20, 0,0);

        recipeList.clear();
        for(ItemRecipe recipe : inv.getItemRecipes()) {
            Table t = new Table();
            t.row().expandX();
            if(Constants.DEBUG) t.debugAll();
            ItemType item = recipe.getProduct().getItem();
            Image icon = new Image(item.getItemTexture());
            Label label = new Label(item.getLabel(), Res.LABEL_STYLE);
            t.add(icon).width(50).height(Utils.percFromW(50)).align(Align.left).pad(10, 5, 10, 5);
            t.add(label);
            t.setBackground(Res.RECIPE_BACK);
            recipeList.add(t).expand();
            recipeList.row();
        }

        recipeDetail.clear();
        if(selectedRecipe == null) selectedRecipe = inv.getItemRecipes().get(0);
        Image imgDetail = new Image(selectedRecipe.getProduct().getItem().getItemTexture());
        TextButton makeButton = new TextButton("Make", Res.TEXT_BUTTON_STYLE);
        Label desc = new Label(selectedRecipe.getDescription(), Res.LABEL_STYLE);
        desc.setWrap(true);
        recipeDetail.add(returnButton).align(Align.topRight);
        recipeDetail.row();
        recipeDetail.add(imgDetail);
        recipeDetail.row();
        recipeDetail.add(makeButton);
        recipeDetail.row();
        for(ItemStack is : selectedRecipe.getRequiredItems()) {

        }
        recipeDetail.row();
        recipeDetail.add(desc).width(200);

        rootTable.add(gridTable).width(Value.percentWidth(0.5f, rootTable)).expandY();
        rootTable.add(scrollProductsList).width(Value.percentWidth(0.2f, rootTable));
        rootTable.add(recipeDetail).expand();

    }

    @Override
    public void resize(int w, int h) {

    }
}
