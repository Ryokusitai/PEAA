package peaa.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class IsFlyingModeSyncPKT implements IMessage
{
	private int invSlot;
	private boolean isFlying;

	public IsFlyingModeSyncPKT() {}

	public IsFlyingModeSyncPKT(int invSlot, boolean isFlying)
	{
		this.invSlot = invSlot;
		this.isFlying = isFlying;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		invSlot = buf.readInt();
		isFlying = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(invSlot);
		buf.writeBoolean(isFlying);
	}

	public int getInvSlot()
	{
		return invSlot;
	}

	public boolean getIsFlying()
	{
		return isFlying;
	}

}
