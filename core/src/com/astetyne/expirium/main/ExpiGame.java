package com.astetyne.expirium.main;

import com.astetyne.expirium.main.net.client.ClientGateway;
import com.astetyne.expirium.main.screens.Gatewayable;
import com.astetyne.expirium.main.screens.LauncherScreen;
import com.astetyne.expirium.server.GameServer;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
		clientGateway.swapBuffers();
		clientGateway.getManager().processIncomingPackets();
		if(getScreen() instanceof Gatewayable) {
			((Gatewayable)getScreen()).onServerUpdate();
		}
	}

	public void startServer() {
		server = new GameServer();
		new Thread(server).start();
	}

	public void startClient(String clientIpAddress, String playerName) {
		this.playerName = playerName;
		clientGateway.setIpAddress(clientIpAddress);
		new Thread(clientGateway).start();
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
