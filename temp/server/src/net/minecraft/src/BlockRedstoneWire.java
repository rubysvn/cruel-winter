package net.minecraft.src;

import java.util.Random;

public class BlockRedstoneWire extends Block {
	private boolean wiresProvidePower = true;

	public BlockRedstoneWire(int id, int blockIndex) {
		super(id, blockIndex, Material.circuits);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return null;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public int getRenderType() {
		return 5;
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return world1.isBlockNormalCube(i2, i3 - 1, i4);
	}

	private void updateAndPropagateCurrentStrength(World world, int x, int y, int z) {
		int i5 = world.getBlockMetadata(x, y, z);
		int i6 = 0;
		this.wiresProvidePower = false;
		boolean z7 = world.isBlockIndirectlyGettingPowered(x, y, z);
		this.wiresProvidePower = true;
		int i8;
		int i9;
		int i10;
		if(z7) {
			i6 = 15;
		} else {
			for(i8 = 0; i8 < 4; ++i8) {
				i9 = x;
				i10 = z;
				if(i8 == 0) {
					i9 = x - 1;
				}

				if(i8 == 1) {
					++i9;
				}

				if(i8 == 2) {
					i10 = z - 1;
				}

				if(i8 == 3) {
					++i10;
				}

				i6 = this.getMaxCurrentStrength(world, i9, y, i10, i6);
				if(world.isBlockNormalCube(i9, y, i10) && !world.isBlockNormalCube(x, y + 1, z)) {
					i6 = this.getMaxCurrentStrength(world, i9, y + 1, i10, i6);
				} else if(!world.isBlockNormalCube(i9, y, i10)) {
					i6 = this.getMaxCurrentStrength(world, i9, y - 1, i10, i6);
				}
			}

			if(i6 > 0) {
				--i6;
			} else {
				i6 = 0;
			}
		}

		if(i5 != i6) {
			world.setBlockMetadataWithNotify(x, y, z, i6);
			world.markBlocksDirty(x, y, z, x, y, z);
			if(i6 > 0) {
				--i6;
			}

			for(i8 = 0; i8 < 4; ++i8) {
				i9 = x;
				i10 = z;
				int i11 = y - 1;
				if(i8 == 0) {
					i9 = x - 1;
				}

				if(i8 == 1) {
					++i9;
				}

				if(i8 == 2) {
					i10 = z - 1;
				}

				if(i8 == 3) {
					++i10;
				}

				if(world.isBlockNormalCube(i9, y, i10)) {
					i11 += 2;
				}

				int i12 = this.getMaxCurrentStrength(world, i9, y, i10, -1);
				if(i12 >= 0 && i12 != i6) {
					this.updateAndPropagateCurrentStrength(world, i9, y, i10);
				}

				i12 = this.getMaxCurrentStrength(world, i9, i11, i10, -1);
				if(i12 >= 0 && i12 != i6) {
					this.updateAndPropagateCurrentStrength(world, i9, i11, i10);
				}
			}

			if(i5 == 0 || i6 == 0) {
				world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
				world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
				world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
				world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
				world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
				world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
				world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
			}
		}

	}

	private void notifyWireNeighborsOfNeighborChange(World world, int x, int y, int z) {
		if(world.getBlockId(x, y, z) == this.blockID) {
			world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
			world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
			world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
			world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
			world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
			world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
			world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
		}
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		super.onBlockAdded(world1, i2, i3, i4);
		this.updateAndPropagateCurrentStrength(world1, i2, i3, i4);
		world1.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
		world1.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
		this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3, i4);
		this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3, i4);
		this.notifyWireNeighborsOfNeighborChange(world1, i2, i3, i4 - 1);
		this.notifyWireNeighborsOfNeighborChange(world1, i2, i3, i4 + 1);
		if(world1.isBlockNormalCube(i2 - 1, i3, i4)) {
			this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3 + 1, i4);
		} else {
			this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3 - 1, i4);
		}

		if(world1.isBlockNormalCube(i2 + 1, i3, i4)) {
			this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3 + 1, i4);
		} else {
			this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3 - 1, i4);
		}

		if(world1.isBlockNormalCube(i2, i3, i4 - 1)) {
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 + 1, i4 - 1);
		} else {
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 - 1, i4 - 1);
		}

		if(world1.isBlockNormalCube(i2, i3, i4 + 1)) {
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 + 1, i4 + 1);
		} else {
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 - 1, i4 + 1);
		}

	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
		super.onBlockRemoval(world1, i2, i3, i4);
		world1.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
		world1.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
		this.updateAndPropagateCurrentStrength(world1, i2, i3, i4);
		this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3, i4);
		this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3, i4);
		this.notifyWireNeighborsOfNeighborChange(world1, i2, i3, i4 - 1);
		this.notifyWireNeighborsOfNeighborChange(world1, i2, i3, i4 + 1);
		if(world1.isBlockNormalCube(i2 - 1, i3, i4)) {
			this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3 + 1, i4);
		} else {
			this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3 - 1, i4);
		}

		if(world1.isBlockNormalCube(i2 + 1, i3, i4)) {
			this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3 + 1, i4);
		} else {
			this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3 - 1, i4);
		}

		if(world1.isBlockNormalCube(i2, i3, i4 - 1)) {
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 + 1, i4 - 1);
		} else {
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 - 1, i4 - 1);
		}

		if(world1.isBlockNormalCube(i2, i3, i4 + 1)) {
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 + 1, i4 + 1);
		} else {
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 - 1, i4 + 1);
		}

	}

	private int getMaxCurrentStrength(World world, int x, int y, int z, int i5) {
		if(world.getBlockId(x, y, z) != this.blockID) {
			return i5;
		} else {
			int i6 = world.getBlockMetadata(x, y, z);
			return i6 > i5 ? i6 : i5;
		}
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		int i6 = world1.getBlockMetadata(i2, i3, i4);
		boolean z7 = this.canPlaceBlockAt(world1, i2, i3, i4);
		if(!z7) {
			this.dropBlockAsItem(world1, i2, i3, i4, i6);
			world1.setBlockWithNotify(i2, i3, i4, 0);
		} else {
			this.updateAndPropagateCurrentStrength(world1, i2, i3, i4);
		}

		super.onNeighborBlockChange(world1, i2, i3, i4, i5);
	}

	public int idDropped(int i1, Random random2) {
		return Item.redstone.shiftedIndex;
	}

	public boolean isIndirectlyPoweringTo(World world1, int i2, int i3, int i4, int i5) {
		return !this.wiresProvidePower ? false : this.isPoweringTo(world1, i2, i3, i4, i5);
	}

	public boolean isPoweringTo(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		if(!this.wiresProvidePower) {
			return false;
		} else if(iBlockAccess1.getBlockMetadata(i2, i3, i4) == 0) {
			return false;
		} else if(i5 == 1) {
			return true;
		} else {
			boolean z6 = isPowerProviderOrWire(iBlockAccess1, i2 - 1, i3, i4) || !iBlockAccess1.isBlockNormalCube(i2 - 1, i3, i4) && isPowerProviderOrWire(iBlockAccess1, i2 - 1, i3 - 1, i4);
			boolean z7 = isPowerProviderOrWire(iBlockAccess1, i2 + 1, i3, i4) || !iBlockAccess1.isBlockNormalCube(i2 + 1, i3, i4) && isPowerProviderOrWire(iBlockAccess1, i2 + 1, i3 - 1, i4);
			boolean z8 = isPowerProviderOrWire(iBlockAccess1, i2, i3, i4 - 1) || !iBlockAccess1.isBlockNormalCube(i2, i3, i4 - 1) && isPowerProviderOrWire(iBlockAccess1, i2, i3 - 1, i4 - 1);
			boolean z9 = isPowerProviderOrWire(iBlockAccess1, i2, i3, i4 + 1) || !iBlockAccess1.isBlockNormalCube(i2, i3, i4 + 1) && isPowerProviderOrWire(iBlockAccess1, i2, i3 - 1, i4 + 1);
			if(!iBlockAccess1.isBlockNormalCube(i2, i3 + 1, i4)) {
				if(iBlockAccess1.isBlockNormalCube(i2 - 1, i3, i4) && isPowerProviderOrWire(iBlockAccess1, i2 - 1, i3 + 1, i4)) {
					z6 = true;
				}

				if(iBlockAccess1.isBlockNormalCube(i2 + 1, i3, i4) && isPowerProviderOrWire(iBlockAccess1, i2 + 1, i3 + 1, i4)) {
					z7 = true;
				}

				if(iBlockAccess1.isBlockNormalCube(i2, i3, i4 - 1) && isPowerProviderOrWire(iBlockAccess1, i2, i3 + 1, i4 - 1)) {
					z8 = true;
				}

				if(iBlockAccess1.isBlockNormalCube(i2, i3, i4 + 1) && isPowerProviderOrWire(iBlockAccess1, i2, i3 + 1, i4 + 1)) {
					z9 = true;
				}
			}

			return !z8 && !z7 && !z6 && !z9 && i5 >= 2 && i5 <= 5 ? true : (i5 == 2 && z8 && !z6 && !z7 ? true : (i5 == 3 && z9 && !z6 && !z7 ? true : (i5 == 4 && z6 && !z8 && !z9 ? true : i5 == 5 && z7 && !z8 && !z9)));
		}
	}

	public boolean canProvidePower() {
		return this.wiresProvidePower;
	}

	public static boolean isPowerProviderOrWire(IBlockAccess blockAccess, int x, int y, int z) {
		int i4 = blockAccess.getBlockId(x, y, z);
		return i4 == Block.redstoneWire.blockID ? true : (i4 == 0 ? false : Block.canBlockGrass[i4].canProvidePower());
	}
}
