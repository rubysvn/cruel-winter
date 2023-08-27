package net.minecraft.src;

public class ItemBucket extends Item {
	private int isFull;

	public ItemBucket(int id, int liquid) {
		super(id);
		this.maxStackSize = 1;
		this.maxDamage = 64;
		this.isFull = liquid;
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
		MovingObjectPosition movingObjectPosition24 = worldObj.rayTraceBlocks_do(vec3D13, vec3D23, this.isFull == 0);
		if(movingObjectPosition24 == null) {
			return itemStack;
		} else {
			if(movingObjectPosition24.typeOfHit == 0) {
				int i25 = movingObjectPosition24.blockX;
				int i26 = movingObjectPosition24.blockY;
				int i27 = movingObjectPosition24.blockZ;
				if(this.isFull == 0) {
					if(worldObj.getBlockMaterial(i25, i26, i27) == Material.water && worldObj.getBlockMetadata(i25, i26, i27) == 0) {
						worldObj.setBlockWithNotify(i25, i26, i27, 0);
						return new ItemStack(Item.bucketWater);
					}

					if(worldObj.getBlockMaterial(i25, i26, i27) == Material.lava && worldObj.getBlockMetadata(i25, i26, i27) == 0) {
						worldObj.setBlockWithNotify(i25, i26, i27, 0);
						return new ItemStack(Item.bucketLava);
					}
				} else {
					if(movingObjectPosition24.sideHit == 0) {
						--i26;
					}

					if(movingObjectPosition24.sideHit == 1) {
						++i26;
					}

					if(movingObjectPosition24.sideHit == 2) {
						--i27;
					}

					if(movingObjectPosition24.sideHit == 3) {
						++i27;
					}

					if(movingObjectPosition24.sideHit == 4) {
						--i25;
					}

					if(movingObjectPosition24.sideHit == 5) {
						++i25;
					}

					if(worldObj.getBlockId(i25, i26, i27) == 0 || !worldObj.getBlockMaterial(i25, i26, i27).isSolid()) {
						worldObj.setBlockAndMetadataWithNotify(i25, i26, i27, this.isFull, 0);
						return new ItemStack(Item.bucketEmpty);
					}
				}
			} else if(this.isFull == 0 && movingObjectPosition24.entityHit instanceof EntityCow) {
				return new ItemStack(Item.bucketMilk);
			}

			return itemStack;
		}
	}
}
