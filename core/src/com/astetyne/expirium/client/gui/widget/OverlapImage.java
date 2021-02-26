package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.data.PlayerDataHandler;
import com.astetyne.expirium.client.world.ClientWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class OverlapImage extends Widget {

    private final static int transparentTime = 400;
    private final static int durationDamage = 800;

    private final ClientWorld world;
    private final PlayerDataHandler playerData;
    private byte lastHealth;
    private long startTimeDamage;

    public OverlapImage(ClientWorld world, PlayerDataHandler playerData) {
        this.world = world;
        this.playerData = playerData;
        lastHealth = -1;
        setTouchable(Touchable.disabled);
        setVisible(false);
    }

    @Override
    public void act(float delta) {

        if(lastHealth == -1 && playerData.getHealth() != 0) {
            lastHealth = playerData.getHealth();
            return;
        }

        if(lastHealth > playerData.getHealth()) {
            startTimeDamage = System.currentTimeMillis();
            lastHealth = playerData.getHealth();
            setVisible(true);
            setColor(Color.WHITE);
        }

        int diff = (int) (startTimeDamage + durationDamage - System.currentTimeMillis());

        if(diff < 0) {
            setVisible(false);
        }else if(diff < transparentTime) {
            getColor().a = (float)diff / transparentTime;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(Res.DAMAGE_OVERLAP, 0, 0, 2000, 1000);
        batch.setColor(Color.WHITE);
    }
}
