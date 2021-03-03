package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.core.world.WeatherType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Background {

    private final ClientWorld world;
    private final Color orangeSkyC;
    private final Color daySkyC, dayHillsC;
    private final Color nightSkyC, nightHillsC;
    private final int parallaxWidth, parallaxWidth2, parallaxWidth3;
    private final int parallaxHeight;
    private final float sunWidth, sunHeight, starWidth, starHeight;
    private final Vector2[] stars;

    public Background(ClientWorld world) {
        this.world = world;
        orangeSkyC = new Color(230 / 255f, 128 / 255f, 64 / 255f, 1);
        daySkyC = new Color(0.6f, 0.8f, 1, 1);
        dayHillsC = new Color(1, 1, 1, 1);
        nightSkyC = new Color(0, 0, 0, 1);
        nightHillsC = new Color(0.2f, 0.2f, 0.2f, 1);
        parallaxWidth = 3200;
        parallaxWidth2 = 2800;
        parallaxWidth3 = 2000;
        parallaxHeight = 1300;
        sunWidth = 300;
        sunHeight = Utils.percFromW(sunWidth);
        starWidth = 10;
        starHeight = Utils.percFromW(starWidth);

        stars = new Vector2[100];
        for(int i = 0; i < stars.length; i++) {
            stars[i] = new Vector2((float) (Math.random()*2 - 1), (float)(Math.random()*2-1) * Consts.SCREEN_HEIGHT/2);
        }
    }

    public void draw(SpriteBatch batch, int time, WeatherType weather) {

        // sky
        Color sky = getSkyColor(batch.getColor(), time, weather);
        Gdx.gl.glClearColor(sky.r, sky.g, sky.b, sky.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(weather != WeatherType.RAIN) {

            int srm = Consts.SUNRISE_START + (Consts.SUNRISE_END - Consts.SUNRISE_START) / 2;
            int ssm = Consts.SUNSET_START + (Consts.SUNSET_END - Consts.SUNSET_START) / 2;

            batch.setColor(Color.WHITE);

            // sun
            if(time >= srm && time < ssm) {
                float scaled = (float) (time - srm) / (ssm - srm);
                float x = scaled * (Consts.SCREEN_WIDTH + sunWidth) - sunWidth;
                batch.draw(Res.SUN, x, getY(scaled), sunWidth, sunHeight);
            }else {

                int timePassed = time >= ssm ? time - ssm : time + (Consts.TICKS_IN_DAY - ssm);
                float scaled = (float) timePassed / (srm + Consts.TICKS_IN_DAY - ssm);
                float x = scaled * (Consts.SCREEN_WIDTH + sunWidth) - sunWidth;

                // stars
                float a = time >= ssm ? (float)(time - ssm) / (Consts.SUNSET_END - ssm) :
                        (float)(time - srm) * -1 / (srm - Consts.SUNRISE_START);
                a = Math.min(a, 1);
                a = Math.max(a, 0);
                Color c = batch.getColor();
                c.a = a;
                batch.setColor(c);

                for(Vector2 star : stars) {
                    batch.draw(Res.STAR, x/2 + star.x * ((Consts.SCREEN_WIDTH + sunWidth) - sunWidth), getY(scaled/2 + star.x) + star.y, starWidth, starHeight);
                }

                batch.setColor(Color.WHITE);

                // moon
                batch.draw(Res.MOON, x, getY(scaled), sunWidth, sunHeight);
            }
        }

        // hills
        float xShift1 = (world.getCamera().position.x*2) % parallaxWidth;
        float yShift1 = world.getCamera().position.y*4;
        float xShift2 = (world.getCamera().position.x*6) % parallaxWidth2;
        float yShift2 = world.getCamera().position.y*8;
        float xShift3 = (world.getCamera().position.x*8) % parallaxWidth3;
        float yShift3 = world.getCamera().position.y*10;

        batch.setColor(getBGColor(batch.getColor(), time, weather));
        Res.BG_1.draw(batch, -xShift1, -yShift1, parallaxWidth, parallaxHeight);
        Res.BG_1.draw(batch, parallaxWidth-xShift1, -yShift1, parallaxWidth, parallaxHeight);
        Res.BG_2.draw(batch, -xShift2, -yShift2, parallaxWidth2, parallaxHeight);
        Res.BG_2.draw(batch, parallaxWidth2-xShift2, -yShift2, parallaxWidth2, parallaxHeight);
        Res.BG_3.draw(batch, -xShift3, -yShift3, parallaxWidth3, parallaxHeight);
        Res.BG_3.draw(batch, parallaxWidth3-xShift3, -yShift3, parallaxWidth3, parallaxHeight);
        batch.setColor(Color.WHITE);

    }

    /** parabolic trajectory of sky elements
     * x should be from 0 to 1
     */
    private float getY(float x) {
        return Consts.SCREEN_HEIGHT - sunHeight / 2f - (float) (Math.pow(x * 2 - 1, 2)) * (Consts.SCREEN_HEIGHT / 2f);
    }

    private Color getSkyColor(Color c, int time, WeatherType weather) {

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

        if(weather == WeatherType.RAIN) {
            float avr = (c.r + c.g + c.b)/3;
            c.r = (avr + c.r)/2 * 0.7f;
            c.g = (avr + c.g)/2 * 0.7f;
            c.b = (avr + c.b)/2 * 0.7f;
        }

        return c;
    }

    private Color getBGColor(Color c, int time, WeatherType weather) {

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

        if(weather == WeatherType.RAIN) {
            c.mul(0.6f);
            c.a = 1;
        }

        return c;
    }

}
