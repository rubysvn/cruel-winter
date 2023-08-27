package net.minecraft.src;

public class InventoryCrafting implements IInventory {
	private ItemStack[] stackList;
	private int gridSize;
	private GuiContainer craftingInventory;

	public InventoryCrafting(GuiContainer guiContainer1, int i2, int i3) {
		this.gridSize = i2 * i3;
		this.stackList = new ItemStack[this.gridSize];
		this.craftingInventory = guiContainer1;
	}

	public InventoryCrafting(GuiContainer guiContainer1, ItemStack[] itemStack2) {
		this.gridSize = itemStack2.length;
		this.stackList = itemStack2;
		this.craftingInventory = guiContainer1;
	}

	public int getSizeInventory() {
		return this.gridSize;
	}

	public ItemStack getStackInSlot(int i1) {
		return this.stackList[i1];
	}

	public String getInvName() {
		return "Crafting";
	}

	public ItemStack decrStackSize(int i1, int i2) {
		if(this.stackList[i1] != null) {
			ItemStack itemStack3;
			if(this.stackList[i1].stackSize <= i2) {
				itemStack3 = this.stackList[i1];
				this.stackList[i1] = null;
				this.craftingInventory.a(this);
				return itemStack3;
			} else {
				itemStack3 = this.stackList[i1].splitStack(i2);
				if(this.stackList[i1].stackSize == 0) {
					this.stackList[i1] = null;
				}

				this.craftingInventory.a(this);
				return itemStack3;
			}
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i1, ItemStack itemStack2) {
		this.stackList[i1] = itemStack2;
		this.craftingInventory.a(this);
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void onInventoryChanged() {
	}
}
