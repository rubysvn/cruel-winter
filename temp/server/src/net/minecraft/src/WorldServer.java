package net.minecraft.src;

import java.io.File;

public class WorldServer extends World {
	public ChunkProviderServer chunkProviderServer;
	public boolean disableSpawnProtection = false;
	public boolean levelSaving;

	public WorldServer(File file1, String string2) {
		super(file1, string2);
		this.snowCovered = false;
	}

	protected IChunkProvider getChunkProvider(File file1) {
		this.chunkProviderServer = new ChunkProviderServer(this, new ChunkLoader(file1, true), new ChunkProviderGenerate(this, this.randomSeed));
		return this.chunkProviderServer;
	}

	public boolean setBlockAndMetadataWithNotify(int i1, int i2, int i3, int i4, int i5) {
		if(!this.disableSpawnProtection) {
			int i6 = i1 - this.spawnX;
			int i7 = i3 - this.spawnZ;
			if(i6 < 0) {
				i6 = -i6;
			}

			if(i7 < 0) {
				i7 = -i7;
			}

			if(i6 > i7) {
				i7 = i6;
			}

			if(i7 < 16) {
				return false;
			}
		}

		return super.setBlockAndMetadataWithNotify(i1, i2, i3, i4, i5);
	}

	public boolean setBlockWithNotify(int x, int y, int z, int i4) {
		if(!this.disableSpawnProtection) {
			int i5 = x - this.spawnX;
			int i6 = z - this.spawnZ;
			if(i5 < 0) {
				i5 = -i5;
			}

			if(i6 < 0) {
				i6 = -i6;
			}

			if(i5 > i6) {
				i6 = i5;
			}

			if(i6 < 16) {
				return false;
			}
		}

		return super.setBlockWithNotify(x, y, z, i4);
	}
}
