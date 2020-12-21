package com.astetyne.main.world.input;

import com.astetyne.main.items.ItemType;
import com.astetyne.main.items.inventory.Inventory;
import com.astetyne.main.stages.GameStage;
import com.astetyne.main.world.GameWorld;
import com.astetyne.main.world.tiles.Tile;
import com.astetyne.main.world.tiles.TileType;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class TilePlacer implements InputProcessor {

    private final SpriteBatch batch;
    private final Inventory inv;
    private final GameWorld world;

    public TilePlacer() {

        batch = GameStage.get().getBatch();
        inv = GameStage.get().getInv();
        world = GameStage.get().getWorld();

    }

    public void render(Tile t) {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return checkPlace(screenX, screenY);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return checkPlace(screenX, screenY);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    private boolean checkPlace(int screenX, int screenY) {
        if(!inv.getMaterialSlot().isFocused()) return false;

        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        Tile t = world.getTileAt((int)vec.x, (int)vec.y);
        if(t.getType() != TileType.AIR) return false;

        int pID = world.getPlayer().getID();
        int chID = t.getChunk().getId();
        ItemType item = inv.getMaterialSlot().getItemStack().getItem().getType();

        //ExpiriumGame.get().getClientGateway().addSubPacket(new TilePlaceActionCS(pID, chID, t.getX(), t.getY(), item));
        return true;
    }
}
