package net.minecraft.src;

public class TextureFX {
	public byte[] imageData = new byte[1024];
	public int iconIndex;
	public boolean anaglyphEnabled = false;
	public int textureId = 0;
	public int tileSize = 1;

	public TextureFX(int i1) {
		this.iconIndex = i1;
	}

	public void onTick() {
	}
}
