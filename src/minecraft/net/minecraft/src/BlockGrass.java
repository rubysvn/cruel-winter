package net.minecraft.src;

import java.util.Random;

public class BlockGrass extends Block {
	protected BlockGrass(int blockID) {
		super(blockID, Material.grass);
		this.blockIndexInTexture = 3;
		this.setTickOnLoad(true);
	}

	public int getBlockTexture(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		if(i5 == 1) {
			return 0;
		} else if(i5 == 0) {
			return 2;
		} else {
			Material material6 = iBlockAccess1.getBlockMaterial(i2, i3 + 1, i4);
			return material6 != Material.snow && material6 != Material.craftedSnow ? 3 : 68;
		}
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		if(world1.getBlockLightValue(i2, i3 + 1, i4) < 4 && world1.getBlockMaterial(i2, i3 + 1, i4).getCanBlockGrass()) {
			if(random5.nextInt(4) != 0) {
				return;
			}

			world1.setBlockWithNotify(i2, i3, i4, Block.dirt.blockID);
		}
	}

	public int idDropped(int i1, Random random2) {
		return Block.dirt.idDropped(0, random2);
	}
}
