package net.minecraft.src;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

public class EntityRenderer {
	private Minecraft mc;
	private float farPlaneDistance = 0.0F;
	public ItemRenderer itemRenderer;
	private int rendererUpdateCount;
	private Entity pointedEntity = null;
	private long prevFrameTime = System.currentTimeMillis();
	private Random random = new Random();
	volatile int unusedInt1 = 0;
	volatile int unusedInt2 = 0;
	FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
	float fogColorRed;
	float fogColorGreen;
	float fogColorBlue;
	private float prevFogColor;
	private float fogColor;

	public EntityRenderer(Minecraft minecraft) {
		this.mc = minecraft;
		this.itemRenderer = new ItemRenderer(minecraft);
	}

	public void updateRenderer() {
		this.prevFogColor = this.fogColor;
		float f1 = this.mc.theWorld.getBrightness(MathHelper.floor_double(this.mc.thePlayer.posX), MathHelper.floor_double(this.mc.thePlayer.posY), MathHelper.floor_double(this.mc.thePlayer.posZ));
		float f2 = (float)(3 - this.mc.options.renderDistance) / 3.0F;
		float f3 = f1 * (1.0F - f2) + f2;
		this.fogColor += (f3 - this.fogColor) * 0.1F;
		++this.rendererUpdateCount;
		this.itemRenderer.updateEquippedItem();
		if(this.mc.isRaining) {
			this.addRainParticles();
		}

	}

	public void getMouseOver(float renderPartialTick) {
		if(this.mc.thePlayer != null) {
			double d2 = (double)this.mc.playerController.getBlockReachDistance();
			this.mc.objectMouseOver = this.mc.thePlayer.rayTrace(d2, renderPartialTick);
			double d4 = d2;
			Vec3D vec3D6 = this.mc.thePlayer.getPosition(renderPartialTick);
			if(this.mc.objectMouseOver != null) {
				d4 = this.mc.objectMouseOver.hitVec.distanceTo(vec3D6);
			}

			if(this.mc.playerController instanceof PlayerControllerCreative) {
				d2 = 32.0D;
				d4 = 32.0D;
			} else {
				if(d4 > 3.0D) {
					d4 = 3.0D;
				}

				d2 = d4;
			}

			Vec3D vec3D7 = this.mc.thePlayer.getLook(renderPartialTick);
			Vec3D vec3D8 = vec3D6.addVector(vec3D7.xCoord * d2, vec3D7.yCoord * d2, vec3D7.zCoord * d2);
			this.pointedEntity = null;
			List list9 = this.mc.theWorld.getEntitiesWithinAABBExcludingEntity(this.mc.thePlayer, this.mc.thePlayer.boundingBox.addCoord(vec3D7.xCoord * d2, vec3D7.yCoord * d2, vec3D7.zCoord * d2));
			double d10 = 0.0D;

			for(int i12 = 0; i12 < list9.size(); ++i12) {
				Entity entity13 = (Entity)list9.get(i12);
				if(entity13.canBeCollidedWith()) {
					float f14 = 0.1F;
					AxisAlignedBB axisAlignedBB15 = entity13.boundingBox.expand((double)f14, (double)f14, (double)f14);
					MovingObjectPosition movingObjectPosition16 = axisAlignedBB15.calculateIntercept(vec3D6, vec3D8);
					if(movingObjectPosition16 != null) {
						double d17 = vec3D6.distanceTo(movingObjectPosition16.hitVec);
						if(d17 < d10 || d10 == 0.0D) {
							this.pointedEntity = entity13;
							d10 = d17;
						}
					}
				}
			}

			if(this.pointedEntity != null && !(this.mc.playerController instanceof PlayerControllerCreative)) {
				this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity);
			}

		}
	}

	private float getFOVModifier(float renderPartialTick) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		float f3 = 70.0F;
		if(entityPlayerSP2.isInsideOfMaterial(Material.water)) {
			f3 = 60.0F;
		}

		if(entityPlayerSP2.health <= 0) {
			float f4 = (float)entityPlayerSP2.deathTime + renderPartialTick;
			f3 /= (1.0F - 500.0F / (f4 + 500.0F)) * 2.0F + 1.0F;
		}

		return f3;
	}

	private void hurtCameraEffect(float renderPartialTick) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		float f3 = (float)entityPlayerSP2.hurtTime - renderPartialTick;
		float f4;
		if(entityPlayerSP2.health <= 0) {
			f4 = (float)entityPlayerSP2.deathTime + renderPartialTick;
			GL11.glRotatef(40.0F - 8000.0F / (f4 + 200.0F), 0.0F, 0.0F, 1.0F);
		}

		if(f3 >= 0.0F) {
			f3 /= (float)entityPlayerSP2.maxHurtTime;
			f3 = MathHelper.sin(f3 * f3 * f3 * f3 * (float)Math.PI);
			f4 = entityPlayerSP2.attackedAtYaw;
			GL11.glRotatef(-f4, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f3 * 14.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(f4, 0.0F, 1.0F, 0.0F);
		}
	}

	private void setupViewBobbing(float renderPartialTick) {
		if(!this.mc.options.thirdPersonView) {
			EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
			float f3 = entityPlayerSP2.distanceWalkedModified - entityPlayerSP2.prevDistanceWalkedModified;
			float f4 = entityPlayerSP2.distanceWalkedModified + f3 * renderPartialTick;
			float f5 = entityPlayerSP2.prevCameraYaw + (entityPlayerSP2.cameraYaw - entityPlayerSP2.prevCameraYaw) * renderPartialTick;
			float f6 = entityPlayerSP2.prevCameraPitch + (entityPlayerSP2.cameraPitch - entityPlayerSP2.prevCameraPitch) * renderPartialTick;
			GL11.glTranslatef(MathHelper.sin(f4 * (float)Math.PI) * f5 * 0.5F, -Math.abs(MathHelper.cos(f4 * (float)Math.PI) * f5), 0.0F);
			GL11.glRotatef(MathHelper.sin(f4 * (float)Math.PI) * f5 * 3.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(Math.abs(MathHelper.cos(f4 * (float)Math.PI + 0.2F) * f5) * 5.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(f6, 1.0F, 0.0F, 0.0F);
		}
	}

	private void orientCamera(float renderPartialTick) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		double d3 = entityPlayerSP2.prevPosX + (entityPlayerSP2.posX - entityPlayerSP2.prevPosX) * (double)renderPartialTick;
		double d5 = entityPlayerSP2.prevPosY + (entityPlayerSP2.posY - entityPlayerSP2.prevPosY) * (double)renderPartialTick;
		double d7 = entityPlayerSP2.prevPosZ + (entityPlayerSP2.posZ - entityPlayerSP2.prevPosZ) * (double)renderPartialTick;
		if(this.mc.options.thirdPersonView) {
			double d9 = 4.0D;
			float f10000 = entityPlayerSP2.prevRenderYawOffset + (entityPlayerSP2.renderYawOffset - entityPlayerSP2.prevRenderYawOffset) * renderPartialTick;
			float f12 = entityPlayerSP2.rotationYaw - 10.0F;
			float f13 = entityPlayerSP2.rotationPitch + 2.0F;
			double d14 = (double)(-MathHelper.sin(f12 / 180.0F * (float)Math.PI) * MathHelper.cos(f13 / 180.0F * (float)Math.PI)) * d9;
			double d16 = (double)(MathHelper.cos(f12 / 180.0F * (float)Math.PI) * MathHelper.cos(f13 / 180.0F * (float)Math.PI)) * d9;
			double d18 = (double)(-MathHelper.sin(f13 / 180.0F * (float)Math.PI)) * d9;

			for(int i20 = 0; i20 < 8; ++i20) {
				float f21 = (float)((i20 & 1) * 2 - 1);
				float f22 = (float)((i20 >> 1 & 1) * 2 - 1);
				float f23 = (float)((i20 >> 2 & 1) * 2 - 1);
				f21 *= 0.1F;
				f22 *= 0.1F;
				f23 *= 0.1F;
				MovingObjectPosition movingObjectPosition24 = this.mc.theWorld.rayTraceBlocks(Vec3D.createVector(d3 + (double)f21, d5 + (double)f22, d7 + (double)f23), Vec3D.createVector(d3 - d14 + (double)f21 + (double)f23, d5 - d18 + (double)f22, d7 - d16 + (double)f23));
				if(movingObjectPosition24 != null) {
					double d25 = movingObjectPosition24.hitVec.distanceTo(Vec3D.createVector(d3, d5, d7));
					if(d25 < d9) {
						d9 = d25;
					}
				}
			}

			GL11.glRotatef(entityPlayerSP2.rotationPitch - f13, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(entityPlayerSP2.rotationYaw - f12, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.0F, 0.0F, (float)(-d9));
			GL11.glRotatef(f12 - entityPlayerSP2.rotationYaw, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(f13 - entityPlayerSP2.rotationPitch, 1.0F, 0.0F, 0.0F);
		} else {
			GL11.glTranslatef(0.0F, 0.0F, -0.1F);
		}

		GL11.glRotatef(entityPlayerSP2.prevRotationPitch + (entityPlayerSP2.rotationPitch - entityPlayerSP2.prevRotationPitch) * renderPartialTick, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(entityPlayerSP2.prevRotationYaw + (entityPlayerSP2.rotationYaw - entityPlayerSP2.prevRotationYaw) * renderPartialTick + 180.0F, 0.0F, 1.0F, 0.0F);
	}

	private void setupCameraTransform(float renderPartialTick, int i2) {
		this.farPlaneDistance = (float)(256 >> this.mc.options.renderDistance);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		float f3 = 0.07F;
		if(this.mc.options.anaglyph) {
			GL11.glTranslatef((float)(-(i2 * 2 - 1)) * f3, 0.0F, 0.0F);
		}

		GLU.gluPerspective(this.getFOVModifier(renderPartialTick), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		if(this.mc.options.anaglyph) {
			GL11.glTranslatef((float)(i2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
		}

		this.hurtCameraEffect(renderPartialTick);
		if(this.mc.options.viewBobbing) {
			this.setupViewBobbing(renderPartialTick);
		}

		this.orientCamera(renderPartialTick);
	}

	private void renderHand(float renderPartialTick, int i2) {
		GL11.glLoadIdentity();
		if(this.mc.options.anaglyph) {
			GL11.glTranslatef((float)(i2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
		}

		GL11.glPushMatrix();
		this.hurtCameraEffect(renderPartialTick);
		if(this.mc.options.viewBobbing) {
			this.setupViewBobbing(renderPartialTick);
		}

		if(!this.mc.options.thirdPersonView) {
			this.itemRenderer.renderItemInFirstPerson(renderPartialTick);
		}

		GL11.glPopMatrix();
		if(!this.mc.options.thirdPersonView) {
			this.itemRenderer.renderOverlays(renderPartialTick);
			this.hurtCameraEffect(renderPartialTick);
		}

		if(this.mc.options.viewBobbing) {
			this.setupViewBobbing(renderPartialTick);
		}

	}

	public void updateCameraAndRender(float renderPartialTick) {
		if(!Display.isActive()) {
			if(System.currentTimeMillis() - this.prevFrameTime > 500L) {
				this.mc.displayInGameMenu();
			}
		} else {
			this.prevFrameTime = System.currentTimeMillis();
		}

		int i3;
		if(this.mc.inGameHasFocus) {
			this.mc.mouseHelper.mouseXYChange();
			int i2 = this.mc.mouseHelper.deltaX;
			i3 = this.mc.mouseHelper.deltaY;
			byte b4 = 1;
			if(this.mc.options.invertMouse) {
				b4 = -1;
			}

			this.mc.thePlayer.setAngles((float)i2, (float)(i3 * b4));
		}

		if(!this.mc.skipRenderWorld) {
			ScaledResolution scaledResolution7 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
			i3 = scaledResolution7.getScaledWidth();
			int i8 = scaledResolution7.getScaledHeight();
			int i5 = Mouse.getX() * i3 / this.mc.displayWidth;
			int i6 = i8 - Mouse.getY() * i8 / this.mc.displayHeight - 1;
			if(this.mc.theWorld != null) {
				this.renderWorld(renderPartialTick);
				this.mc.ingameGUI.renderGameOverlay(renderPartialTick, this.mc.currentScreen != null, i5, i6);
			} else {
				GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
				GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				this.setupOverlayRendering();
			}

			if(this.mc.currentScreen != null) {
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				this.mc.currentScreen.drawScreen(i5, i6, renderPartialTick);
			}

		}
	}

	public void renderWorld(float renderPartialTick) {
		this.getMouseOver(renderPartialTick);
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		RenderGlobal renderGlobal3 = this.mc.renderGlobal;
		EffectRenderer effectRenderer4 = this.mc.effectRenderer;
		double d5 = entityPlayerSP2.lastTickPosX + (entityPlayerSP2.posX - entityPlayerSP2.lastTickPosX) * (double)renderPartialTick;
		double d7 = entityPlayerSP2.lastTickPosY + (entityPlayerSP2.posY - entityPlayerSP2.lastTickPosY) * (double)renderPartialTick;
		double d9 = entityPlayerSP2.lastTickPosZ + (entityPlayerSP2.posZ - entityPlayerSP2.lastTickPosZ) * (double)renderPartialTick;

		for(int i11 = 0; i11 < 2; ++i11) {
			if(this.mc.options.anaglyph) {
				if(i11 == 0) {
					GL11.glColorMask(false, true, true, false);
				} else {
					GL11.glColorMask(true, false, false, false);
				}
			}

			GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
			this.updateFogColor(renderPartialTick);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glEnable(GL11.GL_CULL_FACE);
			this.setupCameraTransform(renderPartialTick, i11);
			ClippingHelperImplementation.getInstance();
			if(this.mc.options.renderDistance < 2) {
				this.setupFog(-1);
				renderGlobal3.renderSky(renderPartialTick);
			}

			GL11.glEnable(GL11.GL_FOG);
			this.setupFog(1);
			Frustum frustum12 = new Frustum();
			frustum12.setPosition(d5, d7, d9);
			this.mc.renderGlobal.clipRenderersByFrustum(frustum12, renderPartialTick);
			this.mc.renderGlobal.updateRenderers(entityPlayerSP2, false);
			this.setupFog(0);
			GL11.glEnable(GL11.GL_FOG);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
			RenderHelper.disableStandardItemLighting();
			renderGlobal3.sortAndRender(entityPlayerSP2, 0, (double)renderPartialTick);
			RenderHelper.enableStandardItemLighting();
			renderGlobal3.renderEntities(entityPlayerSP2.getPosition(renderPartialTick), frustum12, renderPartialTick);
			effectRenderer4.renderLitParticles(entityPlayerSP2, renderPartialTick);
			RenderHelper.disableStandardItemLighting();
			this.setupFog(0);
			effectRenderer4.renderParticles(entityPlayerSP2, renderPartialTick);
			if(this.mc.objectMouseOver != null && entityPlayerSP2.isInsideOfMaterial(Material.water)) {
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				renderGlobal3.drawBlockBreaking(entityPlayerSP2, this.mc.objectMouseOver, 0, entityPlayerSP2.inventory.getCurrentItem(), renderPartialTick);
				renderGlobal3.drawSelectionBox(entityPlayerSP2, this.mc.objectMouseOver, 0, entityPlayerSP2.inventory.getCurrentItem(), renderPartialTick);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
			}

			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			this.setupFog(0);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
			if(this.mc.options.fancyGraphics) {
				GL11.glColorMask(false, false, false, false);
				int i13 = renderGlobal3.sortAndRender(entityPlayerSP2, 1, (double)renderPartialTick);
				GL11.glColorMask(true, true, true, true);
				if(this.mc.options.anaglyph) {
					if(i11 == 0) {
						GL11.glColorMask(false, true, true, false);
					} else {
						GL11.glColorMask(true, false, false, false);
					}
				}

				if(i13 > 0) {
					renderGlobal3.renderAllRenderLists(1, (double)renderPartialTick);
				}
			} else {
				renderGlobal3.sortAndRender(entityPlayerSP2, 1, (double)renderPartialTick);
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			if(this.mc.objectMouseOver != null && !entityPlayerSP2.isInsideOfMaterial(Material.water)) {
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				renderGlobal3.drawBlockBreaking(entityPlayerSP2, this.mc.objectMouseOver, 0, entityPlayerSP2.inventory.getCurrentItem(), renderPartialTick);
				renderGlobal3.drawSelectionBox(entityPlayerSP2, this.mc.objectMouseOver, 0, entityPlayerSP2.inventory.getCurrentItem(), renderPartialTick);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
			}

			GL11.glDisable(GL11.GL_FOG);
			if(this.mc.theWorld.snowCovered) {
				this.renderSnow(renderPartialTick);
			}

			if(this.mc.isRaining) {
				this.i(renderPartialTick);
			}

			if(this.pointedEntity != null) {
				;
			}

			this.setupFog(0);
			GL11.glEnable(GL11.GL_FOG);
			renderGlobal3.renderClouds(renderPartialTick);
			GL11.glDisable(GL11.GL_FOG);
			this.setupFog(1);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			this.renderHand(renderPartialTick, i11);
			if(!this.mc.options.anaglyph) {
				return;
			}
		}

		GL11.glColorMask(true, true, true, false);
	}

	private void addRainParticles() {
		if(this.mc.options.fancyGraphics) {
			EntityPlayerSP entityPlayerSP1 = this.mc.thePlayer;
			World world2 = this.mc.theWorld;
			int i3 = MathHelper.floor_double(entityPlayerSP1.posX);
			int i4 = MathHelper.floor_double(entityPlayerSP1.posY);
			int i5 = MathHelper.floor_double(entityPlayerSP1.posZ);
			byte b6 = 16;

			for(int i7 = 0; i7 < 150; ++i7) {
				int i8 = i3 + this.random.nextInt(b6) - this.random.nextInt(b6);
				int i9 = i5 + this.random.nextInt(b6) - this.random.nextInt(b6);
				int i10 = world2.getPrecipitationHeight(i8, i9);
				int i11 = world2.getBlockId(i8, i10 - 1, i9);
				if(i10 <= i4 + b6 && i10 >= i4 - b6) {
					float f12 = this.random.nextFloat();
					float f13 = this.random.nextFloat();
					if(i11 > 0) {
						this.mc.effectRenderer.addEffect(new EntityRainFX(world2, (double)((float)i8 + f12), (double)((float)i10 + 0.1F) - Block.blocksList[i11].minY, (double)((float)i9 + f13)));
					}
				}
			}

		}
	}

	private void renderSnow(float renderPartialTick) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		World world3 = this.mc.theWorld;
		int i4 = MathHelper.floor_double(entityPlayerSP2.posX);
		int i5 = MathHelper.floor_double(entityPlayerSP2.posY);
		int i6 = MathHelper.floor_double(entityPlayerSP2.posZ);
		Tessellator tessellator7 = Tessellator.instance;
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/snow.png"));
		double d8 = entityPlayerSP2.lastTickPosX + (entityPlayerSP2.posX - entityPlayerSP2.lastTickPosX) * (double)renderPartialTick;
		double d10 = entityPlayerSP2.lastTickPosY + (entityPlayerSP2.posY - entityPlayerSP2.lastTickPosY) * (double)renderPartialTick;
		double d12 = entityPlayerSP2.lastTickPosZ + (entityPlayerSP2.posZ - entityPlayerSP2.lastTickPosZ) * (double)renderPartialTick;
		byte b14 = 5;
		if(this.mc.options.fancyGraphics) {
			b14 = 10;
		}

		for(int i15 = i4 - b14; i15 <= i4 + b14; ++i15) {
			for(int i16 = i6 - b14; i16 <= i6 + b14; ++i16) {
				int i17 = world3.getTopSolidOrLiquidBlock(i15, i16);
				if(i17 < 0) {
					i17 = 0;
				}

				int i18 = i5 - b14;
				int i19 = i5 + b14;
				if(i18 < i17) {
					i18 = i17;
				}

				if(i19 < i17) {
					i19 = i17;
				}

				float f20 = 2.0F;
				if(i18 != i19) {
					this.random.setSeed((long)(i15 * i15 * 3121 + i15 * 45238971 + i16 * i16 * 418711 + i16 * 13761));
					float f21 = (float)this.rendererUpdateCount + renderPartialTick;
					float f22 = ((float)(this.rendererUpdateCount & 511) + renderPartialTick) / 512.0F;
					float f23 = this.random.nextFloat() + f21 * 0.01F * (float)this.random.nextGaussian();
					float f24 = this.random.nextFloat() + f21 * (float)this.random.nextGaussian() * 0.001F;
					double d25 = (double)((float)i15 + 0.5F) - entityPlayerSP2.posX;
					double d27 = (double)((float)i16 + 0.5F) - entityPlayerSP2.posZ;
					float f29 = MathHelper.sqrt_double(d25 * d25 + d27 * d27) / (float)b14;
					tessellator7.startDrawingQuads();
					float f30 = world3.getBrightness(i15, 128, i16);
					GL11.glColor4f(f30, f30, f30, (1.0F - f29 * f29) * 0.7F);
					tessellator7.setTranslationD(-d8 * 1.0D, -d10 * 1.0D, -d12 * 1.0D);
					tessellator7.addVertexWithUV((double)(i15 + 0), (double)i18, (double)(i16 + 0), (double)(0.0F * f20 + f23), (double)((float)i18 * f20 / 8.0F + f22 * f20 + f24));
					tessellator7.addVertexWithUV((double)(i15 + 1), (double)i18, (double)(i16 + 1), (double)(1.0F * f20 + f23), (double)((float)i18 * f20 / 8.0F + f22 * f20 + f24));
					tessellator7.addVertexWithUV((double)(i15 + 1), (double)i19, (double)(i16 + 1), (double)(1.0F * f20 + f23), (double)((float)i19 * f20 / 8.0F + f22 * f20 + f24));
					tessellator7.addVertexWithUV((double)(i15 + 0), (double)i19, (double)(i16 + 0), (double)(0.0F * f20 + f23), (double)((float)i19 * f20 / 8.0F + f22 * f20 + f24));
					tessellator7.addVertexWithUV((double)(i15 + 0), (double)i18, (double)(i16 + 1), (double)(0.0F * f20 + f23), (double)((float)i18 * f20 / 8.0F + f22 * f20 + f24));
					tessellator7.addVertexWithUV((double)(i15 + 1), (double)i18, (double)(i16 + 0), (double)(1.0F * f20 + f23), (double)((float)i18 * f20 / 8.0F + f22 * f20 + f24));
					tessellator7.addVertexWithUV((double)(i15 + 1), (double)i19, (double)(i16 + 0), (double)(1.0F * f20 + f23), (double)((float)i19 * f20 / 8.0F + f22 * f20 + f24));
					tessellator7.addVertexWithUV((double)(i15 + 0), (double)i19, (double)(i16 + 1), (double)(0.0F * f20 + f23), (double)((float)i19 * f20 / 8.0F + f22 * f20 + f24));
					tessellator7.setTranslationD(0.0D, 0.0D, 0.0D);
					tessellator7.draw();
				}
			}
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void i(float f1) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		World world3 = this.mc.theWorld;
		int i4 = MathHelper.floor_double(entityPlayerSP2.posX);
		int i5 = MathHelper.floor_double(entityPlayerSP2.posY);
		int i6 = MathHelper.floor_double(entityPlayerSP2.posZ);
		Tessellator tessellator7 = Tessellator.instance;
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/rain.png"));
		double d8 = entityPlayerSP2.lastTickPosX + (entityPlayerSP2.posX - entityPlayerSP2.lastTickPosX) * (double)f1;
		double d10 = entityPlayerSP2.lastTickPosY + (entityPlayerSP2.posY - entityPlayerSP2.lastTickPosY) * (double)f1;
		double d12 = entityPlayerSP2.lastTickPosZ + (entityPlayerSP2.posZ - entityPlayerSP2.lastTickPosZ) * (double)f1;
		byte b14 = 5;
		if(this.mc.options.fancyGraphics) {
			b14 = 10;
		}

		for(int i15 = i4 - b14; i15 <= i4 + b14; ++i15) {
			for(int i16 = i6 - b14; i16 <= i6 + b14; ++i16) {
				int i17 = world3.getPrecipitationHeight(i15, i16);
				int i18 = i5 - b14;
				int i19 = i5 + b14;
				if(i18 < i17) {
					i18 = i17;
				}

				if(i19 < i17) {
					i19 = i17;
				}

				float f20 = 2.0F;
				if(i18 != i19) {
					float f21 = ((float)(this.rendererUpdateCount + i15 * i15 * 3121 + i15 * 45238971 + i16 * i16 * 418711 + i16 * 13761 & 31) + f1) / 32.0F;
					double d22 = (double)((float)i15 + 0.5F) - entityPlayerSP2.posX;
					double d24 = (double)((float)i16 + 0.5F) - entityPlayerSP2.posZ;
					float f26 = MathHelper.sqrt_double(d22 * d22 + d24 * d24) / (float)b14;
					tessellator7.startDrawingQuads();
					float f27 = world3.getBrightness(i15, 128, i16);
					GL11.glColor4f(f27, f27, f27, (1.0F - f26 * f26) * 0.7F);
					tessellator7.setTranslationD(-d8 * 1.0D, -d10 * 1.0D, -d12 * 1.0D);
					tessellator7.addVertexWithUV((double)(i15 + 0), (double)i18, (double)(i16 + 0), (double)(0.0F * f20), (double)((float)i18 * f20 / 8.0F + f21 * f20));
					tessellator7.addVertexWithUV((double)(i15 + 1), (double)i18, (double)(i16 + 1), (double)(1.0F * f20), (double)((float)i18 * f20 / 8.0F + f21 * f20));
					tessellator7.addVertexWithUV((double)(i15 + 1), (double)i19, (double)(i16 + 1), (double)(1.0F * f20), (double)((float)i19 * f20 / 8.0F + f21 * f20));
					tessellator7.addVertexWithUV((double)(i15 + 0), (double)i19, (double)(i16 + 0), (double)(0.0F * f20), (double)((float)i19 * f20 / 8.0F + f21 * f20));
					tessellator7.addVertexWithUV((double)(i15 + 0), (double)i18, (double)(i16 + 1), (double)(0.0F * f20), (double)((float)i18 * f20 / 8.0F + f21 * f20));
					tessellator7.addVertexWithUV((double)(i15 + 1), (double)i18, (double)(i16 + 0), (double)(1.0F * f20), (double)((float)i18 * f20 / 8.0F + f21 * f20));
					tessellator7.addVertexWithUV((double)(i15 + 1), (double)i19, (double)(i16 + 0), (double)(1.0F * f20), (double)((float)i19 * f20 / 8.0F + f21 * f20));
					tessellator7.addVertexWithUV((double)(i15 + 0), (double)i19, (double)(i16 + 1), (double)(0.0F * f20), (double)((float)i19 * f20 / 8.0F + f21 * f20));
					tessellator7.setTranslationD(0.0D, 0.0D, 0.0D);
					tessellator7.draw();
				}
			}
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void setupOverlayRendering() {
		ScaledResolution scaledResolution1 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
		int i2 = scaledResolution1.getScaledWidth();
		int i3 = scaledResolution1.getScaledHeight();
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, (double)i2, (double)i3, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	}

	private void updateFogColor(float renderPartialTick) {
		World world2 = this.mc.theWorld;
		EntityPlayerSP entityPlayerSP3 = this.mc.thePlayer;
		float f4 = 1.0F / (float)(4 - this.mc.options.renderDistance);
		f4 = 1.0F - (float)Math.pow((double)f4, 0.25D);
		Vec3D vec3D5 = world2.getSkyColor(renderPartialTick);
		float f6 = (float)vec3D5.xCoord;
		float f7 = (float)vec3D5.yCoord;
		float f8 = (float)vec3D5.zCoord;
		Vec3D vec3D9 = world2.getFogColor(renderPartialTick);
		this.fogColorRed = (float)vec3D9.xCoord;
		this.fogColorGreen = (float)vec3D9.yCoord;
		this.fogColorBlue = (float)vec3D9.zCoord;
		this.fogColorRed += (f6 - this.fogColorRed) * f4;
		this.fogColorGreen += (f7 - this.fogColorGreen) * f4;
		this.fogColorBlue += (f8 - this.fogColorBlue) * f4;
		if(entityPlayerSP3.isInsideOfMaterial(Material.water)) {
			this.fogColorRed = 0.02F;
			this.fogColorGreen = 0.02F;
			this.fogColorBlue = 0.2F;
		} else if(entityPlayerSP3.isInsideOfMaterial(Material.lava)) {
			this.fogColorRed = 0.6F;
			this.fogColorGreen = 0.1F;
			this.fogColorBlue = 0.0F;
		}

		float f10 = this.prevFogColor + (this.fogColor - this.prevFogColor) * renderPartialTick;
		this.fogColorRed *= f10;
		this.fogColorGreen *= f10;
		this.fogColorBlue *= f10;
		if(this.mc.options.anaglyph) {
			float f11 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
			float f12 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
			float f13 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
			this.fogColorRed = f11;
			this.fogColorGreen = f12;
			this.fogColorBlue = f13;
		}

		GL11.glClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
	}

	private void setupFog(int i1) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
		GL11.glNormal3f(0.0F, -1.0F, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f3;
		float f4;
		float f5;
		float f6;
		float f7;
		float f8;
		if(entityPlayerSP2.isInsideOfMaterial(Material.water)) {
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F);
			f3 = 0.4F;
			f4 = 0.4F;
			f5 = 0.9F;
			if(this.mc.options.anaglyph) {
				f6 = (f3 * 30.0F + f4 * 59.0F + f5 * 11.0F) / 100.0F;
				f7 = (f3 * 30.0F + f4 * 70.0F) / 100.0F;
				f8 = (f3 * 30.0F + f5 * 70.0F) / 100.0F;
			}
		} else if(entityPlayerSP2.isInsideOfMaterial(Material.lava)) {
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 2.0F);
			f3 = 0.4F;
			f4 = 0.3F;
			f5 = 0.3F;
			if(this.mc.options.anaglyph) {
				f6 = (f3 * 30.0F + f4 * 59.0F + f5 * 11.0F) / 100.0F;
				f7 = (f3 * 30.0F + f4 * 70.0F) / 100.0F;
				f8 = (f3 * 30.0F + f5 * 70.0F) / 100.0F;
			}
		} else {
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
			GL11.glFogf(GL11.GL_FOG_START, this.farPlaneDistance * 0.25F);
			GL11.glFogf(GL11.GL_FOG_END, this.farPlaneDistance);
			if(i1 < 0) {
				GL11.glFogf(GL11.GL_FOG_START, 0.0F);
				GL11.glFogf(GL11.GL_FOG_END, this.farPlaneDistance * 0.8F);
			}

			if(GLContext.getCapabilities().GL_NV_fog_distance) {
				GL11.glFogi(34138, 34139);
			}
		}

		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT);
	}

	private FloatBuffer setFogColorBuffer(float r, float g, float b, float a) {
		this.fogColorBuffer.clear();
		this.fogColorBuffer.put(r).put(g).put(b).put(a);
		this.fogColorBuffer.flip();
		return this.fogColorBuffer;
	}
}
