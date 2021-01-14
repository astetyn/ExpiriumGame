package com.astetyne.expirium.client.desktop;

import com.astetyne.expirium.client.ExpiGame;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1000;
		config.height = (int) (config.width / 2f);
		new LwjglApplication(new ExpiGame(), config);
	}
}
