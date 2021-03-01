package com.astetyne.expirium.client.animation;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.client.tiles.ClientTile;
import com.astetyne.expirium.client.utils.Consts;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WaterAnimationManager {

    private final ClientTile[][] terrain;
    private final int w, h;
    private final float[] vertices;

    public WaterAnimationManager(ClientTile[][] terrain, int w, int h) {
        this.terrain = terrain;
        this.w = w;
        this.h = h;
        vertices = new float[20];
    }

    public void draw(SpriteBatch batch, ClientTile t, int x, int y) {
        // assuming tile has water level > 0

        TextureRegion reg = TileTexAnim.WATER.getAnim().getKeyFrame(ExpiGame.get().getTimeSinceStart());

        float waterLevelScaled = (float)t.getWaterLevel()/Consts.MAX_WATER_LEVEL;

        if(y != h-1 && terrain[x][y+1].getWaterLevel() > 0) {
            waterLevelScaled = 1;
        }
        //y != 0 && terrain[x][y-1].getWaterLevel() != Consts.MAX_WATER_LEVEL && !terrain[x][y-1].getMaterial().isWatertight()

        vertices[1] = vertices[16] = y;
        vertices[6] = vertices[11] = y + waterLevelScaled;
        vertices[9] = vertices[14] = reg.getV();
        vertices[4] = vertices[19] = reg.getV() + (reg.getV2() - reg.getV()) * waterLevelScaled;
        vertices[2] = vertices[7] = vertices[12] = vertices[17] = batch.getPackedColor();

        if(t.getMaterial().isSlopedRight()) {
            populateSloped(reg, x, true, waterLevelScaled);
        }else if(t.getMaterial().isSlopedLeft()) {
            populateSloped(reg, x, false, waterLevelScaled);
        }else {
            populateNormal(reg, x);
        }
        batch.draw(reg.getTexture(), vertices, 0, vertices.length);
    }

    private void populateNormal(TextureRegion reg, int x) {
        vertices[0] = vertices[5] = x;
        vertices[10] = vertices[15] = x + 1;
        vertices[3] = vertices[8] = reg.getU();
        vertices[13] = vertices[18] = reg.getU2();
    }

    private void populateSloped(TextureRegion reg, int x, boolean slopedRight, float waterLevelScaled) {
        if(slopedRight) {
            vertices[0] = vertices[5] = vertices[15] = x;
            vertices[10] = x + waterLevelScaled;
            vertices[3] = vertices[8] = vertices[18] = reg.getU();
            vertices[13] = reg.getU() + (reg.getU2() - reg.getU()) * waterLevelScaled;
        }else {
            vertices[5] = x + 1 - waterLevelScaled;
            vertices[0] = vertices[10] = vertices[15] = x + 1;
            vertices[8] = reg.getU2() + (reg.getU() - reg.getU2()) * waterLevelScaled;
            vertices[3] = vertices[13] = vertices[18] = reg.getU2();
        }
    }
}
