package peaa;

import java.util.logging.Logger;

import net.minecraftforge.common.MinecraftForge;
import peaa.config.PEAAConfig;
import peaa.events.FlightEventHookPEAA;
import peaa.gameObjs.ObjHandlerPEAA;
import peaa.network.PacketHandlerPEAA;
import peaa.proxies.CommonProxy;
import peaa.utils.GuiHandlerPEAA;
import ak.sampleflight.FlightEventHook;
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

	public static Logger logger = Logger.getLogger("PEAA");

	@Instance(MODID)
	public static PEAACore instance;

	@SidedProxy(clientSide = "peaa.proxies.ClientProxy", serverSide = "peaa.proxies.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		PEAAConfig.init(event.getSuggestedConfigurationFile());

		NetworkRegistry.INSTANCE.registerGuiHandler(PEAACore.instance, new GuiHandlerPEAA());

		ObjHandlerPEAA.register();
		ObjHandlerPEAA.addRecipes();

		PacketHandlerPEAA.init();

	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerClientOnlyEvents();
		MinecraftForge.EVENT_BUS.register(new FlightEventHookPEAA());

	}

}
