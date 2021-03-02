package com.astetyne.expirium.client.desktop;

import com.astetyne.expirium.client.ExpiGame;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1000;
		config.height = (int) (config.width / 2f);
		config.vSyncEnabled = false; // Setting to false disables vertical sync
		config.foregroundFPS = 80; // Setting to 0 disables foreground fps throttling
		config.backgroundFPS = 80; // Setting to 0 disables background fps throttling
		new LwjglApplication(new ExpiGame(), config);
	}
}
