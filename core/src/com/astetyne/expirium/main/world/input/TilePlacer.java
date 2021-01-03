package com.astetyne.expirium.main.world.input;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Consts;
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
    private final GameWorld world;
    private final ImageButton stabilityButton;
    private boolean stabilityShowActive, pressed;

    public TilePlacer() {

        batch = GameStage.get().getBatch();
        world = GameStage.get().getWorld();
        stabilityShowActive = false;

        stabilityButton = new ImageButton(new TextureRegionDrawable(Res.DIRT_TILE));
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
            batch.draw(Res.WHITE_TILE, t.getC() * Consts.T_W_CH + t.getX(), t.getY(), 1, 1);
            batch.setColor(1, 1, 1, 1);
        }else {
            batch.draw(t.getTex(), t.getC() * Consts.T_W_CH + t.getX(), t.getY(), 1, 1);
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
        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        if(vec.x < 0 || vec.x >= Consts.T_W_CH * world.getChunks().length && vec.y < 0 || vec.y >= Consts.T_H_CH) {
            return false;
        }
        ExpiriumGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.PRESS);
        pressed = true;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(!pressed) return true;
        pressed = false;
        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        ExpiriumGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.RELEASE);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        if(vec.x < 0 || vec.x >= Consts.T_W_CH * world.getChunks().length && vec.y < 0 || vec.y >= Consts.T_H_CH) {
            return true;
        }
        ExpiriumGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.DRAG);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public ImageButton getStabilityButton() {
        return stabilityButton;
    }
}
