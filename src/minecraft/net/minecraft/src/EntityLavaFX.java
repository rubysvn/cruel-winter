package net.minecraft.src;

public class EntityLavaFX extends EntityFX {
	private float lavaParticleScale;

	public EntityLavaFX(World worldObj, double x, double y, double z) {
		super(worldObj, x, y, z, 0.0D, 0.0D, 0.0D);
		this.motionX *= (double)0.8F;
		this.motionY *= (double)0.8F;
		this.motionZ *= (double)0.8F;
		this.motionY = (double)(this.rand.nextFloat() * 0.4F + 0.05F);
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.particleScale *= this.rand.nextFloat() * 2.0F + 0.2F;
		this.lavaParticleScale = this.particleScale;
		this.particleMaxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
		this.noClip = false;
		this.particleTextureIndex = 49;
	}

	public float getBrightness(float f1) {
		return 1.0F;
	}

	public void renderParticle(Tessellator tessellator, float renderPartialTick, float xOffset, float yOffset, float zOffset, float xOffset2, float zOffset2) {
		float f8 = ((float)this.particleAge + renderPartialTick) / (float)this.particleMaxAge;
		this.particleScale = this.lavaParticleScale * (1.0F - f8 * f8);
		super.renderParticle(tessellator, renderPartialTick, xOffset, yOffset, zOffset, xOffset2, zOffset2);
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setEntityDead();
		}

		float f1 = (float)this.particleAge / (float)this.particleMaxAge;
		if(this.rand.nextFloat() > f1) {
			this.worldObj.spawnParticle("smoke", this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
		}

		this.motionY -= 0.03D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.999F;
		this.motionY *= (double)0.999F;
		this.motionZ *= (double)0.999F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}
}
