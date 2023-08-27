package net.minecraft.src;

public class ItemSoup extends ItemFood {
	public ItemSoup(int i1, int i2) {
		super(i1, i2);
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World worldObj, EntityPlayer entityPlayer) {
		super.onItemRightClick(itemStack, worldObj, entityPlayer);
		return new ItemStack(Item.bowlEmpty);
	}
}
