package com.astetyne.expirium.main.world.input;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.items.inventory.Inventory;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.world.GameWorld;
import com.astetyne.expirium.main.world.tiles.Tile;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TilePlacer implements InputProcessor {

    private final SpriteBatch batch;
    private final Inventory inv;
    private final GameWorld world;
    private final ImageButton stabilityButton;
    private boolean stabilityShowActive;

    public TilePlacer() {

        batch = GameStage.get().getBatch();
        inv = GameStage.get().getInv();
        world = GameStage.get().getWorld();
        stabilityShowActive = false;

        stabilityButton = new ImageButton(new TextureRegionDrawable(Res.DIRT_TEXTURE));
        stabilityButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stabilityShowActive = !stabilityShowActive;
            }
        });

    }

    public void render(Tile t) {

        if(t.getType() == TileType.AIR) return;

        if(stabilityShowActive) {

            if(t.getStability() == 1) {
                batch.setColor(0.9f, 0f, 0f, 1);
            }else if(t.getStability() == 2) {
                batch.setColor(0.9f, 0.3f, 0f, 1);
            }else if(t.getStability() == 3) {
                batch.setColor(0.9f, 0.6f, 0f, 1);
            }else if(t.getStability() == 4) {
                batch.setColor(0.9f, 0.9f, 0f, 1);
            }
            batch.draw(Res.WHITE_TILE, t.getChunk().getId() * Constants.T_W_CH + t.getX(), t.getY(), 1, 1);
            batch.setColor(1, 1, 1, 1);
        }else {
            batch.draw(t.getTexture(), t.getChunk().getId() * Constants.T_W_CH + t.getX(), t.getY(), 1, 1);
        }

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
        return canBePlaced(screenX, screenY);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return canBePlaced(screenX, screenY);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    private boolean canBePlaced(int screenX, int screenY) {
        if(!inv.getMaterialSlot().isFocused()) return false;

        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        Tile t = world.getTileAt((int)vec.x, (int)vec.y);
        if(t.getType() != TileType.AIR) return false;
        if(inv.getMaterialSlot().getItemStack() == null) return false;

        ItemType item = inv.getMaterialSlot().getItemStack().getItem();
        ExpiriumGame.get().getClientGateway().getManager().putTilePlaceReqPacket(t, item);
        return true;
    }

    public ImageButton getStabilityButton() {
        return stabilityButton;
    }
}