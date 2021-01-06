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

public class DoubleStorageGrid extends Table {

    private final StorageGrid grid1, grid2;
    private final Cell<StorageGrid> gridCell1, gridCell2;
    private ItemStack selectedItem;
    private final Vector2 itemVec;
    private boolean fromMain;

    public DoubleStorageGrid(int r1, int c1, int r2, int c2, StorageGrid.StorageGridStyle style) {

        int gap = 100;

        grid1 = new StorageGrid(r1, c1, style);
        grid2 = new StorageGrid(r2, c2, style);
        int mlt = 60;
        gridCell1 = add(grid1).width(c1 * mlt).height(Utils.percFromW(r1 * mlt)).padRight(gap);
        gridCell2 = add(grid2).width(c2 * mlt).height(Utils.percFromW(r2 * mlt));

        setTouchable(Touchable.enabled);

        itemVec = new Vector2();

        addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ItemStack is = grid1.getItemAt(x - gridCell1.getActorX(), y - gridCell1.getActorY());
                ItemStack is2 = grid2.getItemAt(x - gridCell2.getActorX(), y - gridCell2.getActorY());
                if(is != null) {
                    selectedItem = is;
                    itemVec.set(getX() + x, getY() + y);
                    fromMain = false;
                    return true;
                }else if(is2 != null) {
                    fromMain = true;
                    selectedItem = is2;
                    itemVec.set(getX() + x, getY() + y);
                    return true;
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                IntVector2 pos;
                boolean toMain = false;
                if(grid1.isInsideGrid(x - gridCell1.getActorX(), y - gridCell1.getActorY())) {
                    pos = grid1.getGridPos(x - gridCell1.getActorX(), y - gridCell1.getActorY());
                    toMain = true;
                }else if(grid2.isInsideGrid(x - gridCell2.getActorX(), y - gridCell2.getActorY())) {
                    pos = grid2.getGridPos(x - gridCell2.getActorX(), y - gridCell2.getActorY());
                }else {
                    pos = new IntVector2(-1, -1);
                }
                ExpiGame.get().getClientGateway().getManager().putInvItemMoveReqPacket(fromMain, selectedItem.getGridPos(), toMain, pos);
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
        grid1.feed(in);
        grid2.feed(in);
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
