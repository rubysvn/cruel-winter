package net.minecraft.src;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class WorldClient extends World {
	private LinkedList blocksToReceive = new LinkedList();
	private NetClientHandler sendQueue;
	private ChunkProviderClient clientChunkProvider;
	private MCHashTable entityHashTable = new MCHashTable();
	private Set entityList = new HashSet();
	private Set entitySpawnQueue = new HashSet();

	public WorldClient(NetClientHandler netClientHandler) {
		super("MpServer");
		this.sendQueue = netClientHandler;
		this.spawnX = 8;
		this.spawnY = 64;
		this.spawnZ = 8;
	}

	public void tick() {
		int i1;
		for(i1 = 0; i1 < 10 && !this.entitySpawnQueue.isEmpty(); ++i1) {
			Entity entity2 = (Entity)this.entitySpawnQueue.iterator().next();
			this.spawnEntityInWorld(entity2);
		}

		this.sendQueue.processReadPackets();

		for(i1 = 0; i1 < this.blocksToReceive.size(); ++i1) {
			WorldBlockPositionType worldBlockPositionType3 = (WorldBlockPositionType)this.blocksToReceive.get(i1);
			if(--worldBlockPositionType3.acceptCountdown == 0) {
				super.setBlockAndMetadata(worldBlockPositionType3.posX, worldBlockPositionType3.posY, worldBlockPositionType3.posZ, worldBlockPositionType3.blockID, worldBlockPositionType3.metadata);
				super.markBlockNeedsUpdate(worldBlockPositionType3.posX, worldBlockPositionType3.posY, worldBlockPositionType3.posZ);
				this.blocksToReceive.remove(i1--);
			}
		}

	}

	public void invalidateBlockReceiveRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		for(int i7 = 0; i7 < this.blocksToReceive.size(); ++i7) {
			WorldBlockPositionType worldBlockPositionType8 = (WorldBlockPositionType)this.blocksToReceive.get(i7);
			if(worldBlockPositionType8.posX >= minX && worldBlockPositionType8.posY >= minY && worldBlockPositionType8.posZ >= minZ && worldBlockPositionType8.posX <= maxX && worldBlockPositionType8.posY <= maxY && worldBlockPositionType8.posZ <= maxZ) {
				this.blocksToReceive.remove(i7--);
			}
		}

	}

	protected IChunkProvider getChunkProvider(File file1) {
		this.clientChunkProvider = new ChunkProviderClient(this);
		return this.clientChunkProvider;
	}

	public void setSpawnLocation() {
		this.spawnX = 8;
		this.spawnY = 64;
		this.spawnZ = 8;
	}

	protected void updateBlocksAndPlayCaveSounds() {
	}

	public void scheduleBlockUpdate(int i1, int i2, int i3, int i4) {
	}

	public boolean tickUpdates(boolean z1) {
		return false;
	}

	public void doPreChunk(int x, int z, boolean mode) {
		if(mode) {
			this.clientChunkProvider.loadChunk(x, z);
		} else {
			this.clientChunkProvider.unloadChunk(x, z);
		}

		if(!mode) {
			this.markBlocksDirty(x * 16, 0, z * 16, x * 16 + 15, 128, z * 16 + 15);
		}

	}

	public boolean spawnEntityInWorld(Entity entity) {
		boolean z2 = super.spawnEntityInWorld(entity);
		if(entity instanceof EntityPlayerSP) {
			this.entityList.add(entity);
		}

		return z2;
	}

	public void setEntityDead(Entity entity1) {
		super.setEntityDead(entity1);
		if(entity1 instanceof EntityPlayerSP) {
			this.entityList.remove(entity1);
		}

	}

	protected void obtainEntitySkin(Entity entity) {
		super.obtainEntitySkin(entity);
		if(this.entitySpawnQueue.contains(entity)) {
			this.entitySpawnQueue.remove(entity);
		}

	}

	protected void releaseEntitySkin(Entity entity1) {
		super.releaseEntitySkin(entity1);
		if(this.entityList.contains(entity1)) {
			this.entitySpawnQueue.add(entity1);
		}

	}

	public void addEntityToWorld(int id, Entity entity) {
		this.entityList.add(entity);
		if(!this.spawnEntityInWorld(entity)) {
			this.entitySpawnQueue.add(entity);
		}

		this.entityHashTable.addKey(id, entity);
	}

	public Entity getEntityByID(int id) {
		return (Entity)this.entityHashTable.lookup(id);
	}

	public Entity removeEntityFromWorld(int id) {
		Entity entity2 = (Entity)this.entityHashTable.removeObject(id);
		if(entity2 != null) {
			this.entityList.remove(entity2);
			this.setEntityDead(entity2);
		}

		return entity2;
	}

	public boolean setBlockMetadata(int x, int y, int z, int metadata) {
		int i5 = this.getBlockId(x, y, z);
		int i6 = this.getBlockMetadata(x, y, z);
		if(super.setBlockMetadata(x, y, z, metadata)) {
			this.blocksToReceive.add(new WorldBlockPositionType(this, x, y, z, i5, i6));
			return true;
		} else {
			return false;
		}
	}

	public boolean setBlockAndMetadata(int x, int y, int z, int id, int metadata) {
		int i6 = this.getBlockId(x, y, z);
		int i7 = this.getBlockMetadata(x, y, z);
		if(super.setBlockAndMetadata(x, y, z, id, metadata)) {
			this.blocksToReceive.add(new WorldBlockPositionType(this, x, y, z, i6, i7));
			return true;
		} else {
			return false;
		}
	}

	public boolean setBlock(int i1, int i2, int i3, int i4) {
		int i5 = this.getBlockId(i1, i2, i3);
		int i6 = this.getBlockMetadata(i1, i2, i3);
		if(super.setBlock(i1, i2, i3, i4)) {
			this.blocksToReceive.add(new WorldBlockPositionType(this, i1, i2, i3, i5, i6));
			return true;
		} else {
			return false;
		}
	}

	public boolean handleBlockChange(int x, int y, int z, int id, int metadata) {
		this.invalidateBlockReceiveRegion(x, y, z, x, y, z);
		if(super.setBlockAndMetadata(x, y, z, id, metadata)) {
			this.notifyBlockChange(x, y, z, id);
			return true;
		} else {
			return false;
		}
	}

	public void sendQuittingDisconnectingPacket() {
		this.sendQueue.addToSendQueue(new Packet255KickDisconnect("Quitting"));
	}
}
