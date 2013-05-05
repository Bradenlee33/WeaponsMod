package weapons;



import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import weapons.armor.ItemJetPack;
import weapons.bullets.EntityBullet;
import weapons.bullets.EntityRocket;
import weapons.bullets.ItemBullet;
import weapons.gunitems.FT;
import weapons.gunitems.IceBallLauncher;
import weapons.gunitems.Pistol;
import weapons.gunitems.RocketLancher;
import weapons.gunitems.ScarH;
import weapons.speacalitems.ItemInfo;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "weapons", name = "weapons mod", version = "pre0.1A")
@NetworkMod(channels = { "weapons" }, clientSideRequired = true, serverSideRequired = false)//, packetHandler = OrePacketHandler.class)

public class Weapons
{
	@SidedProxy(clientSide = "weapons.client.ClientProxy", serverSide = "weapons.CommonOreProxy")
	public static CommonOreProxy proxy;
	@Instance("Weapons")
	public static Weapons instance;
	public static int dimension = 2;
	public static int dimensionJCE = 3;
	public static WeaponsTab weaponsTab = new WeaponsTab("weaponstab");
	public static boolean inMCP = false;
	public static boolean explosions;


	public static Map<String, Integer> fTFuel = new HashMap<String, Integer>();
	public static Map<String, Integer> iceBalls = new HashMap<String, Integer>();

	public static Item pisol1;
	public static Item mGun1;
	public static Item rocketLancher1;
	public static Item flameThrower;
	public static Item iceBallLauncher;
	
	public static Item info;

	public static Item bullet1;
	public static Item rocket1;
	
	public static Item jetPack;


	public static String errorString = "";
	public static int errorStringTimer = 0;




	static int startEntityId = 300;
	public static int yourblockModelId;
	public static int yourblockModelId2;
	public static int yourblockModelId3;
	public static int yourblockModelId4;
	public static int startBlockID;
	public static int bulletid;


	@PreInit
	public void initConfig(FMLPreInitializationEvent fpe)
	{

		Configuration config = new Configuration(fpe.getSuggestedConfigurationFile());

		config.load();

		int firstBlockId = config.getBlock("StartBlockId", 3700).getInt();
		startBlockID = firstBlockId;

		int randomItemID = config.getItem("StartItemId", 15000).getInt();
		bulletid = randomItemID;

		int entityid = config.get("EntityId", "EntityId", 400).getInt();
		startEntityId = entityid;


		// Since this flag is a boolean, we can read it into the variable directly from the config.
		explosions = config.get(Configuration.CATEGORY_GENERAL, "Explosions", false).getBoolean(false);

		//Notice there is nothing that gets the value of this property so the expression results in a Property object.
		Property someProperty = config.get(Configuration.CATEGORY_GENERAL, "SomeConfigString", "nothing");

		// Here we add a comment to our new property.
		someProperty.comment = "This value can be read as a string!";

		// this could also be:
		// int someInt = someProperty.getInt();
		// boolean someBoolean = someProperty.getBoolean(true);

		config.save();
		proxy.serverInit();
	}








	@SuppressWarnings("static-access")
	@Init

	public void load(FMLInitializationEvent fie)
	{

		this.addAchievementLocalizations();



		int gunid = bulletid - 256;
		int specialid = bulletid + 256;

		pisol1 = (new Pistol(gunid).setUnlocalizedName("1"));
		mGun1 = (new ScarH(gunid + 50).setUnlocalizedName("5"));
		rocketLancher1 = (new RocketLancher(gunid + 100).setUnlocalizedName("2"));
		flameThrower = (new FT(gunid + 125).setUnlocalizedName("6"));
		info = (new ItemInfo(specialid).setUnlocalizedName("7"));
		iceBallLauncher = (new IceBallLauncher(gunid + 80).setUnlocalizedName("8"));

		bullet1 = (new ItemBullet(bulletid).setUnlocalizedName("3"));

		rocket1 = (new ItemBullet(bulletid + 100).setUnlocalizedName("4"));
		jetPack = (new ItemJetPack(specialid + 40, 0, 1).setUnlocalizedName("9"));

		pisol1.setCreativeTab(weaponsTab);
		mGun1.setCreativeTab(weaponsTab);
		iceBallLauncher.setCreativeTab(weaponsTab);
		rocketLancher1.setCreativeTab(weaponsTab);
		flameThrower.setCreativeTab(weaponsTab);
		bullet1.setCreativeTab(weaponsTab);
		rocket1.setCreativeTab(weaponsTab);
		info.setCreativeTab(weaponsTab);
		jetPack.setCreativeTab(weaponsTab);

		registeringBlocks();
		itemNames();
		recipes();
		smelting();
		blockNames();
		biomes();
		otherNames();
		addAchievementLocalizations();




		//Minecraft Forge Preload Textures

		EntityRegistry.registerModEntity(EntityBullet.class, "bullet", this.getUniqueEntityId(), this, 80, 3, true);
		LanguageRegistry.instance().addStringLocalization("entity.MoreOres.bullet.name", "bullet");
		EntityRegistry.registerModEntity(EntityRocket.class, "rocket", this.getUniqueEntityId(), this, 80, 3, true);
		LanguageRegistry.instance().addStringLocalization("entity.MoreOres.rocket.name", "rocket");


		proxy.load();
		proxy.loadSound();


	}
	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		//		GameRegistry.registerCraftingHandler(craftHandler);	



	}





	public static int getUniqueEntityId() 
	{
		do 
		{
			startEntityId++;
		} 
		while (EntityList.getStringFromID(startEntityId) != null);

		return startEntityId;
	}

	@SuppressWarnings("unchecked")
	public static void registerEntityEgg(Class<? extends Entity> entity, int primaryColor, int secondaryColor) 
	{
		int id = getUniqueEntityId();
		EntityList.IDtoClassMapping.put(id, entity);
		EntityList.entityEggs.put(id, new EntityEggInfo(id, primaryColor, secondaryColor));
	}






	//	public static CraftingHandler craftHandler = new CraftingHandler();

	public static boolean bombsEnabled = true;

	public static boolean bulletsEnabled = false;

	public static boolean vehiclesNeedFuel = true;

	public static boolean canBreakGlass = true;




	private void addAchievementLocalizations()
	{

	}




	public void registeringBlocks()
	{


	}

	public void blockNames()
	{

	}

	public void itemNames()
	{
		LanguageRegistry.addName(pisol1, "RPD Police Beretta");
		LanguageRegistry.addName(bullet1, "Ammo");
		LanguageRegistry.addName(rocketLancher1, "RPG");
		LanguageRegistry.addName(flameThrower, "Flame Thrower");
		LanguageRegistry.addName(rocket1, "RPG Ammo");
		LanguageRegistry.addName(mGun1, "Scar H");
		LanguageRegistry.addName(info, "foo");
		LanguageRegistry.addName(iceBallLauncher,"Ice Ball Launcher");
		LanguageRegistry.addName(jetPack,"JetPack");
	}
	public EnumChatFormatting Color(String Color){
		return EnumChatFormatting.valueOf(Color);
	}

	public void otherNames(){
		LanguageRegistry.instance().addStringLocalization("itemGroup.weaponstab", "en_US", "Weapons Mod");
	}

	public void recipes()
	{


		GameRegistry.addRecipe(new ItemStack(Weapons.bullet1, 32), 
				" x ", 
				"xwx", 
				"yzy", 
				'w', Item.gunpowder,
				'x', Item.ingotIron,
				'y',Item.blazePowder,
				'z', Item.flint);
		GameRegistry.addRecipe(new ItemStack(Weapons.rocket1, 3), 
				" a ", 
				"xwx", 
				"yzy", 
				'w', Block.tnt,
				'x', Item.ingotIron,
				'y',Item.blazePowder,
				'z', Item.fireballCharge,
				'a', Block.blockIron);

	}

	public void smelting()
	{
		//		GameRegistry.addSmelting(MoreOres.InfernalOre.blockID, new ItemStack(MoreOres.InfernalIngot), 2.0f);
	}
	public void biomes()
	{
	}
	public static void logQuietly(String s)
	{
	}

	public static void logLoudly(String s)
	{
		errorString = s;
		errorStringTimer = 100;
		System.out.println("SERIOUS PROBLEM!");
		log(s);
	}

	/** Logger. */
	public static void log(Object arg0)
	{
		System.out.println("WeaponsMod : " + arg0);
	}
}