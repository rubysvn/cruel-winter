package net.minecraft.src;

import java.util.Vector;
import javax.swing.JList;

import net.minecraft.server.MinecraftServer;

public class PlayerListBox extends JList implements ICommandListener {
	private MinecraftServer a;
	private int b = 0;

	public PlayerListBox(MinecraftServer minecraftServer) {
		this.a = minecraftServer;
		minecraftServer.addToOnlinePlayerList(this);
	}

	public void getUsername() {
		if(this.b++ % 20 == 0) {
			Vector vector1 = new Vector();

			for(int i2 = 0; i2 < this.a.configManager.playerEntities.size(); ++i2) {
				vector1.add(((EntityPlayerMP)this.a.configManager.playerEntities.get(i2)).username);
			}

			this.setListData(vector1);
		}

	}
}
