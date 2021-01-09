package com.astetyne.expirium.main.gui.widget;

import com.astetyne.expirium.main.Res;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

public class RecipeList extends ScrollPane {

    public RecipeList(Actor actor) {
        super(actor);
        setScrollingDisabled(true, false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(Res.INV_CHOOSE_BACK, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }
}
