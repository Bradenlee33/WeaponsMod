package weapons.armor;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import weapons.client.models.armor.ModelJetBoots;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemJetBoots extends ItemArmor
implements
ISpecialArmor {

	int tid = 0;
	int cooldown = 0;
	/**
	 * @param id
	 * @param renderIndex
	 * @param armorType   0 = head; 1 = torso; 2 = legs; 3 = feet
	 */
	public ItemJetBoots(int id, int renderIndex, int armorType) {
		super(id, EnumArmorMaterial.IRON, renderIndex, armorType);
		setMaxStackSize(1);
		tid = id;
	}

	@Override
	public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, int layer) {

		return "/mods/weapons/textures/models/jetboots.png";

	}



	@Override
	@SideOnly(Side.CLIENT)
	public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack itemStack)
	{
		player.fallDistance = 0;
		if(!world.isRemote){
			if(player.isJumping && this.cooldown < 4 && player.motionY <= 0){
				updatejump(player);
				this.cooldown += 1;
			}
			else if(player.onGround){
				this.cooldown = 0;
			}
		}
		if(world.isRemote){
			if(player.isJumping && this.cooldown < 4 && player.motionY <= 0){
				updatejump(player);
				this.cooldown += 1;
				for(int i = 0; i < 2000; i++){
					float offX = 0 + (float)(Math.random() * ((6 - 0) + 1));
					float offY = 0 + (float)(Math.random() * ((4 - 0) + 1));
					float offZ = 0 + (float)(Math.random() * ((6 - 0) + 1)); 
					world.spawnParticle("reddust", player.posX - 3 + offX,  player.posY - 2 + offY,  player.posZ - 3 + offZ, 1, 0.8, 0.392156863);
				}
				for(int i = 0; i < 4000; i++){
					float offX = 0 + (float)(Math.random() * ((0.02 - 0) + 1));
					float offY = 0 + (float)(Math.random() * ((0.2 - 0) + 1));
					float offZ = 0 + (float)(Math.random() * ((0.02 - 0) + 1)); 
					if(i < 1000){
						world.spawnParticle("flame", player.posX,  player.posY -1,  player.posZ, offX, -offY, offZ);
					}
					else if(i < 2000){
						world.spawnParticle("flame", player.posX,  player.posY -1,  player.posZ, -offX, -offY, -offZ);
					}
					else if(i < 3000){
						world.spawnParticle("flame", player.posX,  player.posY -1,  player.posZ, offX, -offY, -offZ);
					}
					else{
						world.spawnParticle("flame", player.posX,  player.posY -1,  player.posZ, -offX, -offY, offZ);
					}
				}
				world.spawnParticle("largeexplode", player.posX,  player.posY - 2,  player.posZ, -10, 0, 0);
			}
			else if(player.onGround){
				this.cooldown = 0;
			}
		}

	}
	public void updatejump(EntityPlayer player){
		player.motionY += 3;
		player.fallDistance = 0;
		double volocityX = Math.cos(Math.toRadians(player.rotationPitch)) * Math.sin(Math.toRadians(player.rotationYawHead) + Math.PI);
		double volocityZ = Math.cos(Math.toRadians(player.rotationPitch)) * Math.cos(Math.toRadians(player.rotationYawHead));
		player.motionX = volocityX;
		player.motionZ = volocityZ;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLiving entityLiving, ItemStack itemstack, int armorSlot) {
		ModelJetBoots model;
		model = ModelJetBoots.getInstance();
		model.bipedBody.showModel = armorSlot == 1;
		model.bipedRightArm.showModel = armorSlot == 1;
		model.bipedLeftArm.showModel = armorSlot == 1;
		return model;
	}


	/**
	 * Inherited from ISpecialArmor, allows significant customization of damage
	 * calculations.
	 */
	@Override
	public ISpecialArmor.ArmorProperties getProperties(EntityLiving player, ItemStack armor, DamageSource source, double damage, int slot) {
		// Order in which this armor is assessed for damage. Higher(?) priority
		// items take damage first, and if none spills over, the other items
		// take no damage.
		int priority = 1;
		double armorDouble;

		if (player instanceof EntityPlayer) {
			armorDouble = getArmorDouble((EntityPlayer) player, armor);
		} else {
			armorDouble = 2;
		}

		// How much of incoming damage is absorbed by this armor piece.
		// 1.0 = absorbs all damage
		// 0.5 = 50% damage to item, 50% damage carried over
		double absorbRatio = 0.04 * armorDouble;

		// Maximum damage absorbed by this piece. Actual damage to this item
		// will be clamped between (damage * absorbRatio) and (absorbMax). Note
		// that a player has 20 hp (1hp = 1 half-heart)
		int absorbMax = (int) armorDouble * 75; // Not sure why this is
		// necessary but oh well
		if (source.isUnblockable()) {
			absorbMax = 0;
			absorbRatio = 0;
		}
		return new ArmorProperties(priority, absorbRatio, absorbMax);
	}

	public static double clampDouble(double value, double min, double max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return false;
	}


	/**
	 * Inherited from ISpecialArmor, allows us to customize the calculations for
	 * how much armor will display on the player's HUD.
	 */
	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return (int) getArmorDouble(player, armor);
	}

	public double getArmorDouble(EntityPlayer player, ItemStack stack) {
		double totalArmor = 5;
		return totalArmor;
	}

	/**
	 * Inherited from ISpecialArmor, allows us to customize how the armor
	 * handles being damaged.
	 */
	@Override
	public void damageArmor(EntityLiving entity, ItemStack stack, DamageSource source, int damage, int slot) {
	}

	/**
	 * Adds information to the item's tooltip when 'getting' it.
	 *
	 * @param stack            The itemstack to get the tooltip for
	 * @param player           The player (client) viewing the tooltip
	 * @param currentTipList   A list of strings containing the existing tooltip. When
	 *                         passed, it will just contain the name of the item;
	 *                         enchantments and lore are
	 *                         appended afterwards.
	 * @param advancedToolTips Whether or not the player has 'advanced tooltips' turned on in
	 *                         their settings.
	 */
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, @SuppressWarnings("rawtypes") List currentTipList, boolean advancedToolTips) {

	}


}
