package peaa.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import peaa.PEAACore;
import peaa.gameObjs.ObjHandlerPEAA;
import peaa.gameObjs.items.RingFlightTeleport;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class RingFlightTeleportSyncPKTHandler implements IMessageHandler<RingFlightTeleportSyncPKT, IMessage>
{
	@Override
	public IMessage onMessage(RingFlightTeleportSyncPKT pkt, MessageContext ctx)
	{
		EntityPlayer player = PEAACore.proxy.getEntityPlayerInstance();
		ItemStack ringFlightTeleport = player.inventory.mainInventory[pkt.getInvSlot()];
		if (ringFlightTeleport != null && ringFlightTeleport.getItem() instanceof RingFlightTeleport)
		{
			((RingFlightTeleport)ringFlightTeleport.getItem()).setCanFlying(ringFlightTeleport, pkt.getCanFlying());
			((RingFlightTeleport)ringFlightTeleport.getItem()).setIsHeld(ringFlightTeleport, pkt.getIsHeld());
			((RingFlightTeleport)ringFlightTeleport.getItem()).setFlyingSpeed(ringFlightTeleport, pkt.getFlyingSpeed());
		}

		return null;
	}

}
