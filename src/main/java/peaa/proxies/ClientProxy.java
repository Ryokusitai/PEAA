package peaa.proxies;

import peaa.events.ToolTipEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerClientOnlyEvents()
	{
		MinecraftForge.EVENT_BUS.register(new ToolTipEvent());
	}
}
