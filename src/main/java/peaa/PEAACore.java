package peaa;

import peaa.eaemc.EAEMC;
import peaa.gameObjs.ObjHandlerPEAA;
import peaa.proxies.CommonProxy;
import peaa.utils.GuiHandlerPEAA;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = PEAACore.MODID, name =  PEAACore.MODNAME, version =  PEAACore.VERSION, dependencies="required-after:ProjectE;after:BuildCraft;after:ic2")
public class PEAACore
{
	public static final String MODID = "PEAA";
	public static final String MODNAME = "PEAA";
	public static final String VERSION = "@VERSION@";

	@Instance(MODID)
	public static PEAACore instance;

	@SidedProxy(clientSide = "peaa.proxies.ClientProxy", serverSide = "peaa.proxies.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(PEAACore.instance, new GuiHandlerPEAA());

		ObjHandlerPEAA.register();
		ObjHandlerPEAA.addRecipes();

	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// こうすればクリエイティブタブからは簡単に消せる
		//ObjHandler.collectorMK3.setCreativeTab(null);
		proxy.registerClientOnlyEvents();
		EAEMC.extendedRegistrationEMC();

		for (ModContainer mod : Loader.instance().getModList()) {
			System.out.println("mod id : " + mod.getModId());
		}

		/* レシピ書き換えの勉強用に使ったやつ もったいなくて消していないだけなので気にしないでください
		List list = CraftingManager.getInstance().getRecipeList();
		System.out.println("listSize : " + list.size());
		ShapedRecipes source;

		ShapedRecipes recipe = RecipeCheck.createDataRecipe(new ItemStack(ObjHandlerPEAA.collectorMK4), "CCC", "CMC", "CCC", 'C', ObjHandler.collectorMK3, 'M', new ItemStack(ObjHandler.matter, 1, 1));
		ShapedRecipes recipe2 = RecipeCheck.createDataRecipe(new ItemStack(ObjHandlerPEAA.collectorMK4), "CCC", "CMC", "CCC", 'C', ObjHandler.collectorMK2, 'M', new ItemStack(ObjHandler.matter, 1, 1));
		for (int i = 0; i < list.size(); ++i) {
			if (list.get(i) instanceof ShapedRecipes) {
				source = (ShapedRecipes)list.get(i);
				//System.out.println(source.getRecipeOutput());
				if(RecipeCheck.checkMatchRecipe(recipe, source)) {
					System.out.println("Equal !!");
					list.set(i, recipe2);
				}
			}
		}

		System.out.println("aaaaaa");
		*/



	}

}
