package peaa.proxies;

import net.minecraft.entity.player.EntityPlayer;

public class CommonProxy
{
	public void registerClientOnlyEvents() {}

	public boolean doFlightOnSide(EntityPlayer player, boolean isFlying, float flyingSpeed, boolean isHeld){return false;}
}
