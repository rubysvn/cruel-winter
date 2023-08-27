package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public abstract class GuiContainer extends GuiScreen {
	private static RenderItem itemRenderer = new RenderItem();
	private ItemStack l = null;
	protected int xSize = 176;
	protected int ySize = 166;
	protected List inventorySlots = new ArrayList();

	public void drawScreen(int i1, int i2, float f3) {
		this.drawDefaultBackground();
		int i4 = (this.width - this.xSize) / 2;
		int i5 = (this.height - this.ySize) / 2;
		this.drawGuiContainerBackgroundLayer(f3);
		GL11.glPushMatrix();
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef((float)i4, (float)i5, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);

		for(int i6 = 0; i6 < this.inventorySlots.size(); ++i6) {
			SlotInventory slotInventory7 = (SlotInventory)this.inventorySlots.get(i6);
			this.drawSlotInventory(slotInventory7);
			if(slotInventory7.getIsMouseOverSlot(i1, i2)) {
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				int i8 = slotInventory7.xDisplayPosition;
				int i9 = slotInventory7.yDisplayPosition;
				this.drawGradientRect(i8, i9, i8 + 16, i9 + 16, -2130706433, -2130706433);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
		}

		if(this.l != null) {
			GL11.glTranslatef(0.0F, 0.0F, 32.0F);
			itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, this.l, i1 - i4 - 8, i2 - i5 - 8);
			itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, this.l, i1 - i4 - 8, i2 - i5 - 8);
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		this.drawGuiContainerForegroundLayer();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	protected void drawGuiContainerForegroundLayer() {
	}

	protected abstract void drawGuiContainerBackgroundLayer(float f1);

	private void drawSlotInventory(SlotInventory slotInventory) {
		IInventory iInventory2 = slotInventory.inventory;
		int i3 = slotInventory.slotNumber;
		int i4 = slotInventory.xDisplayPosition;
		int i5 = slotInventory.yDisplayPosition;
		ItemStack itemStack6 = iInventory2.getStackInSlot(i3);
		if(itemStack6 == null) {
			int i7 = slotInventory.getBackgroundIconIndex();
			if(i7 >= 0) {
				GL11.glDisable(GL11.GL_LIGHTING);
				this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/gui/items.png"));
				this.drawTexturedModalRect(i4, i5, i7 % 16 * 16, i7 / 16 * 16, 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
				return;
			}
		}

		itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, itemStack6, i4, i5);
		itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, itemStack6, i4, i5);
	}

	private SlotInventory getSlotAtPosition(int i1, int i2) {
		for(int i3 = 0; i3 < this.inventorySlots.size(); ++i3) {
			SlotInventory slotInventory4 = (SlotInventory)this.inventorySlots.get(i3);
			if(slotInventory4.getIsMouseOverSlot(i1, i2)) {
				return slotInventory4;
			}
		}

		return null;
	}

	protected void mouseClicked(int i1, int i2, int i3) {
		if(i3 == 0 || i3 == 1) {
			SlotInventory slotInventory4 = this.getSlotAtPosition(i1, i2);
			int i6;
			if(slotInventory4 != null) {
				slotInventory4.onSlotChanged();
				ItemStack itemStack5 = slotInventory4.getStack();
				if(itemStack5 != null || this.l != null) {
					if(itemStack5 != null && this.l == null) {
						i6 = i3 == 0 ? itemStack5.stackSize : (itemStack5.stackSize + 1) / 2;
						this.l = slotInventory4.inventory.decrStackSize(slotInventory4.slotNumber, i6);
						if(itemStack5.stackSize == 0) {
							slotInventory4.putStack((ItemStack)null);
						}

						slotInventory4.onPickupFromSlot();
					} else if(itemStack5 == null && this.l != null && slotInventory4.isItemValid(this.l)) {
						i6 = i3 == 0 ? this.l.stackSize : 1;
						if(i6 > slotInventory4.inventory.getInventoryStackLimit()) {
							i6 = slotInventory4.inventory.getInventoryStackLimit();
						}

						slotInventory4.putStack(this.l.splitStack(i6));
						if(this.l.stackSize == 0) {
							this.l = null;
						}
					} else if(itemStack5 != null && this.l != null) {
						if(slotInventory4.isItemValid(this.l)) {
							if(itemStack5.itemID != this.l.itemID) {
								if(this.l.stackSize <= slotInventory4.inventory.getInventoryStackLimit()) {
									slotInventory4.putStack(this.l);
									this.l = itemStack5;
								}
							} else if(itemStack5.itemID == this.l.itemID) {
								if(i3 == 0) {
									i6 = this.l.stackSize;
									if(i6 > slotInventory4.inventory.getInventoryStackLimit() - itemStack5.stackSize) {
										i6 = slotInventory4.inventory.getInventoryStackLimit() - itemStack5.stackSize;
									}

									if(i6 > this.l.getMaxStackSize() - itemStack5.stackSize) {
										i6 = this.l.getMaxStackSize() - itemStack5.stackSize;
									}

									this.l.splitStack(i6);
									if(this.l.stackSize == 0) {
										this.l = null;
									}

									itemStack5.stackSize += i6;
								} else if(i3 == 1) {
									i6 = 1;
									if(i6 > slotInventory4.inventory.getInventoryStackLimit() - itemStack5.stackSize) {
										i6 = slotInventory4.inventory.getInventoryStackLimit() - itemStack5.stackSize;
									}

									if(i6 > this.l.getMaxStackSize() - itemStack5.stackSize) {
										i6 = this.l.getMaxStackSize() - itemStack5.stackSize;
									}

									this.l.splitStack(i6);
									if(this.l.stackSize == 0) {
										this.l = null;
									}

									itemStack5.stackSize += i6;
								}
							}
						} else if(itemStack5.itemID == this.l.itemID && this.l.getMaxStackSize() > 1) {
							i6 = itemStack5.stackSize;
							if(i6 > 0 && i6 + this.l.stackSize <= this.l.getMaxStackSize()) {
								this.l.stackSize += i6;
								itemStack5.splitStack(i6);
								if(itemStack5.stackSize == 0) {
									slotInventory4.putStack((ItemStack)null);
								}

								slotInventory4.onPickupFromSlot();
							}
						}
					}
				}
			} else if(this.l != null) {
				int i8 = (this.width - this.xSize) / 2;
				i6 = (this.height - this.ySize) / 2;
				if(i1 < i8 || i2 < i6 || i1 >= i8 + this.xSize || i2 >= i6 + this.xSize) {
					EntityPlayerSP entityPlayerSP7 = this.mc.thePlayer;
					if(i3 == 0) {
						entityPlayerSP7.dropPlayerItem(this.l);
						this.l = null;
					}

					if(i3 == 1) {
						entityPlayerSP7.dropPlayerItem(this.l.splitStack(1));
						if(this.l.stackSize == 0) {
							this.l = null;
						}
					}
				}
			}
		}

	}

	protected void mouseMovedOrUp(int i1, int i2, int i3) {
		if(i3 == 0) {
			;
		}

	}

	protected void keyTyped(char c1, int i2) {
		if(i2 == 1 || i2 == this.mc.options.keyBindInventory.keyCode) {
			this.mc.displayGuiScreen((GuiScreen)null);
		}

	}

	public void onGuiClosed() {
		if(this.l != null) {
			this.mc.thePlayer.dropPlayerItem(this.l);
		}

	}

	public void a(IInventory iInventory1) {
	}

	public boolean doesGuiPauseGame() {
		return false;
	}
}
