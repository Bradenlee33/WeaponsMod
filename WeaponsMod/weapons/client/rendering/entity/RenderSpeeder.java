package weapons.client.rendering.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import weapons.client.models.ModelSpeeder;
import weapons.entity.EntitySpeeder;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSpeeder extends Render
{
	protected ModelSpeeder model;

	public RenderSpeeder()
	{
		this.shadowSize = 0.5F;
		this.model = new ModelSpeeder();
	}

	public void renderBoat(EntitySpeeder entity, double par2, double par4, double par6, float par8, float par9)
	{
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glTranslatef((float)par2, (float)par4, (float)par6);
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-(float)entity.renderrotationYaw, 0, 1, 0);
		GL11.glRotatef(-(float)entity.renderrotationPitch, 0, 0, 1);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// Bind texture
		FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/weapons/textures/models/speeder.png");
		this.model.render();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.renderBoat((EntitySpeeder)par1Entity, par2, par4, par6, par8, par9);
	}
}
