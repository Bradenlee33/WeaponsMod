package weapons.client.renderitems;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import weapons.client.models.ModelInfo;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ItemInfoRender extends Render implements IItemRenderer {

	private ModelInfo testModel;

	public ItemInfoRender() {

		testModel = new ModelInfo();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

		if(type == ItemRenderType.ENTITY){
			return type == ItemRenderType.ENTITY;
		}
		else{
			return type == ItemRenderType.INVENTORY;
		}
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {


		switch (type) {
			case ENTITY: {
				renderBullet(0F, 0F, 0F, 0F, 0F, 0F, 0.50F);
				return;
			}
			case EQUIPPED: {
				renderBullet(3F, 0.0F, 1.25F, 0F, 90F, 0F, 0.16F);
				return;
			}
			case INVENTORY: {
				renderBullet(1.0F, -1F, 0.0F, 0, 10F, 0F, 0.50F);
				return;
			}
			case EQUIPPED_FIRST_PERSON:{
				renderBullet2(135F, -5.5F, 15F, 0, 175F, -45F, 40F);
				return;
			}
			default:
				return;
		}

	}

	private void renderBullet(float x, float y, float z, float rotx, float roty, float rotz, float scale) {

		GL11.glPushMatrix();

		// Scale, Translate, Rotate
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(rotx, 1, 0, 0);
		GL11.glRotatef(roty, 0, 1, 0);
		GL11.glRotatef(rotz, 0, 0, 1);




		// Bind texture
		FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/weapons/textures/models/info.png");

		// Render
		testModel.render();


		GL11.glPopMatrix();
	}
	private void renderBullet2(float x, float y, float z, float rotx, float roty, float rotz, float scale) {

		GL11.glPushMatrix();

		// Scale, Translate, Rotate
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(rotx, 1, 0, 0);
		GL11.glRotatef(roty, 0, 1, 0);
		GL11.glRotatef(rotz, 0, 0, 1);
		GL11.glScalef(scale, scale, scale);




		// Bind texture
		FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/weapons/textures/models/info.png");

		// Render
		testModel.render();


		GL11.glPopMatrix();
	}

	@Override
	public void doRender(Entity entity, double d0, double d1, double d2,
			float f, float f1)
	{
		// TODO Auto-generated method stub

	}
}
