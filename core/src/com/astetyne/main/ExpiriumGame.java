package com.astetyne.main;

import com.astetyne.main.gui.GUIManager;
import com.astetyne.main.net.client.ClientGateway;
import com.astetyne.main.net.server.GameServer;
import com.astetyne.main.stages.LauncherStage;
import com.astetyne.main.stages.Stage;
import com.badlogic.gdx.ApplicationAdapter;

public class ExpiriumGame extends ApplicationAdapter {

	private static ExpiriumGame game;

	private GUIManager guiManager;

	private ClientGateway clientGateway;
	private GameServer server;

	private Stage currentStage;

	private final Object serverTickLock;
	private boolean available;

	public ExpiriumGame() {
		serverTickLock = new Object();
		available = false;
		game = this;
	}

	@Override
	public void create () {

		TextureManager.loadTextures();

		guiManager = new GUIManager();
		clientGateway = new ClientGateway(this);

		if(true) {
			server = new GameServer();
			new Thread(server).start();
		}

		new Thread(clientGateway).start();

		currentStage = new LauncherStage();

	}

	public void update() {

		checkServerMessages();

		currentStage.update();

		guiManager.update();

	}

	@Override
	public void render () {

		update();

		currentStage.render();

	}

	@Override
	public void resize(int width, int height) {
		guiManager.onResize();
		currentStage.resize();
	}
	
	@Override
	public void dispose () {

		clientGateway.end();
		currentStage.dispose();

		if(server != null) {
			server.stop();
		}

	}

	public void checkServerMessages() {

		synchronized(serverTickLock) {
			if(!available) return;
			available = false;
		}
		currentStage.onServerUpdate(clientGateway.getServerActions());
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

	public GUIManager getGui() {
		return guiManager;
	}

	public Stage getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(Stage currentStage) {
		this.currentStage = currentStage;
	}
}
