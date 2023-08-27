package net.minecraft.src;

public class ItemSaddle extends Item {
	public ItemSaddle(int i1) {
		super(i1);
		this.maxStackSize = 1;
		this.maxDamage = 64;
	}

	public void saddleEntity(ItemStack itemStack, EntityLiving entityLiving) {
		if(entityLiving instanceof EntityPig) {
			EntityPig entityPig3 = (EntityPig)entityLiving;
			if(!entityPig3.saddled) {
				entityPig3.saddled = true;
				--itemStack.stackSize;
			}
		}

	}

	public void hitEntity(ItemStack itemStack1, EntityLiving entityLiving2) {
		this.saddleEntity(itemStack1, entityLiving2);
	}
}
