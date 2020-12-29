package com.astetyne.expirium.main;

import com.astetyne.expirium.main.net.client.ClientGateway;
import com.astetyne.expirium.main.stages.ExpiStage;
import com.astetyne.expirium.main.stages.LauncherStage;
import com.astetyne.expirium.server.GameServer;
import com.badlogic.gdx.ApplicationAdapter;

public class ExpiriumGame extends ApplicationAdapter {

	private static ExpiriumGame game;
	private GameServer server;
	private final ClientGateway clientGateway;
	private ExpiStage currentExpiStage;
	private final Object serverTickLock;
	private boolean available;
	private String playerName;

	public ExpiriumGame() {
		game = this;
		available = false;
		serverTickLock = new Object();
		clientGateway = new ClientGateway();
	}

	@Override
	public void create () {
		Resources.loadTextures();
		currentExpiStage = new LauncherStage();
	}

	public void update() {
		checkServerMessages();
		currentExpiStage.update();
	}

	@Override
	public void render () {
		update();
		currentExpiStage.render();
	}

	@Override
	public void resize(int width, int height) {
		currentExpiStage.resize();
	}

	@Override
	public void dispose () {

		currentExpiStage.dispose();

		if(server != null) {
			server.stop();
		}
		clientGateway.end();
		Resources.dispose();

	}

	public ClientGateway getClientGateway() {
		return clientGateway;
	}

	public static ExpiriumGame get() {
		return game;
	}

	public ExpiStage getCurrentStage() {
		return currentExpiStage;
	}

	public void setCurrentStage(ExpiStage currentExpiStage) {
		this.currentExpiStage = currentExpiStage;
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
		currentExpiStage.onServerUpdate();
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
}
