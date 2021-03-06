package com.astetyne.expirium.server.core.world;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;

import java.io.DataInputStream;
import java.io.IOException;

public class WeatherManager implements WorldSaveable {

    private final ExpiServer server;
    private WeatherType weather;
    private long endTick;
    private final float surfaceWatersPerTick;
    private float reminder;

    public WeatherManager(World world, DataInputStream in) throws IOException {
        this.server = world.getServer();
        this.weather = WeatherType.get(in.readByte());
        this.endTick = in.readLong();
        surfaceWatersPerTick = world.getTerrainWidth() / (Consts.SERVER_TPS * 70f);
        reminder = 0;
    }

    public void onTick() {

        if(endTick <= server.getWorld().getTick()) {
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

        if(weather == WeatherType.RAIN) {
            int i = 0;
            for(; i < surfaceWatersPerTick + reminder; i++) {
                int x = (int) (Math.random() * (server.getWorld().getTerrainWidth() - 2) + 1);
                for(int y = server.getWorld().getTerrainHeight()-2; y >= 1; y--) {
                    if(server.getWorld().getTileAt(x, y).getMaterial().isWatertight()) {
                        if(!server.getWorld().getTileAt(x, y+1).getMaterial().isWatertight()) {
                            if(server.getWorld().getTileAt(x, y+1).getWaterLevel() != Consts.MAX_WATER_LEVEL) {
                                System.out.println("");
                                server.getWorld().getWaterEngine().increaseWaterLevel(server.getWorld().getTileAt(x, y + 1), 1);
                            }
                        }
                        break;
                    }
                }
            }
            reminder = surfaceWatersPerTick + reminder - i;
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
