package net.minecraft.src;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;

public class NetLoginHandler extends NetHandler {
	public static Logger logger = Logger.getLogger("Minecraft");
	private static Random rand = new Random();
	public NetworkManager netManager;
	public boolean finishedProcessing = false;
	private MinecraftServer mcServer;
	private int loginTimer = 0;
	private String username = null;
	private Packet1Login packet1login = null;
	private String serverId = "";

	public NetLoginHandler(MinecraftServer minecraftServer, Socket socket, String threadName) throws IOException {
		this.mcServer = minecraftServer;
		this.netManager = new NetworkManager(socket, threadName, this);
	}

	public void tryLogin() throws IOException {
		if(this.packet1login != null) {
			this.doLogin(this.packet1login);
			this.packet1login = null;
		}

		if(this.loginTimer++ == 100) {
			this.kickUser("Took too long to log in");
		} else {
			this.netManager.processReadPackets();
		}

	}

	public void kickUser(String playerName) {
		logger.info("Disconnecting " + this.getUserAndIPString() + ": " + playerName);
		this.netManager.addToSendQueue(new Packet255KickDisconnect(playerName));
		this.netManager.serverShutdown();
		this.finishedProcessing = true;
	}

	public void handleHandshake(Packet2Handshake packet2Handshake1) {
		if(this.mcServer.onlineMode) {
			this.serverId = Long.toHexString(rand.nextLong());
			this.netManager.addToSendQueue(new Packet2Handshake(this.serverId));
		} else {
			this.netManager.addToSendQueue(new Packet2Handshake("-"));
		}

	}

	public void handleLogin(Packet1Login packet1Login1) {
		this.username = packet1Login1.username;
		if(packet1Login1.protocolVersion != 14) {
			this.kickUser("Outdated client!");
		} else {
			if(!this.mcServer.onlineMode) {
				this.doLogin(packet1Login1);
			} else {
				(new ThreadLoginVerifier(this, packet1Login1)).start();
			}

		}
	}

	public void doLogin(Packet1Login loginPacket) {
		EntityPlayerMP entityPlayerMP2 = this.mcServer.configManager.login(this, loginPacket.username, loginPacket.password);
		if(entityPlayerMP2 != null) {
			logger.info(this.getUserAndIPString() + " logged in");
			NetServerHandler netServerHandler3 = new NetServerHandler(this.mcServer, this.netManager, entityPlayerMP2);
			netServerHandler3.sendPacket(new Packet1Login("", "", 0));
			this.mcServer.configManager.playerLoggedIn(entityPlayerMP2);
			netServerHandler3.teleportTo(entityPlayerMP2.posX, entityPlayerMP2.posY, entityPlayerMP2.posZ, entityPlayerMP2.rotationYaw, entityPlayerMP2.rotationPitch);
			this.mcServer.networkServer.addPlayer(netServerHandler3);
		}

		this.finishedProcessing = true;
	}

	public void handleErrorMessage(String string1) {
		logger.info(this.getUserAndIPString() + " lost connection");
		this.finishedProcessing = true;
	}

	public void registerPacket(Packet packet) {
		this.kickUser("Protocol error");
	}

	public String getUserAndIPString() {
		return this.username != null ? this.username + " [" + this.netManager.getRemoteAddress().toString() + "]" : this.netManager.getRemoteAddress().toString();
	}

	static String getServerId(NetLoginHandler loginHandler) {
		return loginHandler.serverId;
	}

	static Packet1Login setLoginPacket(NetLoginHandler loginHandler, Packet1Login loginPacket) {
		return loginHandler.packet1login = loginPacket;
	}
}
