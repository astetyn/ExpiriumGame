package com.astetyne.expirium.client;

import com.astetyne.expirium.client.data.ExtraCellTexture;
import com.astetyne.expirium.client.data.InvVariableType;
import com.astetyne.expirium.client.gui.roots.menu.LoadingRoot;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.net.BroadcastListener;
import com.astetyne.expirium.client.net.ClientGateway;
import com.astetyne.expirium.client.net.ClientPacketManager;
import com.astetyne.expirium.client.net.FailListener;
import com.astetyne.expirium.client.resources.*;
import com.astetyne.expirium.client.screens.MenuScreen;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.ServerFailListener;
import com.astetyne.expirium.server.core.entity.player.LivingEffect;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

public class ExpiGame extends Game implements FailListener, ServerFailListener {

	private static ExpiGame expiGame;

	private final ClientGateway clientGateway;
	private String playerName;
	private PlayerCharacter character;
	private float timeSinceStart;
	private SpriteBatch batch;
	private boolean hostingServer;
	private ExpiServer server;
	private final Queue<Runnable> tasks;
	private final BroadcastListener broadcastListener;
	Box2DDebugRenderer debugRenderer;

	public ExpiGame() {
		expiGame = this;
		clientGateway = new ClientGateway(this);
		timeSinceStart = 0;
		playerName = "";
		character = PlayerCharacter.FENDER;
		hostingServer = false;
		tasks = new LinkedList<>();

		broadcastListener = new BroadcastListener();
		Thread t = new Thread(broadcastListener);
		t.setName("Multicast listener");
		t.start();
	}

	@Override
	public void create () {
		TextureAtlas bg = new TextureAtlas("background.atlas");
		TextureAtlas textures = new TextureAtlas("textures.atlas");
		LivingEffect.loadTextures(textures);
		GuiRes.loadTextures(textures);// must be called before Res
		Res.loadTextures(textures, bg);
		TileTex.loadTextures(textures);
		TileTexAnim.loadTextures(textures);
		Item.loadTextures(textures);
		ExtraCellTexture.loadTextures(textures);
		InvVariableType.loadTextures(textures);
		PlayerCharacter.loadTextures(textures);
		batch = new SpriteBatch();
		setScreen(new MenuScreen());
		debugRenderer = new Box2DDebugRenderer();
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
		// this is only for debug purposes, ultra unsafe
		/*if(server != null && server.getWorld() != null && GameScreen.get() != null && GameScreen.get().getWorld() != null) {
			debugRenderer.render(server.getWorld().getB2dWorld(), GameScreen.get().getWorld().getCamera().combined);
		}*/
	}

	@Override
	public void dispose () {
		super.dispose();
		broadcastListener.stop();
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

	public void startServer(MenuScreen menu, boolean createNew) {
		hostingServer = true;
		menu.setRoot(new LoadingRoot("Loading world..."));
		//todo: port free check
		server = new ExpiServer(this, createNew);
	}

	public void stopServer() {
		if(server == null) return;
		server.close();
	}

	public void connectToServer(InetAddress address) {
		clientGateway.connectToServer(address);
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public PlayerCharacter getCharacter() {
		return character;
	}

	public void setCharacter(PlayerCharacter character) {
		this.character = character;
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

	public BroadcastListener getMulticastListener() {
		return broadcastListener;
	}

	public void runOnMainThread(Runnable r) {
		synchronized(tasks) {
			tasks.add(r);
		}
	}

	@Override
	public void onFail(String msg) {
		System.out.println("client fail: "+msg);
		runOnMainThread(() -> {
			if(isHostingServer()) {
				stopServer();
			}
			setScreen(new MenuScreen(msg));
		});
	}

	@Override
	public void onServerFail(String msg) {
		System.out.println("server fail: "+msg);
		runOnMainThread(() -> {
			clientGateway.close();
			setScreen(new MenuScreen(msg));
		});
	}
}
