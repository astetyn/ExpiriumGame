package com.astetyne.main;

import com.astetyne.main.net.client.ClientGateway;
import com.astetyne.main.net.server.GameServer;
import com.astetyne.main.stages.ExpiStage;
import com.astetyne.main.stages.LauncherStage;
import com.badlogic.gdx.ApplicationAdapter;

public class ExpiriumGame extends ApplicationAdapter {

	private static ExpiriumGame game;

	private ClientGateway clientGateway;
	private GameServer server;

	private ExpiStage currentExpiStage;

	private final Object serverTickLock;
	private boolean available;
	private String clientIpAddress;

	public ExpiriumGame() {
		serverTickLock = new Object();
		available = false;
		game = this;
	}

	@Override
	public void create () {
		TextureManager.loadTextures();
		clientGateway = new ClientGateway(this);
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

		clientGateway.end();
		currentExpiStage.dispose();

		if(server != null) {
			server.stop();
		}

	}

	public void checkServerMessages() {

		synchronized(serverTickLock) {
			if(!available) return;
			available = false;
		}
		currentExpiStage.onServerUpdate(clientGateway.getServerActions());
	}

	public ClientGateway getClientGateway() {
		return clientGateway;
	}

	public static ExpiriumGame getGame() {
		return game;
	}

	public void notifyServerTickLock() {
		synchronized(serverTickLock) {
			available = true;
		}
	}

	public ExpiStage getCurrentStage() {
		return currentExpiStage;
	}

	public void setCurrentStage(ExpiStage currentExpiStage) {
		this.currentExpiStage = currentExpiStage;
	}

	public void startServer() {
		server = new GameServer();
		new Thread(server).start();
	}

	public void startClient(String clientIpAddress) {
		this.clientIpAddress = clientIpAddress;
		new Thread(clientGateway).start();
	}

	public String getClientIpAddress() {
		return clientIpAddress;
	}

}
