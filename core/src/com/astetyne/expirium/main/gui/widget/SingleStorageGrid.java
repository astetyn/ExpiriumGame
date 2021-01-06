package com.astetyne.expirium.main.gui.widget;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.main.utils.Utils;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class SingleStorageGrid extends Table {

    private final StorageGrid grid;
    private final Cell<StorageGrid> gridCell;
    private ItemStack selectedItem;
    private final Vector2 itemVec;

    public SingleStorageGrid(int r, int c, StorageGrid.StorageGridStyle style) {

        grid = new StorageGrid(r, c, style);

        int mlt = 60;
        gridCell = add(grid).width(c * mlt).height(Utils.percFromW(r * mlt)).colspan(2);
        row();
        add(grid.getWeightImage()).width(30).height(Utils.percFromW(30)).align(Align.left).padTop(20);
        add(grid.getWeightLabel()).expandX().align(Align.left).pad(20, 20, 0,0);

        setTouchable(Touchable.enabled);

        itemVec = new Vector2();

        addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ItemStack is = grid.getItemAt(x - gridCell.getActorX(), y - gridCell.getActorY());
                if(is != null) {
                    selectedItem = is;
                    itemVec.set(getX() + x, getY() + y);
                    return true;
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                IntVector2 pos;
                if(grid.isInsideGrid(x - gridCell.getActorX(), y - gridCell.getActorY())) {
                    pos = grid.getGridPos(x - gridCell.getActorX(), y - gridCell.getActorY());
                }else {
                    pos = new IntVector2(-1, -1);
                }
                ExpiGame.get().getClientGateway().getManager().putInvItemMoveReqPacket(true, selectedItem.getGridPos(), true, pos);
                selectedItem = null;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if(selectedItem != null) {
                    itemVec.set(getX() + x, getY() + y);
                }
            }

        });

    }

    public void feed(PacketInputStream in) {
        grid.feed(in);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(selectedItem != null) {
            TextureRegion tex = selectedItem.getItem().getItemTexture();
            batch.draw(tex, itemVec.x, itemVec.y, 100, Utils.percFromW(100));
        }
    }

}
