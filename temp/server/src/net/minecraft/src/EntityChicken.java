package net.minecraft.src;

public class EntityChicken extends EntityAnimal {
	public boolean unusedBool2 = false;
	public float wingRotation = 0.0F;
	public float destPos = 0.0F;
	public float prevDestPos;
	public float prevWingRotation;
	public float moveWings = 1.0F;
	public int timeUntilNextEgg;

	public EntityChicken(World world1) {
		super(world1);
		this.texture = "/mob/chicken.png";
		this.setSize(0.3F, 0.4F);
		this.health = 4;
		this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.prevWingRotation = this.wingRotation;
		this.prevDestPos = this.destPos;
		this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3D);
		if(this.destPos < 0.0F) {
			this.destPos = 0.0F;
		}

		if(this.destPos > 1.0F) {
			this.destPos = 1.0F;
		}

		if(!this.onGround && this.moveWings < 1.0F) {
			this.moveWings = 1.0F;
		}

		this.moveWings = (float)((double)this.moveWings * 0.9D);
		if(!this.onGround && this.motionY < 0.0D) {
			this.motionY *= 0.6D;
		}

		this.wingRotation += this.moveWings * 2.0F;
		if(--this.timeUntilNextEgg <= 0) {
			this.dropItem(Item.egg.shiftedIndex, 1);
			this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
		}

	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
	}

	protected String getLivingSound() {
		return "mob.chicken";
	}

	protected String getHurtSound() {
		return "mob.chickenhurt";
	}

	protected String getDeathSound() {
		return "mob.chickenhurt";
	}

	protected int getDropItemId() {
		return Item.feather.shiftedIndex;
	}
}
