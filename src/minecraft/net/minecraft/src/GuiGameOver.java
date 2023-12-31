package net.minecraft.src;

import java.io.File;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;

public class GuiGameOver extends GuiScreen {
	public void initGui() {
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72, "Title menu"));
		if(this.mc.session == null) {
			((GuiButton)this.controlList.get(0)).enabled = false;
		}

	}

	protected void keyTyped(char c1, int i2) {
	}

	protected String getSaveName(int i1) {
		File file2 = Minecraft.getMinecraftDir();
		return World.getLevelData(file2, "World" + i1) != null ? "World" + i1 : null;
	}

	protected void actionPerformed(GuiButton guiButton1) {

		if(guiButton1.id == 0) {
			this.mc.changeWorld1((World)null);
			this.mc.displayGuiScreen(new GuiMainMenu());

			//File file3 = Minecraft.getMinecraftDir();
			//String string2 = this.getSaveName(this.mc.theWorld);
			//if(string2 != null) {
			//	World.deleteWorld(file3, string2);
			//}
		}

	}

	public void drawScreen(int i1, int i2, float f3) {
		this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
		GL11.glPushMatrix();
		GL11.glScalef(2.0F, 2.0F, 2.0F);
		this.drawCenteredString(this.fontRenderer, "Game over.", this.width / 2 / 2, 30, 0xFFFFFF);
		GL11.glPopMatrix();
		this.drawCenteredString(this.fontRenderer, "Score: &e" + this.mc.thePlayer.getScore(), this.width / 2, 100, 0xFFFFFF);
		super.drawScreen(i1, i2, f3);
	}

	public boolean doesGuiPauseGame() {
		return false;
	}
}
