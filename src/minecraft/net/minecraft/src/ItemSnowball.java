package net.minecraft.src;

public class ItemSnowball extends Item {
	public ItemSnowball(int i1) {
		super(i1);
		this.maxStackSize = 16;
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World worldObj, EntityPlayer entityPlayer) {
		--itemStack.stackSize;
		worldObj.playSoundAtEntity(entityPlayer, "random.bow", 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
		worldObj.spawnEntityInWorld(new EntitySnowball(worldObj, entityPlayer));
		return itemStack;
	}
}
