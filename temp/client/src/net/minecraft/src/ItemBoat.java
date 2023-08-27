package net.minecraft.src;

public class ItemBoat extends Item {
	public ItemBoat(int i1) {
		super(i1);
		this.maxStackSize = 1;
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World worldObj, EntityPlayer entityPlayer) {
		float f4 = 1.0F;
		float f5 = entityPlayer.prevRotationPitch + (entityPlayer.rotationPitch - entityPlayer.prevRotationPitch) * f4;
		float f6 = entityPlayer.prevRotationYaw + (entityPlayer.rotationYaw - entityPlayer.prevRotationYaw) * f4;
		double d7 = entityPlayer.prevPosX + (entityPlayer.posX - entityPlayer.prevPosX) * (double)f4;
		double d9 = entityPlayer.prevPosY + (entityPlayer.posY - entityPlayer.prevPosY) * (double)f4;
		double d11 = entityPlayer.prevPosZ + (entityPlayer.posZ - entityPlayer.prevPosZ) * (double)f4;
		Vec3D vec3D13 = Vec3D.createVector(d7, d9, d11);
		float f14 = MathHelper.cos(-f6 * 0.017453292F - (float)Math.PI);
		float f15 = MathHelper.sin(-f6 * 0.017453292F - (float)Math.PI);
		float f16 = -MathHelper.cos(-f5 * 0.017453292F);
		float f17 = MathHelper.sin(-f5 * 0.017453292F);
		float f18 = f15 * f16;
		float f20 = f14 * f16;
		double d21 = 5.0D;
		Vec3D vec3D23 = vec3D13.addVector((double)f18 * d21, (double)f17 * d21, (double)f20 * d21);
		MovingObjectPosition movingObjectPosition24 = worldObj.rayTraceBlocks_do(vec3D13, vec3D23, true);
		if(movingObjectPosition24 == null) {
			return itemStack;
		} else {
			if(movingObjectPosition24.typeOfHit == 0) {
				int i25 = movingObjectPosition24.blockX;
				int i26 = movingObjectPosition24.blockY;
				int i27 = movingObjectPosition24.blockZ;
				worldObj.spawnEntityInWorld(new EntityBoat(worldObj, (double)((float)i25 + 0.5F), (double)((float)i26 + 1.5F), (double)((float)i27 + 0.5F)));
				--itemStack.stackSize;
			}

			return itemStack;
		}
	}
}
