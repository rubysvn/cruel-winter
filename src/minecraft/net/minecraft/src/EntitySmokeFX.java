package net.minecraft.src;

public class EntitySmokeFX extends EntityFX {
	float smokeParticleScale;

	public EntitySmokeFX(World world1, double d2, double d4, double d6) {
		this(world1, d2, d4, d6, 1.0F);
	}

	public EntitySmokeFX(World world1, double d2, double d4, double d6, float f8) {
		super(world1, d2, d4, d6, 0.0D, 0.0D, 0.0D);
		this.motionX *= (double)0.1F;
		this.motionY *= (double)0.1F;
		this.motionZ *= (double)0.1F;
		this.particleRed = this.particleGreen = this.particleBlue = (float)(Math.random() * (double)0.3F);
		this.particleScale *= 0.75F;
		this.particleScale *= f8;
		this.smokeParticleScale = this.particleScale;
		this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
		this.particleMaxAge = (int)((float)this.particleMaxAge * f8);
		this.noClip = false;
	}

	public void renderParticle(Tessellator tessellator, float renderPartialTick, float xOffset, float yOffset, float zOffset, float xOffset2, float zOffset2) {
		float f8 = ((float)this.particleAge + renderPartialTick) / (float)this.particleMaxAge * 32.0F;
		if(f8 < 0.0F) {
			f8 = 0.0F;
		}

		if(f8 > 1.0F) {
			f8 = 1.0F;
		}

		this.particleScale = this.smokeParticleScale * f8;
		super.renderParticle(tessellator, renderPartialTick, xOffset, yOffset, zOffset, xOffset2, zOffset2);
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setEntityDead();
		}

		this.particleTextureIndex = 7 - this.particleAge * 8 / this.particleMaxAge;
		this.motionY += 0.004D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		if(this.posY == this.prevPosY) {
			this.motionX *= 1.1D;
			this.motionZ *= 1.1D;
		}

		this.motionX *= (double)0.96F;
		this.motionY *= (double)0.96F;
		this.motionZ *= (double)0.96F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}
}
