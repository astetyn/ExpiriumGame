package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.resources.BGRes;
import com.astetyne.expirium.client.utils.Consts;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background {

    private final GameWorld world;
    private final Color orangeSkyC;
    private final Color daySkyC, dayHillsC;
    private final Color nightSkyC, nightHillsC;
    private final int parallaxWidth, parallaxWidth2, parallaxWidth3;
    private final int parallaxHeight;

    public Background(GameWorld world) {
        this.world = world;
        orangeSkyC = new Color(230 / 255f, 128 / 255f, 64 / 255f, 1);
        daySkyC = new Color(0.6f, 0.8f, 1, 1);
        dayHillsC = new Color(1, 1, 1, 1);
        nightSkyC = new Color(0, 0, 0, 1);
        nightHillsC = new Color(0.1f, 0.1f, 0.1f, 1);
        parallaxWidth = 3200;
        parallaxWidth2 = 2800;
        parallaxWidth3 = 2000;
        parallaxHeight = 1300;
    }

    public void draw(SpriteBatch batch, int time) {

        // sky
        Color sky = getSkyColor(batch.getColor(), time);
        Gdx.gl.glClearColor(sky.r, sky.g, sky.b, sky.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // hills
        float xShift1 = (world.getCamera().position.x*2) % parallaxWidth;
        float yShift1 = world.getCamera().position.y*4;
        float xShift2 = (world.getCamera().position.x*6) % parallaxWidth2;
        float yShift2 = world.getCamera().position.y*7;
        float xShift3 = (world.getCamera().position.x*8) % parallaxWidth3;
        float yShift3 = world.getCamera().position.y*8;

        batch.setColor(getBGColor(batch.getColor(), time));
        BGRes.BACKGROUND_1.getDrawable().draw(batch, -xShift1, -yShift1, parallaxWidth, parallaxHeight);
        BGRes.BACKGROUND_1.getDrawable().draw(batch, parallaxWidth-xShift1, -yShift1, parallaxWidth, parallaxHeight);
        BGRes.BACKGROUND_2.getDrawable().draw(batch, -xShift2, -yShift2, parallaxWidth2, parallaxHeight);
        BGRes.BACKGROUND_2.getDrawable().draw(batch, parallaxWidth2-xShift2, -yShift2, parallaxWidth2, parallaxHeight);
        BGRes.BACKGROUND_3.getDrawable().draw(batch, -xShift3, -yShift3, parallaxWidth3, parallaxHeight);
        BGRes.BACKGROUND_3.getDrawable().draw(batch, parallaxWidth3-xShift3, -yShift3, parallaxWidth3, parallaxHeight);
        batch.setColor(Color.WHITE);

    }

    private Color getSkyColor(Color c, int time) {

        int srs = Consts.SUNRISE_START;
        int srh = (Consts.SUNRISE_END - Consts.SUNRISE_START)/2;
        int srm = srs + srh;
        int sre = Consts.SUNRISE_END;

        int sss = Consts.SUNSET_START;
        int ssh = (Consts.SUNSET_END - Consts.SUNSET_START)/2;
        int ssm = sss + ssh;
        int sse = Consts.SUNSET_END;

        if(time >= srs && time < srm) { // sunrise 1
            c.set(nightSkyC);
            c.lerp(orangeSkyC, 1f / srh * (time-srs));

        }else if(time >= srm  && time < sre) { // sunrise 2
            c.set(orangeSkyC);
            c.lerp(daySkyC, 1f / srh * (time - srm));

        }else if(time >= sre && time < sss) { // day
            c.set(daySkyC);

        }else if(time >= sss && time < ssm) { // sunset 1
            c.set(daySkyC);
            c.lerp(orangeSkyC, 1f / ssh * (time - sss));

        }else if(time >= ssm && time < sse) { // sunset 2
            c.set(orangeSkyC);
            c.lerp(nightSkyC, 1f / ssh * (time - ssm));

        }else { // night
            c.set(nightSkyC);
        }
        return c;
    }

    private Color getBGColor(Color c, int time) {

        int srs = Consts.SUNRISE_START;
        int srh = (Consts.SUNRISE_END - Consts.SUNRISE_START)/2;
        int srm = srs + srh;
        int sre = Consts.SUNRISE_END;

        int sss = Consts.SUNSET_START;
        int ssh = (Consts.SUNSET_END - Consts.SUNSET_START)/2;
        int ssm = sss + ssh;
        int sse = Consts.SUNSET_END;

        if(time >= srs && time < srm) { // sunrise 1
            c.set(nightHillsC);
            c.lerp(orangeSkyC, 1f / srh * (time-srs));

        }else if(time >= srm  && time < sre) { // sunrise 2
            c.set(orangeSkyC);
            c.lerp(dayHillsC, 1f / srh * (time - srm));

        }else if(time >= sre && time < sss) { // day
            c.set(dayHillsC);

        }else if(time >= sss && time < ssm) { // sunset 1
            c.set(dayHillsC);
            c.lerp(orangeSkyC, 1f / ssh * (time - sss));

        }else if(time >= ssm && time < sse) { // sunset 2
            c.set(orangeSkyC);
            c.lerp(nightHillsC, 1f / ssh * (time - ssm));

        }else { // night
            c.set(nightHillsC);
        }
        return c;

    }

}
