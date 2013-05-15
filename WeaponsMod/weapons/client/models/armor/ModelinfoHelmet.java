package weapons.client.models.armor;

import net.minecraft.client.model.ModelBase;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ModelinfoHelmet extends ModelBase {

    private IModelCustom model;

    public ModelinfoHelmet() {

        model = AdvancedModelLoader.loadModel("/mods/weapons/models/infohelmet.obj");
    }

    public void render() {

        model.renderAll();
    }



   
}
