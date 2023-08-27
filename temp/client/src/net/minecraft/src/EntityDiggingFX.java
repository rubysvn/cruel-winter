package net.minecraft.src;

public class EntityDiggingFX extends EntityFX {
	public EntityDiggingFX(World worldObj, double x, double y, double z, double motionX, double motionY, double motionZ, Block block) {
		super(worldObj, x, y, z, motionX, motionY, motionZ);
		this.particleTextureIndex = block.blockIndexInTexture;
		this.particleGravity = block.blockParticleGravity;
		this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
		this.particleScale /= 2.0F;
	}

	public int getFXLayer() {
		return 1;
	}

	public void renderParticle(Tessellator tessellator, float renderPartialTick, float xOffset, float yOffset, float zOffset, float xOffset2, float zOffset2) {
		float f8 = ((float)(this.particleTextureIndex % 16) + this.particleTextureJitterX / 4.0F) / 16.0F;
		float f9 = f8 + 0.015609375F;
		float f10 = ((float)(this.particleTextureIndex / 16) + this.particleTextureJitterY / 4.0F) / 16.0F;
		float f11 = f10 + 0.015609375F;
		float f12 = 0.1F * this.particleScale;
		float f13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)renderPartialTick - interpPosX);
		float f14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)renderPartialTick - interpPosY);
		float f15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)renderPartialTick - interpPosZ);
		float f16 = this.getBrightness(renderPartialTick);
		tessellator.setColorOpaque_F(f16 * this.particleRed, f16 * this.particleGreen, f16 * this.particleBlue);
		tessellator.addVertexWithUV((double)(f13 - xOffset * f12 - xOffset2 * f12), (double)(f14 - yOffset * f12), (double)(f15 - zOffset * f12 - zOffset2 * f12), (double)f8, (double)f11);
		tessellator.addVertexWithUV((double)(f13 - xOffset * f12 + xOffset2 * f12), (double)(f14 + yOffset * f12), (double)(f15 - zOffset * f12 + zOffset2 * f12), (double)f8, (double)f10);
		tessellator.addVertexWithUV((double)(f13 + xOffset * f12 + xOffset2 * f12), (double)(f14 + yOffset * f12), (double)(f15 + zOffset * f12 + zOffset2 * f12), (double)f9, (double)f10);
		tessellator.addVertexWithUV((double)(f13 + xOffset * f12 - xOffset2 * f12), (double)(f14 - yOffset * f12), (double)(f15 + zOffset * f12 - zOffset2 * f12), (double)f9, (double)f11);
	}
}
