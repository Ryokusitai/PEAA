package peaa.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class RingFlightTeleportSyncPKT implements IMessage
{
	private int invSlot;
	private boolean canFlying;
	private boolean isHeld;
	float flyingSpeed;

	public RingFlightTeleportSyncPKT() {}

	public RingFlightTeleportSyncPKT(int invSlot, boolean canFlying, boolean isHeld, float flyingSpeed)
	{
		this.invSlot = invSlot;
		this.canFlying = canFlying;
		this.isHeld = isHeld;
		this.flyingSpeed = flyingSpeed;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		invSlot = buf.readInt();
		canFlying = buf.readBoolean();
		isHeld = buf.readBoolean();
		flyingSpeed = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(invSlot);
		buf.writeBoolean(canFlying);
		buf.writeBoolean(isHeld);
		buf.writeFloat(flyingSpeed);
	}

	public int getInvSlot()
	{
		return invSlot;
	}

	public boolean getCanFlying()
	{
		return canFlying;
	}

	public boolean getIsHeld()
	{
		return isHeld;
	}

	public float getFlyingSpeed()
	{
		return flyingSpeed;
	}

}
