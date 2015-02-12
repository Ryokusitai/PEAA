package peaa;

import peaa.gameObjs.ObjHandlerPEAA;
import peaa.proxies.CommonProxy;
import peaa.utils.GuiHandlerPEAA;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = PEAACore.MODID, name =  PEAACore.MODNAME, version =  PEAACore.VERSION, dependencies="required-after:ProjectE;")
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
		proxy.registerClientOnlyEvents();
		MinecraftForge.EVENT_BUS.register(ObjHandlerPEAA.ringSpaceTeleport);

	}

}
