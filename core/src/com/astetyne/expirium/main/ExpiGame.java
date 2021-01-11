package com.astetyne.expirium.main;

import com.astetyne.expirium.main.net.client.ClientGateway;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.screens.LauncherScreen;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.world.WorldSettings;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.net.Inet4Address;

public class ExpiGame extends Game {

	private static ExpiGame expiGame;

	private GameServer server;
	private final ClientGateway clientGateway;
	private final Object serverTickLock;
	private boolean available;
	private String playerName;
	private float timeSinceStart;
	private SpriteBatch batch;

	public ExpiGame() {
		expiGame = this;
		available = false;
		serverTickLock = new Object();
		clientGateway = new ClientGateway();
		timeSinceStart = 0;
	}

	@Override
	public void create () {
		Res.loadTextures();
		batch = new SpriteBatch();
		setScreen(new LauncherScreen());
	}

	public void update() {
		timeSinceStart += Gdx.graphics.getDeltaTime();
		checkServerMessages();
	}

	@Override
	public void render () {
		update();
		super.render();
	}

	@Override
	public void dispose () {
		super.dispose();
		getScreen().dispose();

		if(server != null) {
			server.stop();
		}
		clientGateway.end();
		Res.dispose();

	}

	public ClientGateway getClientGateway() {
		return clientGateway;
	}

	public static ExpiGame get() {
		return expiGame;
	}

	public void notifyServerUpdate() {
		synchronized(serverTickLock) {
			available = true;
		}
	}

	public void checkServerMessages() {
		synchronized(serverTickLock) {
			if(!available) return;
			available = false;
		}
		System.out.println("C: swapping!");
		clientGateway.swapBuffers();
		clientGateway.getManager().processIncomingPackets();

		if(GameScreen.get() != null) {
			GameScreen.get().getWorld().getPlayer().sendTSPacket();
		}

	}

	public void startServer(WorldSettings settings, boolean createNew, int tps, int port) {
		try {
			server = new GameServer(settings, createNew, tps, port);
		}catch(Exception e) {
			e.printStackTrace();
		}
		Thread t = new Thread(server);
		t.setName("Game Server");
		t.start();
	}

	public void startClient(Inet4Address address, String playerName) {
		this.playerName = playerName;
		clientGateway.setIpAddress(address);
		Thread t = new Thread(clientGateway);
		t.setName("Client gateway");
		t.start();
	}

	public String getPlayerName() {
		return playerName;
	}

	public float getTime() {
		return timeSinceStart;
	}

	public SpriteBatch getBatch() {
		return batch;
	}
}
