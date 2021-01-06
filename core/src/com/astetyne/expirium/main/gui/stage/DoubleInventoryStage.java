package com.astetyne.expirium.main.gui.stage;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.widget.DoubleStorageGrid;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class DoubleInventoryStage extends Stage implements ExpiStage {

    private DoubleStorageGrid grid;

    public DoubleInventoryStage() {
        super(new StretchViewport(1000, 1000), ExpiGame.get().getBatch());
    }

    public void open(PacketInputStream in) {
        int r1 = Consts.PLAYER_INV_ROWS;
        int c1 = Consts.PLAYER_INV_COLUMNS;
        byte r2 = in.getByte();
        byte c2 = in.getByte();
        grid = new DoubleStorageGrid(r1, c1, r2, c2, Res.STORAGE_GRID_STYLE);
        GameScreen.get().showDoubleInvStage();
    }

    public void setVisible(boolean b) {

    }

    @Override
    public boolean isDimmed() {
        return true;
    }

    public DoubleStorageGrid getGrid() {
        return grid;
    }
}
