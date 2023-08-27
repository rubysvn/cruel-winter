package net.minecraft.src;

class SlotCrafting extends SlotInventory {
	private final IInventory craftMatrix;

	public SlotCrafting(GuiContainer guiContainer, IInventory inventory, IInventory resultInventory, int slotIndex, int x, int y) {
		super(guiContainer, resultInventory, slotIndex, x, y);
		this.craftMatrix = inventory;
	}

	public boolean isItemValid(ItemStack itemStack1) {
		return false;
	}

	public void onPickupFromSlot() {
		for(int i1 = 0; i1 < this.craftMatrix.getSizeInventory(); ++i1) {
			if(this.craftMatrix.getStackInSlot(i1) != null) {
				this.craftMatrix.decrStackSize(i1, 1);
			}
		}

	}
}
