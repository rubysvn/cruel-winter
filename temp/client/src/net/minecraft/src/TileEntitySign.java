package net.minecraft.src;

public class TileEntitySign extends TileEntity {
	public String[] signText = new String[]{"", "", "", ""};
	public int lineBeingEdited = -1;

	public void writeToNBT(NBTTagCompound compoundTag) {
		super.writeToNBT(compoundTag);
		compoundTag.setString("Text1", this.signText[0]);
		compoundTag.setString("Text2", this.signText[1]);
		compoundTag.setString("Text3", this.signText[2]);
		compoundTag.setString("Text4", this.signText[3]);
	}

	public void readFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readFromNBT(nBTTagCompound1);

		for(int i2 = 0; i2 < 4; ++i2) {
			this.signText[i2] = nBTTagCompound1.getString("Text" + (i2 + 1));
			if(this.signText[i2].length() > 15) {
				this.signText[i2] = this.signText[i2].substring(0, 15);
			}
		}

	}
}
