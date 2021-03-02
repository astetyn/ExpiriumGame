package com.astetyne.expirium.server.core.world;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.io.DataInputStream;
import java.io.IOException;

public class WeatherManager implements WorldSaveable {

    private final ExpiServer server;
    private WeatherType weather;
    private long endTick;

    public WeatherManager(ExpiServer server, DataInputStream in) throws IOException {
        this.server = server;
        this.weather = WeatherType.get(in.readByte());
        this.endTick = in.readLong();
    }

    public void onTick() {

        if(endTick <= server.getWorld().getTick() || Gdx.input.isKeyPressed(Input.Keys.K)) {
            // weather change
            if(weather == WeatherType.SUN) {
                weather = WeatherType.RAIN;
                endTick = server.getWorld().getTick() + Consts.TICKS_IN_HOUR * (5 + (int) (Math.random() * 24));
            }else if(weather == WeatherType.RAIN) {
                weather = WeatherType.SUN;
                endTick = server.getWorld().getTick() + Consts.TICKS_IN_HOUR * (12 + (int) (Math.random() * 72));
            }
            for(Player p : server.getPlayers()) {
                p.getNetManager().putWeatherChangePacket(weather);
            }
        }
    }

    public WeatherType getWeather() {
        return weather;
    }

    @Override
    public void writeData(WorldBuffer out) {
        out.writeByte((byte) weather.ordinal());
        out.writeLong(endTick);
    }
}
