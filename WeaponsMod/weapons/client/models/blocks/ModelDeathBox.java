package weapons.client.models.blocks;

import net.minecraft.client.model.ModelBase;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import weapons.tileentity.TileEntityDeath;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDeathBox extends ModelBase {

    private IModelCustom model;

    public ModelDeathBox() {

        model = AdvancedModelLoader.loadModel("/mods/weapons/models/deathbox.obj");
    }

    public void render() {

        model.renderAll();
    }

    public void render(TileEntityDeath tile, double x, double y, double z) {

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
    	GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslated(x, y, z);
        GL11.glTranslatef(0.5F, 0, 0.5F);
        // Bind texture
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/weapons/textures/models/deathbox.png");

        // Render
        this.render();

        GL11.glPopMatrix();
    }

}
