package com.astetyne.expirium.client;

import com.astetyne.expirium.client.gui.roots.menu.LoadingRoot;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.net.ClientFailListener;
import com.astetyne.expirium.client.net.ClientGateway;
import com.astetyne.expirium.client.net.ClientPacketManager;
import com.astetyne.expirium.client.net.MulticastListener;
import com.astetyne.expirium.client.resources.BGRes;
import com.astetyne.expirium.client.resources.GuiRes;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.client.screens.MenuScreen;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.ServerFailListener;
import com.astetyne.expirium.server.ServerPreferences;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class ExpiGame extends Game implements ClientFailListener, ServerFailListener {

	public static final String version = "alpha 1.0";

	private static ExpiGame expiGame;

	private final ClientGateway clientGateway;
	private String playerName;
	private float timeSinceStart;
	private SpriteBatch batch;
	private boolean hostingServer;
	private String gameCode;
	public GameServer server;
	private final Queue<Runnable> tasks;
	private final MulticastListener multicastListener;

	public ExpiGame() {
		expiGame = this;
		clientGateway = new ClientGateway(this);
		timeSinceStart = 0;
		playerName = "";
		hostingServer = false;
		gameCode = "";
		tasks = new LinkedList<>();

		multicastListener = new MulticastListener();
		Thread t = new Thread(multicastListener);
		t.start();
	}

	@Override
	public void create () {
		GuiRes.loadTextures();
		BGRes.loadTextures();
		Res.loadTextures();
		TileTex.loadTextures();
		TileTexAnim.loadTextures();
		Item.loadTextures();
		batch = new SpriteBatch();
		setScreen(new MenuScreen());
	}

	public void update() {

		synchronized(tasks) {
			while(!tasks.isEmpty()) tasks.poll().run();
		}

		timeSinceStart += Gdx.graphics.getDeltaTime();
		clientGateway.update();
	}

	@Override
	public void render () {
		update();
		super.render();
	}

	@Override
	public void dispose () {
		super.dispose();
		multicastListener.end();
		getScreen().dispose();
		if(server != null) {
			server.close();
		}
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

	public void startServer(ServerPreferences preferences, MenuScreen menu) {
		hostingServer = true;
		menu.setRoot(new LoadingRoot("Loading world..."));
		gameCode = Utils.getGameCode();
		//todo: port free check
		Thread t = new Thread(() -> server = new GameServer(preferences, this));
		t.setName("Game Server");
		t.start();
	}

	public void stopServer() {
		if(server == null) return;
		server.close();
		server = null;
	}

	public void startClient(InetAddress address) throws UnknownHostException {
		clientGateway.connectToServer(address);
		if(!hostingServer) gameCode = Utils.getCodeFromAddress((Inet4Address)InetAddress.getLocalHost());
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public float getTimeSinceStart() {
		return timeSinceStart;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public boolean isHostingServer() {
		return hostingServer;
	}

	public String getGameCode() {
		return gameCode;
	}

	public void runOnMainThread(Runnable r) {
		synchronized(tasks) {
			tasks.add(r);
		}
	}

	@Override
	public void onClientFail(String msg) {
		runOnMainThread(() -> {
			if(isHostingServer()) {
				stopServer();
			}
			setScreen(new MenuScreen(msg));
		});
	}

	@Override
	public void onServerFail(String msg) {

	}
}
