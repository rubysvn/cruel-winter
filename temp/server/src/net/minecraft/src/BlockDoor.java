package net.minecraft.src;

import java.util.Random;

public class BlockDoor extends Block {
	protected BlockDoor(int i1, Material material2) {
		super(i1, material2);
		this.blockIndexInTexture = 97;
		if(material2 == Material.iron) {
			++this.blockIndexInTexture;
		}

		float f3 = 0.5F;
		float f4 = 1.0F;
		this.setBlockBounds(0.5F - f3, 0.0F, 0.5F - f3, 0.5F + f3, f4, 0.5F + f3);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public int getRenderType() {
		return 7;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		this.setBlockBoundsBasedOnState(world1, i2, i3, i4);
		return super.getCollisionBoundingBoxFromPool(world1, i2, i3, i4);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		this.setBlockBoundsFromState(this.getState(iBlockAccess1.getBlockMetadata(i2, i3, i4)));
	}

	public void setBlockBoundsFromState(int state) {
		float f2 = 0.1875F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		if(state == 0) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f2);
		}

		if(state == 1) {
			this.setBlockBounds(1.0F - f2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}

		if(state == 2) {
			this.setBlockBounds(0.0F, 0.0F, 1.0F - f2, 1.0F, 1.0F, 1.0F);
		}

		if(state == 3) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, f2, 1.0F, 1.0F);
		}

	}

	public void onBlockClicked(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		this.blockActivated(world1, i2, i3, i4, entityPlayer5);
	}

	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer) {
		if(this.material == Material.iron) {
			return true;
		} else {
			int i6 = world.getBlockMetadata(x, y, z);
			if((i6 & 8) != 0) {
				if(world.getBlockId(x, y - 1, z) == this.blockID) {
					this.blockActivated(world, x, y - 1, z, entityPlayer);
				}

				return true;
			} else {
				if(world.getBlockId(x, y + 1, z) == this.blockID) {
					world.setBlockMetadataWithNotify(x, y + 1, z, (i6 ^ 4) + 8);
				}

				world.setBlockMetadataWithNotify(x, y, z, i6 ^ 4);
				world.markBlocksDirty(x, y - 1, z, x, y, z);
				if(Math.random() < 0.5D) {
					world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.door_open", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
				} else {
					world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.door_close", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
				}

				return true;
			}
		}
	}

	public void onPoweredBlockChange(World world, int x, int y, int z, boolean flag) {
		int i6 = world.getBlockMetadata(x, y, z);
		if((i6 & 8) != 0) {
			if(world.getBlockId(x, y - 1, z) == this.blockID) {
				this.onPoweredBlockChange(world, x, y - 1, z, flag);
			}

		} else {
			boolean z7 = (world.getBlockMetadata(x, y, z) & 4) > 0;
			if(z7 != flag) {
				if(world.getBlockId(x, y + 1, z) == this.blockID) {
					world.setBlockMetadataWithNotify(x, y + 1, z, (i6 ^ 4) + 8);
				}

				world.setBlockMetadataWithNotify(x, y, z, i6 ^ 4);
				world.markBlocksDirty(x, y - 1, z, x, y, z);
				if(Math.random() < 0.5D) {
					world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.door_open", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
				} else {
					world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.door_close", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
				}

			}
		}
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		int i6 = world1.getBlockMetadata(i2, i3, i4);
		if((i6 & 8) != 0) {
			if(world1.getBlockId(i2, i3 - 1, i4) != this.blockID) {
				world1.setBlockWithNotify(i2, i3, i4, 0);
			}

			if(i5 > 0 && Block.canBlockGrass[i5].canProvidePower()) {
				this.onNeighborBlockChange(world1, i2, i3 - 1, i4, i5);
			}
		} else {
			boolean z7 = false;
			if(world1.getBlockId(i2, i3 + 1, i4) != this.blockID) {
				world1.setBlockWithNotify(i2, i3, i4, 0);
				z7 = true;
			}

			if(!world1.isBlockNormalCube(i2, i3 - 1, i4)) {
				world1.setBlockWithNotify(i2, i3, i4, 0);
				z7 = true;
				if(world1.getBlockId(i2, i3 + 1, i4) == this.blockID) {
					world1.setBlockWithNotify(i2, i3 + 1, i4, 0);
				}
			}

			if(z7) {
				this.dropBlockAsItem(world1, i2, i3, i4, i6);
			} else if(i5 > 0 && Block.canBlockGrass[i5].canProvidePower()) {
				boolean z8 = world1.isBlockIndirectlyGettingPowered(i2, i3, i4) || world1.isBlockIndirectlyGettingPowered(i2, i3 + 1, i4);
				this.onPoweredBlockChange(world1, i2, i3, i4, z8);
			}
		}

	}

	public int idDropped(int i1, Random random2) {
		return (i1 & 8) != 0 ? 0 : (this.material == Material.iron ? Item.doorSteel.shiftedIndex : Item.doorWood.shiftedIndex);
	}

	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3D vector1, Vec3D vector2) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.collisionRayTrace(world, x, y, z, vector1, vector2);
	}

	public int getState(int flag) {
		return (flag & 4) == 0 ? flag - 1 & 3 : flag & 3;
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return i3 >= 127 ? false : world1.isBlockNormalCube(i2, i3 - 1, i4) && super.canPlaceBlockAt(world1, i2, i3, i4) && super.canPlaceBlockAt(world1, i2, i3 + 1, i4);
	}
}
