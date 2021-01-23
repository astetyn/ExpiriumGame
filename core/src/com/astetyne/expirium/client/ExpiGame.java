package com.astetyne.expirium.client;

import com.astetyne.expirium.client.gui.roots.LauncherRoot;
import com.astetyne.expirium.client.gui.roots.LoadingRoot;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.net.ClientGateway;
import com.astetyne.expirium.client.net.ClientPacketManager;
import com.astetyne.expirium.client.resources.BGRes;
import com.astetyne.expirium.client.resources.GuiRes;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.screens.LauncherScreen;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.ServerPreferences;
import com.astetyne.expirium.server.api.world.generator.WorldLoadingException;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.net.*;
import java.util.Enumeration;

public class ExpiGame extends Game {

	public static final String version = "alpha 1.0";

	private static ExpiGame expiGame;

	private ClientGateway clientGateway;
	private final Object serverTickLock;
	private boolean nextPacketsAvailable;
	private String playerName;
	private float timeSinceStart;
	private SpriteBatch batch;
	private boolean hostingServer;
	private String gameCode;
	public GameServer server;

	public ExpiGame() {
		expiGame = this;
		nextPacketsAvailable = false;
		serverTickLock = new Object();
		timeSinceStart = 0;
		playerName = "";
		hostingServer = false;
		gameCode = "";
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
		setScreen(new LauncherScreen());
	}

	public void update() {
		timeSinceStart += Gdx.graphics.getDeltaTime();
		checkServerMessages();
		if(clientGateway != null && clientGateway.isConnectionBroken()) {
			if(isHostingServer()) {
				stopServer();
			}
			setScreen(new LauncherScreen("You were disconnected due to connection issue."));
			clientGateway = null;
		}
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
		if(server != null) {
			server.stop();
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

	public boolean startServer(ServerPreferences preferences) {
		hostingServer = true;
		LauncherScreen.get().setRoot(new LoadingRoot("Loading world..."));
		try {
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
						.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					if (intf.getName().contains("wlan")) {
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
								.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress()
									&& (inetAddress.getAddress().length == 4)) {
								System.out.println(inetAddress.getHostAddress());
								gameCode = Utils.getCodeFromAddress((Inet4Address) inetAddress);
							}
						}
					}
				}
			} catch (SocketException ex) {
				ex.printStackTrace();
			}
			server = new GameServer(preferences);
			//todo: port free check
		}catch(WorldLoadingException e) {
			LauncherScreen.get().setRoot(new LauncherRoot(e.getMessage()));
			return false;
		}
		Thread t = new Thread(server);
		t.setName("Game Server");
		t.start();
		return true;
	}

	public void stopServer() {
		if(server == null) return;
		server.stop();
		server = null;
	}

	public void startClient(Inet4Address address) throws UnknownHostException {
		clientGateway = new ClientGateway(address);
		if(!hostingServer) gameCode = Utils.getCodeFromAddress((Inet4Address)InetAddress.getLocalHost());
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

	public float getTimeSinceStart() {
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

	public String getGameCode() {
		return gameCode;
	}

}
