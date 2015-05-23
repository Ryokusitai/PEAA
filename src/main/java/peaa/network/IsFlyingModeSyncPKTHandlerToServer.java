package peaa.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import peaa.PEAACore;
import peaa.gameObjs.items.RingFlightTeleport;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class IsFlyingModeSyncPKTHandlerToServer implements IMessageHandler<IsFlyingModeSyncPKT, IMessage>
{
	@Override
	public IMessage onMessage(IsFlyingModeSyncPKT pkt, MessageContext ctx)
	{
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		ItemStack ringFlightTeleport = player.inventory.mainInventory[pkt.getInvSlot()];
		if (ringFlightTeleport != null && ringFlightTeleport.getItem() instanceof RingFlightTeleport)
		{
			ringFlightTeleport.stackTagCompound.setBoolean("isFlyingMode", pkt.getIsFlying());

			// この時には既にクライアントの値が上書きされてしまっているのでさらに上書きする(強引)
			PacketHandlerPEAA.INSTANCE.sendToAll(new IsFlyingModeSyncPKT(pkt.getInvSlot(), pkt.getIsFlying()));
		}

		return null;
	}

}
