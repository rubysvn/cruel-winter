package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class GuiCrafting extends GuiContainer {
	private InventoryCrafting craftingInventory = new InventoryCrafting(this, 3, 3);
	private IInventory l = new InventoryCraftResult();

	public GuiCrafting(InventoryPlayer inventoryPlayer1) {
		this.inventorySlots.add(new SlotCrafting(this, this.craftingInventory, this.l, 0, 124, 35));

		int i2;
		int i3;
		for(i2 = 0; i2 < 3; ++i2) {
			for(i3 = 0; i3 < 3; ++i3) {
				this.inventorySlots.add(new SlotInventory(this, this.craftingInventory, i3 + i2 * 3, 30 + i3 * 18, 17 + i2 * 18));
			}
		}

		for(i2 = 0; i2 < 3; ++i2) {
			for(i3 = 0; i3 < 9; ++i3) {
				this.inventorySlots.add(new SlotInventory(this, inventoryPlayer1, i3 + (i2 + 1) * 9, 8 + i3 * 18, 84 + i2 * 18));
			}
		}

		for(i2 = 0; i2 < 9; ++i2) {
			this.inventorySlots.add(new SlotInventory(this, inventoryPlayer1, i2, 8 + i2 * 18, 142));
		}

	}

	public void onGuiClosed() {
		super.onGuiClosed();

		for(int i1 = 0; i1 < 9; ++i1) {
			ItemStack itemStack2 = this.craftingInventory.getStackInSlot(i1);
			if(itemStack2 != null) {
				this.mc.thePlayer.dropPlayerItem(itemStack2);
			}
		}

	}

	public void a(IInventory iInventory1) {
		int[] i2 = new int[9];

		for(int i3 = 0; i3 < 3; ++i3) {
			for(int i4 = 0; i4 < 3; ++i4) {
				int i5 = i3 + i4 * 3;
				ItemStack itemStack6 = this.craftingInventory.getStackInSlot(i5);
				if(itemStack6 == null) {
					i2[i5] = -1;
				} else {
					i2[i5] = itemStack6.itemID;
				}
			}
		}

		this.l.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(i2));
	}

	protected void drawGuiContainerForegroundLayer() {
		this.fontRenderer.drawString("Crafting", 28, 6, 4210752);
		this.fontRenderer.drawString("Inventory", 8, this.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float f1) {
		int i2 = this.mc.renderEngine.getTexture("/gui/crafting.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(i2);
		int i3 = (this.width - this.xSize) / 2;
		int i4 = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i3, i4, 0, 0, this.xSize, this.ySize);
	}
}
