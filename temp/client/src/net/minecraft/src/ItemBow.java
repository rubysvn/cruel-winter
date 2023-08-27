package net.minecraft.src;

public class ItemBow extends Item {
	public ItemBow(int i1) {
		super(i1);
		this.maxStackSize = 1;
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World worldObj, EntityPlayer entityPlayer) {
		if(entityPlayer.inventory.consumeInventoryItem(Item.arrow.shiftedIndex)) {
			worldObj.playSoundAtEntity(entityPlayer, "random.bow", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
			worldObj.spawnEntityInWorld(new EntityArrow(worldObj, entityPlayer));
		}

		return itemStack;
	}
}
