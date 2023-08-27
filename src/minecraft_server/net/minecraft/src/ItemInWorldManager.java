package net.minecraft.src;

public class ItemInWorldManager {
	private World worldObj;
	public EntityPlayer thisPlayer;
	private float removeProgressUnused;
	private float removeProgress = 0.0F;
	private int curBlockDurability = 0;
	private float curblockDamage = 0.0F;
	private int posX;
	private int posY;
	private int posZ;

	public ItemInWorldManager(World world) {
		this.worldObj = world;
	}

	public void onBlockClicked(int i1, int i2, int i3) {
		int i4 = this.worldObj.getBlockId(i1, i2, i3);
		if(i4 > 0 && this.removeProgress == 0.0F) {
			Block.canBlockGrass[i4].onBlockClicked(this.worldObj, i1, i2, i3, this.thisPlayer);
		}

		if(i4 > 0 && Block.canBlockGrass[i4].blockStrength(this.thisPlayer) >= 1.0F) {
			this.tryHarvestBlock(i1, i2, i3);
		}

	}

	public void blockRemoving() {
		this.removeProgress = 0.0F;
		this.curBlockDurability = 0;
	}

	public void updateBlockRemoving(int i1, int i2, int i3, int i4) {
		if(this.curBlockDurability > 0) {
			--this.curBlockDurability;
		} else {
			if(i1 == this.posX && i2 == this.posY && i3 == this.posZ) {
				int i5 = this.worldObj.getBlockId(i1, i2, i3);
				if(i5 == 0) {
					return;
				}

				Block block6 = Block.canBlockGrass[i5];
				this.removeProgress += block6.blockStrength(this.thisPlayer);
				++this.curblockDamage;
				if(this.removeProgress >= 1.0F) {
					this.tryHarvestBlock(i1, i2, i3);
					this.removeProgress = 0.0F;
					this.removeProgressUnused = 0.0F;
					this.curblockDamage = 0.0F;
					this.curBlockDurability = 5;
				}
			} else {
				this.removeProgress = 0.0F;
				this.removeProgressUnused = 0.0F;
				this.curblockDamage = 0.0F;
				this.posX = i1;
				this.posY = i2;
				this.posZ = i3;
			}

		}
	}

	public boolean removeBlock(int i1, int i2, int i3) {
		Block block4 = Block.canBlockGrass[this.worldObj.getBlockId(i1, i2, i3)];
		int i5 = this.worldObj.getBlockMetadata(i1, i2, i3);
		boolean z6 = this.worldObj.setBlockWithNotify(i1, i2, i3, 0);
		if(block4 != null && z6) {
			block4.onBlockDestroyedByPlayer(this.worldObj, i1, i2, i3, i5);
		}

		return z6;
	}

	public boolean tryHarvestBlock(int i1, int i2, int i3) {
		int i4 = this.worldObj.getBlockId(i1, i2, i3);
		int i5 = this.worldObj.getBlockMetadata(i1, i2, i3);
		boolean z6 = this.removeBlock(i1, i2, i3);
		ItemStack itemStack7 = this.thisPlayer.getCurrentEquippedItem();
		if(itemStack7 != null) {
			itemStack7.onDestroyBlock(i4, i1, i2, i3);
			if(itemStack7.stackSize == 0) {
				itemStack7.onItemDestroyedByUse(this.thisPlayer);
				this.thisPlayer.destroyCurrentEquippedItem();
			}
		}

		if(z6 && this.thisPlayer.canHarvestBlock(Block.canBlockGrass[i4])) {
			Block.canBlockGrass[i4].dropBlockAsItem(this.worldObj, i1, i2, i3, i5);
		}

		return z6;
	}

	public boolean activeBlockOrUseItem(EntityPlayer entityPlayer1, World world2, ItemStack itemStack3, int i4, int i5, int i6, int i7) {
		return itemStack3.useItem(entityPlayer1, world2, i4, i5, i6, i7);
	}
}
