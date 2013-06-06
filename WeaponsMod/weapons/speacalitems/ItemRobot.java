package weapons.speacalitems;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import weapons.ModItem;
import weapons.Weapons;
import weapons.entity.EntityRobot;

public class ItemRobot extends ModItem{

	public ItemRobot(int itemId)
	{
		super(itemId);
		this.setUnlocalizedName("weaponsRobot");
		this.setCreativeTab(Weapons.weaponsTab);
		this.maxStackSize = 1;
		LanguageRegistry.addName(this,"Robot");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player)
	{
		//		robot.setPosition(player.posX, player.posY, player.posZ);
		//		robot.serverPosX = (int)player.posX;
		//		robot.serverPosY = (int)player.posY;
		//		robot.serverPosZ = (int)player.posZ;
		if(!world.isRemote){
			EntityRobot robot = new EntityRobot(world);
			robot.setLocationAndAngles(player.posX, player.posY, player.posZ, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
			robot.rotationYawHead = robot.rotationYaw;
			robot.renderYawOffset = robot.rotationYaw;
			robot.initCreature();
			world.spawnEntityInWorld(robot);
		}
		item.stackSize --;
		return item;
	}

}
