package net.minecraft.src;

public class GuiCredits extends GuiScreen {
	private GuiScreen parentScreen;
	protected String screenTitle = "Credits";

	public GuiCredits(GuiScreen guiScreen1) {
		this.parentScreen = guiScreen1;
	}

	public void initGui() {
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 6 + 168, "Done"));
	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.enabled) {

			if(guiButton1.id == 1) {
				this.mc.displayGuiScreen(this.parentScreen);
			}

		}
	}

	public void drawScreen(int i1, int i2, float f3) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 0xFFFFFF);
		
		/*the actual credits*/
		this.drawString(this.fontRenderer, "Programmed by rubysvn", this.width / 2 - 140, this.height / 4 - 60 + 60 + 0, 10526880);
		this.drawString(this.fontRenderer, "Programming assistance by TDFHD", this.width / 2 - 140, this.height / 4 - 60 + 80 + 0, 10526880);
		this.drawString(this.fontRenderer, "Textures by TDFHD & pichi", this.width / 2 - 140, this.height / 4 - 60 + 100 + 0, 10526880);
		
		super.drawScreen(i1, i2, f3);
	}
}
