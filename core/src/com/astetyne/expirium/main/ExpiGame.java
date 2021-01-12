package com.astetyne.expirium.main;

import com.astetyne.expirium.main.net.client.ClientGateway;
import com.astetyne.expirium.main.net.client.ClientPacketManager;
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

	private final ClientGateway clientGateway;
	private final Object serverTickLock;
	private boolean nextPacketsAvailable;
	private String playerName;
	private float timeSinceStart;
	private SpriteBatch batch;
	private boolean hostingServer;

	public ExpiGame() {
		expiGame = this;
		nextPacketsAvailable = false;
		serverTickLock = new Object();
		clientGateway = new ClientGateway();
		timeSinceStart = 0;
		playerName = "";
		hostingServer = false;
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
		clientGateway.end();
		Res.dispose();
	}

	public ClientGateway getClientGateway() {
		return clientGateway;
	}

	public ClientPacketManager getNetManager() {
		return clientGateway.getManager();
	}

	public static ExpiGame get() {
		return expiGame;
	}

	public void notifyServerUpdate() {
		synchronized(serverTickLock) {
			nextPacketsAvailable = true;
		}
	}

	public void checkServerMessages() {
		synchronized(serverTickLock) {
			if(!nextPacketsAvailable) return;
			nextPacketsAvailable = false;
		}
		if(GameScreen.get() != null) {
			clientGateway.getManager().putTSPacket();
		}
		clientGateway.swapBuffers();
		clientGateway.getManager().processIncomingPackets();
	}

	public void startServer(WorldSettings settings, boolean createNew, int tps, int port) {
		hostingServer = true;
		GameServer server = new GameServer(settings, createNew, tps, port);
		Thread t = new Thread(server);
		t.setName("Game Server");
		t.start();
	}

	public void startClient(Inet4Address address) {
		clientGateway.setIpAddress(address);
		Thread t = new Thread(clientGateway);
		t.setName("Client gateway");
		t.start();
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public float getTime() {
		return timeSinceStart;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public boolean isHostingServer() {
		return hostingServer;
	}

	public void setHostingServer(boolean hostingServer) {
		this.hostingServer = hostingServer;
	}
}
