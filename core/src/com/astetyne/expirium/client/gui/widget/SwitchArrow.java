package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.server.core.world.inventory.UIInteractType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class SwitchArrow extends Widget {

    private final TextureRegion texture;
    private boolean pressed;
    private final boolean toRight;

    public SwitchArrow(TextureRegion texture, UIInteractType onClick, boolean toRight) {

        this.texture = texture;
        pressed = false;
        this.toRight = toRight;

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ExpiGame.get().getClientGateway().getManager().putUIInteractPacket(onClick);
                pressed = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                pressed = false;
            }
        });

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if(pressed) {
            batch.setColor(Color.GRAY);
        }

        if(toRight) {
            batch.draw(texture, getX(), getY(), getWidth()/2, getHeight()/2, getWidth(), getHeight(), -1, 1, 0);
        }else {
            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        }
    }

}
