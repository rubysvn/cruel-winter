package net.minecraft.src;

public class ItemFood extends Item {
	private int healAmount;

	public ItemFood(int id, int healAmount) {
		super(id);
		this.healAmount = healAmount;
		this.maxStackSize = 1;
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World worldObj, EntityPlayer entityPlayer) {
		--itemStack.stackSize;
		entityPlayer.heal(this.healAmount);
		return itemStack;
	}
}
