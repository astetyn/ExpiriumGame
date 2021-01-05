package com.astetyne.expirium.main.desktop;

import com.astetyne.expirium.main.ExpiGame;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = (int) (config.width / 2.1f);
		new LwjglApplication(new ExpiGame(), config);
	}
}
