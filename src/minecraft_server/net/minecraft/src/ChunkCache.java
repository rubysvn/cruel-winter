package net.minecraft.src;

public class ChunkCache implements IBlockAccess {
	private int chunkX;
	private int chunkZ;
	private Chunk[][] chunkArray;
	private World worldObj;

	public ChunkCache(World world, int x, int i3, int z, int i5, int i6, int i7) {
		this.worldObj = world;
		this.chunkX = x >> 4;
		this.chunkZ = z >> 4;
		int i8 = i5 >> 4;
		int i9 = i7 >> 4;
		this.chunkArray = new Chunk[i8 - this.chunkX + 1][i9 - this.chunkZ + 1];

		for(int i10 = this.chunkX; i10 <= i8; ++i10) {
			for(int i11 = this.chunkZ; i11 <= i9; ++i11) {
				this.chunkArray[i10 - this.chunkX][i11 - this.chunkZ] = world.getChunkFromChunkCoords(i10, i11);
			}
		}

	}

	public int getBlockId(int blockX, int blockY, int blockZ) {
		if(blockY < 0) {
			return 0;
		} else if(blockY >= 128) {
			return 0;
		} else {
			int i4 = (blockX >> 4) - this.chunkX;
			int i5 = (blockZ >> 4) - this.chunkZ;
			return this.chunkArray[i4][i5].getBlockID(blockX & 15, blockY, blockZ & 15);
		}
	}

	public int getBlockMetadata(int x, int y, int z) {
		if(y < 0) {
			return 0;
		} else if(y >= 128) {
			return 0;
		} else {
			int i4 = (x >> 4) - this.chunkX;
			int i5 = (z >> 4) - this.chunkZ;
			return this.chunkArray[i4][i5].getBlockMetadata(x & 15, y, z & 15);
		}
	}

	public Material getBlockMaterial(int i1, int i2, int i3) {
		int i4 = this.getBlockId(i1, i2, i3);
		return i4 == 0 ? Material.air : Block.canBlockGrass[i4].material;
	}

	public boolean isBlockNormalCube(int x, int y, int z) {
		Block block4 = Block.canBlockGrass[this.getBlockId(x, y, z)];
		return block4 == null ? false : block4.isOpaqueCube();
	}
}
