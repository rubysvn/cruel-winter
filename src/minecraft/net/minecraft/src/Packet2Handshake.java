package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet2Handshake extends Packet {
	public int protocol;
	public String username;
	public String password;

	public Packet2Handshake() {
	}

	public Packet2Handshake(String string1, String string2, int i3) {
		this.username = string1;
		this.password = string2;
		this.protocol = i3;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.protocol = dataInputStream1.readInt();
		this.username = dataInputStream1.readUTF();
		this.password = dataInputStream1.readUTF();
	}

	public void writePacket(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.protocol);
		dataOutputStream1.writeUTF(this.username);
		dataOutputStream1.writeUTF(this.password);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleLogin(this);
	}

	public int getPacketSize() {
		return 4 + this.username.length() + this.password.length() + 4;
	}
}
