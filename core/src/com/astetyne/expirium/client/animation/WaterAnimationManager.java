package com.astetyne.expirium.client.animation;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.client.tiles.ClientTile;
import com.astetyne.expirium.client.utils.Consts;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WaterAnimationManager {

    private final ClientTile[][] terrain;
    private final int w, h;

    public WaterAnimationManager(ClientTile[][] terrain, int w, int h) {
        this.terrain = terrain;
        this.w = w;
        this.h = h;
    }

    public void draw(SpriteBatch batch, ClientTile t, int x, int y) {

        if(t.getWaterLevel() == 0) return;

        if((y != h-1 && terrain[x][y+1].getWaterLevel() > 0) || (y != 0 && terrain[x][y-1].getWaterLevel() != Consts.MAX_WATER_LEVEL && !terrain[x][y-1].getMaterial().isWatertight())) {
            batch.draw(TileTexAnim.WATER.getAnim().getKeyFrame(ExpiGame.get().getTimeSinceStart()), x, y, 1, 1);
        }else {
            batch.draw(TileTexAnim.WATER.getAnim().getKeyFrame(ExpiGame.get().getTimeSinceStart()), x, y, 1, (float)t.getWaterLevel()/Consts.MAX_WATER_LEVEL);
        }
    }
}
