package net.minecraft.server;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.ConsoleLogManager;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EntityTracker;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NetworkListenThread;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.PropertyManager;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.ServerGUI;
import net.minecraft.src.ThreadCommandReader;
import net.minecraft.src.ThreadServerApplication;
import net.minecraft.src.ThreadSleepForever;
import net.minecraft.src.Vec3D;
import net.minecraft.src.WorldManager;
import net.minecraft.src.WorldServer;

public class MinecraftServer implements Runnable {
	public static Logger logger = Logger.getLogger("Minecraft");
	public static HashMap playerList = new HashMap();
	public NetworkListenThread networkServer;
	public PropertyManager propertyManagerObj;
	public WorldServer worldMngr;
	public ServerConfigurationManager configManager;
	private boolean serverRunning = true;
	public boolean serverStopped = false;
	int deathTime = 0;
	public String currentTask;
	public int percentDone;
	private List playersOnline = new ArrayList();
	private List commands = Collections.synchronizedList(new ArrayList());
	public EntityTracker entityTracker;
	public boolean onlineMode;

	public MinecraftServer() {
		new ThreadSleepForever(this);
	}

	private boolean startServer() throws IOException {
		ThreadCommandReader threadCommandReader1 = new ThreadCommandReader(this);
		threadCommandReader1.setDaemon(true);
		threadCommandReader1.start();
		ConsoleLogManager.init();
		logger.info("Starting minecraft server version 0.1.2_01");
		if(Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
			logger.warning("**** NOT ENOUGH RAM!");
			logger.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
		}

		logger.info("Loading properties");
		this.propertyManagerObj = new PropertyManager(new File("server.properties"));
		String string2 = this.propertyManagerObj.getStringProperty("server-ip", "");
		this.onlineMode = this.propertyManagerObj.getBooleanProperty("online-mode", true);
		InetAddress inetAddress3 = null;
		if(string2.length() > 0) {
			inetAddress3 = InetAddress.getByName(string2);
		}

		int i4 = this.propertyManagerObj.getIntProperty("server-port", 25565);
		logger.info("Starting Minecraft server on " + (string2.length() == 0 ? "*" : string2) + ":" + i4);

		try {
			this.networkServer = new NetworkListenThread(this, inetAddress3, i4);
		} catch (IOException iOException6) {
			logger.warning("**** FAILED TO BIND TO PORT!");
			logger.log(Level.WARNING, "The exception was: " + iOException6.toString());
			logger.warning("Perhaps a server is already running on that port?");
			return false;
		}

		if(!this.onlineMode) {
			logger.warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
			logger.warning("The server will make no attempt to authenticate usernames. Beware.");
			logger.warning("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
			logger.warning("To change this, set \"online-mode\" to \"true\" in the server.settings file.");
		}

		this.configManager = new ServerConfigurationManager(this);
		this.entityTracker = new EntityTracker(this);
		String string5 = this.propertyManagerObj.getStringProperty("level-name", "world");
		logger.info("Preparing level \"" + string5 + "\"");
		this.initWorld(string5);
		logger.info("Done! For help, type \"help\" or \"?\"");
		return true;
	}

	private void initWorld(String worldName) {
		logger.info("Preparing start region");
		this.worldMngr = new WorldServer(new File("."), worldName);
		this.worldMngr.addWorldAccess(new WorldManager(this));
		byte b2 = 10;

		for(int i3 = -b2; i3 <= b2; ++i3) {
			this.outputPercentRemaining("Preparing spawn area", (i3 + b2) * 100 / (b2 + b2 + 1));

			for(int i4 = -b2; i4 <= b2; ++i4) {
				if(!this.serverRunning) {
					return;
				}

				this.worldMngr.chunkProviderServer.loadChunk((this.worldMngr.spawnX >> 4) + i3, (this.worldMngr.spawnZ >> 4) + i4);
			}
		}

		this.clearCurrentTask();
	}

	private void outputPercentRemaining(String currentTask, int percent) {
		this.currentTask = currentTask;
		this.percentDone = percent;
		System.out.println(currentTask + ": " + percent + "%");
	}

	private void clearCurrentTask() {
		this.currentTask = null;
		this.percentDone = 0;
	}

	private void save() {
		logger.info("Saving chunks");
		this.worldMngr.saveWorld(true, (IProgressUpdate)null);
	}

	private void stop() {
		logger.info("Stopping server");
		if(this.worldMngr != null) {
			this.save();
		}

	}

	public void stopRunning() {
		this.serverRunning = false;
	}

	public void run() {
		try {
			if(this.startServer()) {
				long j1 = System.currentTimeMillis();
				long j3 = 0L;

				while(this.serverRunning) {
					long j5 = System.currentTimeMillis();
					long j7 = j5 - j1;
					if(j7 > 2000L) {
						logger.warning("Can\'t keep up! Did the system time change, or is the server overloaded?");
						j7 = 2000L;
					}

					if(j7 < 0L) {
						logger.warning("Time ran backwards! Did the system time change?");
						j7 = 0L;
					}

					j3 += j7;
					j1 = j5;

					while(j3 > 50L) {
						j3 -= 50L;
						this.doTick();
					}

					Thread.sleep(1L);
				}
			} else {
				while(this.serverRunning) {
					this.commandLineParser();

					try {
						Thread.sleep(10L);
					} catch (InterruptedException interruptedException15) {
						interruptedException15.printStackTrace();
					}
				}
			}
		} catch (Exception exception16) {
			exception16.printStackTrace();
			logger.log(Level.SEVERE, "Unexpected exception", exception16);

			while(this.serverRunning) {
				this.commandLineParser();

				try {
					Thread.sleep(10L);
				} catch (InterruptedException interruptedException14) {
					interruptedException14.printStackTrace();
				}
			}
		} finally {
			this.stop();
			this.serverStopped = true;
			System.exit(0);
		}

	}

	private void doTick() throws IOException {
		ArrayList arrayList1 = new ArrayList();
		Iterator iterator2 = playerList.keySet().iterator();

		while(iterator2.hasNext()) {
			String string3 = (String)iterator2.next();
			int i4 = ((Integer)playerList.get(string3)).intValue();
			if(i4 > 0) {
				playerList.put(string3, i4 - 1);
			} else {
				arrayList1.add(string3);
			}
		}

		int i6;
		for(i6 = 0; i6 < arrayList1.size(); ++i6) {
			playerList.remove(arrayList1.get(i6));
		}

		AxisAlignedBB.clearBoundingBoxPool();
		Vec3D.initialize();
		++this.deathTime;
		this.worldMngr.tick();

		while(this.worldMngr.updatingLighting()) {
		}

		this.worldMngr.updateEntities();
		this.networkServer.handleNetworkListenThread();
		this.configManager.onTick();
		this.entityTracker.updateTrackedEntities();

		for(i6 = 0; i6 < this.playersOnline.size(); ++i6) {
			((ICommandListener)this.playersOnline.get(i6)).getUsername();
		}

		try {
			this.commandLineParser();
		} catch (Exception exception5) {
			logger.log(Level.WARNING, "Unexpected exception while parsing console command", exception5);
		}

	}

	public void addCommand(String command) {
		this.commands.add(command);
	}

	public void commandLineParser() {
		while(this.commands.size() > 0) {
			String string1 = (String)this.commands.remove(0);
			if(!string1.toLowerCase().startsWith("help") && !string1.toLowerCase().startsWith("?")) {
				if(string1.toLowerCase().startsWith("list")) {
					logger.info("Connected players: " + this.configManager.getPlayerList());
				} else if(string1.toLowerCase().startsWith("stop")) {
					this.serverRunning = false;
				} else if(string1.toLowerCase().startsWith("save-all")) {
					logger.log(Level.INFO, "Forcing save..");
					this.worldMngr.saveWorld(true, (IProgressUpdate)null);
					logger.log(Level.INFO, "Save complete.");
				} else if(string1.toLowerCase().startsWith("save-off")) {
					logger.log(Level.INFO, "Disabling level saving..");
					this.worldMngr.levelSaving = true;
				} else if(string1.toLowerCase().startsWith("save-on")) {
					logger.log(Level.INFO, "Enabling level saving..");
					this.worldMngr.levelSaving = false;
				} else {
					String string8;
					if(string1.toLowerCase().startsWith("op ")) {
						string8 = string1.substring(string1.indexOf(" ")).trim();
						this.configManager.opPlayer(string8);
						logger.log(Level.INFO, "Opping " + string8);
					} else if(string1.toLowerCase().startsWith("deop ")) {
						string8 = string1.substring(string1.indexOf(" ")).trim();
						this.configManager.deopPlayer(string8);
						logger.log(Level.INFO, "De-opping " + string8);
					} else if(string1.toLowerCase().startsWith("ban-ip ")) {
						string8 = string1.substring(string1.indexOf(" ")).trim();
						this.configManager.banIP(string8);
						logger.log(Level.INFO, "Banning ip " + string8);
					} else if(string1.toLowerCase().startsWith("pardon-ip ")) {
						string8 = string1.substring(string1.indexOf(" ")).trim();
						this.configManager.pardonIP(string8);
						logger.log(Level.INFO, "Pardoning ip " + string8);
					} else {
						EntityPlayerMP entityPlayerMP9;
						int i10;
						EntityPlayerMP entityPlayerMP12;
						if(string1.toLowerCase().startsWith("ban ")) {
							string8 = string1.substring(string1.indexOf(" ")).trim();
							this.configManager.banPlayer(string8);
							logger.log(Level.INFO, "Banning " + string8);
							entityPlayerMP9 = null;

							for(i10 = 0; i10 < this.configManager.playerEntities.size(); ++i10) {
								entityPlayerMP12 = (EntityPlayerMP)this.configManager.playerEntities.get(i10);
								if(entityPlayerMP12.username.equalsIgnoreCase(string8)) {
									entityPlayerMP9 = entityPlayerMP12;
								}
							}

							if(entityPlayerMP9 != null) {
								entityPlayerMP9.playerNetServerHandler.kickPlayer("Banned by admin");
							}
						} else if(string1.toLowerCase().startsWith("pardon ")) {
							string8 = string1.substring(string1.indexOf(" ")).trim();
							this.configManager.pardonPlayer(string8);
							logger.log(Level.INFO, "Pardoning " + string8);
						} else if(string1.toLowerCase().startsWith("kick ")) {
							string8 = string1.substring(string1.indexOf(" ")).trim();
							entityPlayerMP9 = null;

							for(i10 = 0; i10 < this.configManager.playerEntities.size(); ++i10) {
								entityPlayerMP12 = (EntityPlayerMP)this.configManager.playerEntities.get(i10);
								if(entityPlayerMP12.username.equalsIgnoreCase(string8)) {
									entityPlayerMP9 = entityPlayerMP12;
								}
							}

							if(entityPlayerMP9 != null) {
								entityPlayerMP9.playerNetServerHandler.kickPlayer("Kicked by admin");
								logger.log(Level.INFO, "Kicking " + entityPlayerMP9.username);
							} else {
								logger.log(Level.INFO, "Can\'t find user " + string8 + ". No kick.");
							}
						} else {
							String[] string2;
							EntityPlayerMP entityPlayerMP4;
							int i5;
							EntityPlayerMP entityPlayerMP6;
							if(string1.toLowerCase().startsWith("tp ")) {
								string2 = string1.split(" ");
								if(string2.length == 3) {
									entityPlayerMP9 = null;
									entityPlayerMP4 = null;

									for(i5 = 0; i5 < this.configManager.playerEntities.size(); ++i5) {
										entityPlayerMP6 = (EntityPlayerMP)this.configManager.playerEntities.get(i5);
										if(entityPlayerMP6.username.equalsIgnoreCase(string2[1])) {
											entityPlayerMP9 = entityPlayerMP6;
										}

										if(entityPlayerMP6.username.equalsIgnoreCase(string2[2])) {
											entityPlayerMP4 = entityPlayerMP6;
										}
									}

									if(entityPlayerMP9 == null) {
										logger.log(Level.INFO, "Can\'t find user " + string2[1] + ". No tp.");
									} else if(entityPlayerMP4 == null) {
										logger.log(Level.INFO, "Can\'t find user " + string2[2] + ". No tp.");
									} else {
										entityPlayerMP9.playerNetServerHandler.teleportTo(entityPlayerMP4.posX, entityPlayerMP4.posY, entityPlayerMP4.posZ, entityPlayerMP4.rotationYaw, entityPlayerMP4.rotationPitch);
										logger.log(Level.INFO, "Teleporting " + string2[1] + " to " + string2[2] + ".");
									}
								} else {
									logger.log(Level.INFO, "Teleporting " + string2[1] + " to " + string2[2] + ".");
								}
							} else if(string1.toLowerCase().startsWith("give ")) {
								string2 = string1.split(" ");
								if(string2.length != 3 && string2.length != 4) {
									return;
								}

								String string3 = string2[1];
								entityPlayerMP4 = null;

								for(i5 = 0; i5 < this.configManager.playerEntities.size(); ++i5) {
									entityPlayerMP6 = (EntityPlayerMP)this.configManager.playerEntities.get(i5);
									if(entityPlayerMP6.username.equalsIgnoreCase(string3)) {
										entityPlayerMP4 = entityPlayerMP6;
									}
								}

								if(entityPlayerMP4 != null) {
									try {
										i5 = Integer.parseInt(string2[2]);
										if(Item.itemsList[i5] != null) {
											logger.log(Level.INFO, "Giving " + entityPlayerMP4.username + " some " + i5);
											int i11 = 1;
											if(string2.length > 3) {
												i11 = this.parseInt(string2[3], 1);
											}

											if(i11 < 1) {
												i11 = 1;
											}

											if(i11 > 64) {
												i11 = 64;
											}

											entityPlayerMP4.dropPlayerItem(new ItemStack(i5, i11));
										} else {
											logger.log(Level.INFO, "There\'s no item with id " + i5);
										}
									} catch (NumberFormatException numberFormatException7) {
										logger.log(Level.INFO, "There\'s no item with id " + string2[2]);
									}
								} else {
									logger.log(Level.INFO, "Can\'t find user " + string3);
								}
							} else if(string1.toLowerCase().startsWith("say ")) {
								string1 = string1.substring(string1.indexOf(" ")).trim();
								logger.info("[Server] " + string1);
								this.configManager.sendPacketToPlayer(new Packet3Chat("\u00a7d[Server] " + string1));
							} else {
								logger.warning("Unknown console command. Type \"help\" for help.");
							}
						}
					}
				}
			} else {
				logger.info("To run the server without a gui, start it like this:");
				logger.info("   java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui");
				logger.info("Console commands:");
				logger.info("   help  or  ?               shows this message");
				logger.info("   kick <player>             removes a player from the server");
				logger.info("   ban <player>              bans a player from the server");
				logger.info("   pardon <player>           pardons a banned player so that they can connect again");
				logger.info("   ban-ip <ip>               bans an IP address from the server");
				logger.info("   pardon-ip <ip>            pardons a banned IP address so that they can connect again");
				logger.info("   op <player>               turns a player into an op");
				logger.info("   deop <player>             removes op status from a player");
				logger.info("   tp <player1> <player2>    moves one player to the same location as another player");
				logger.info("   give <player> <id> [num]  gives a player a resource");
				logger.info("   stop                      gracefully stops the server");
				logger.info("   save-all                  forces a server-wide level save");
				logger.info("   save-off                  disables terrain saving (useful for backup scripts)");
				logger.info("   save-on                   re-enables terrain saving");
				logger.info("   list                      lists all currently connected players");
				logger.info("   say <message>             broadcasts a message to all players");
			}
		}

	}

	private int parseInt(String string, int defaultValue) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException numberFormatException4) {
			return defaultValue;
		}
	}

	public void addToOnlinePlayerList(ICommandListener iCommandListener1) {
		this.playersOnline.add(iCommandListener1);
	}

	public static void main(String[] args) {
		try {
			MinecraftServer minecraftServer1 = new MinecraftServer();
			if(!GraphicsEnvironment.isHeadless() && (args.length <= 0 || !args[0].equals("nogui"))) {
				ServerGUI.initGui(minecraftServer1);
			}

			(new ThreadServerApplication("Server thread", minecraftServer1)).start();
		} catch (Exception exception2) {
			logger.log(Level.SEVERE, "Failed to start the minecraft server", exception2);
		}

	}

	public File getFile(String fileName) {
		return new File(fileName);
	}

	public static boolean isServerRunning(MinecraftServer mcServer) {
		return mcServer.serverRunning;
	}
}
