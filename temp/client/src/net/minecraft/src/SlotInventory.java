package net.minecraft.src;

public class SlotInventory {
	public final int slotNumber;
	public final int xDisplayPosition;
	public final int yDisplayPosition;
	public final IInventory inventory;
	private final GuiContainer guiContainer;

	public SlotInventory(GuiContainer guiContainer1, IInventory iInventory2, int i3, int i4, int i5) {
		this.guiContainer = guiContainer1;
		this.inventory = iInventory2;
		this.slotNumber = i3;
		this.xDisplayPosition = i4;
		this.yDisplayPosition = i5;
	}

	public boolean getIsMouseOverSlot(int i1, int i2) {
		int i3 = (this.guiContainer.width - this.guiContainer.xSize) / 2;
		int i4 = (this.guiContainer.height - this.guiContainer.ySize) / 2;
		i1 -= i3;
		i2 -= i4;
		return i1 >= this.xDisplayPosition - 1 && i1 < this.xDisplayPosition + 16 + 1 && i2 >= this.yDisplayPosition - 1 && i2 < this.yDisplayPosition + 16 + 1;
	}

	public void onPickupFromSlot() {
		this.onSlotChanged();
	}

	public boolean isItemValid(ItemStack itemStack1) {
		return true;
	}

	public ItemStack getStack() {
		return this.inventory.getStackInSlot(this.slotNumber);
	}

	public void putStack(ItemStack itemStack1) {
		this.inventory.setInventorySlotContents(this.slotNumber, itemStack1);
		this.onSlotChanged();
	}

	public int getBackgroundIconIndex() {
		return -1;
	}

	public void onSlotChanged() {
		this.inventory.onInventoryChanged();
	}
}
