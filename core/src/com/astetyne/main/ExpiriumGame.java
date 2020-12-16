package com.astetyne.main;

import com.astetyne.main.net.client.ClientGateway;
import com.astetyne.main.net.server.GameServer;
import com.astetyne.main.stages.ExpiStage;
import com.astetyne.main.stages.LauncherStage;
import com.badlogic.gdx.ApplicationAdapter;

public class ExpiriumGame extends ApplicationAdapter {

	private static ExpiriumGame game;
	private GameServer server;
	private ClientGateway clientGateway;
	private ExpiStage currentExpiStage;
	private final Object serverTickLock;
	private boolean available;
	private String playerName;

	public ExpiriumGame() {
		available = false;
		game = this;
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

	}

	public ClientGateway getClientGateway() {
		return clientGateway;
	}

	public static ExpiriumGame getGame() {
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
		currentExpiStage.onServerUpdate(clientGateway.getServerActions());
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
