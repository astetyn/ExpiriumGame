package com.astetyne.expirium.main.gui.stage;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.widget.StorageGrid;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class DoubleInventoryStage extends Stage {

    private final StorageGrid mainGrid;
    private final StorageGrid secondGrid;

    public DoubleInventoryStage() {
        super(new StretchViewport(1000, 1000), ExpiGame.get().getBatch());
        int c = Consts.PLAYER_INV_COLUMNS;
        int r = Consts.PLAYER_INV_ROWS;
        mainGrid = new StorageGrid(r, c, Res.STORAGE_GRID_STYLE);
        secondGrid = new StorageGrid(Res.STORAGE_GRID_STYLE);
    }

    public void open(PacketInputStream in) {
        byte r = in.getByte();
        byte c = in.getByte();
        secondGrid.setGrid(r, c);
        GameScreen.get().getGameStage().setVisible(false);
        GameScreen.get().getInvStage().setVisible(false);
        setVisible(true);
    }

    public StorageGrid getMainGrid() {
        return mainGrid;
    }

    public StorageGrid getSecondGrid() {
        return secondGrid;
    }

    public void setVisible(boolean b) {

    }
}
