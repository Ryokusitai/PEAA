package peaa.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import peaa.PEAACore;
import peaa.gameObjs.items.RingFlightTeleport;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class IsFlyingModeSyncPKTHandler implements IMessageHandler<IsFlyingModeSyncPKT, IMessage>
{
	@Override
	public IMessage onMessage(IsFlyingModeSyncPKT pkt, MessageContext ctx)
	{
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		ItemStack ringFlightTeleport = player.inventory.mainInventory[pkt.getInvSlot()];
		if (ringFlightTeleport != null && ringFlightTeleport.getItem() instanceof RingFlightTeleport)
		{
			//((RingFlightTeleport)ringFlightTeleport.getItem()).setIsFlyingMode(ringFlightTeleport, pkt.getIsFlying());
			ringFlightTeleport.stackTagCompound.setBoolean("isFlyingMode", pkt.getIsFlying());
			System.out.println("IsFlyingHandler : " + ((RingFlightTeleport)ringFlightTeleport.getItem()).getIsFlyingMode(ringFlightTeleport));
		}

		return null;
	}

}
