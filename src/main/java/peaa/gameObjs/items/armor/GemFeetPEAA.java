package peaa.gameObjs.items.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import peaa.config.PEAAConfig;
import peaa.events.FlightEventHookPEAA;
import peaa.gameObjs.items.RingFlightTeleport;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.utils.EnumArmorType;
import moze_intel.projecte.utils.PlayerHelper;

public class GemFeetPEAA extends GemFeet
{
	public GemFeetPEAA()
    {
        super();
    }

	@Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
    	ItemStack spaceRing = FlightEventHookPEAA.getStack(player);	// 手持ちに司空の指輪があれば取得する

        if (!world.isRemote)
        {
            EntityPlayerMP playerMP = ((EntityPlayerMP) player);
            playerMP.fallDistance = 0;
        }
        else
        {
        	if (!player.capabilities.isFlying && PECore.proxy.isJumpPressed())
            {
                player.motionY += 0.1;
            }

            if (!player.onGround)
            {
                if (player.motionY <= 0)
                {
                    player.motionY *= 0.90;
                }
                if (!player.capabilities.isFlying
                		&& (spaceRing == null || (PEAAConfig.isHighSpeedMoveWhenLanding && !RingFlightTeleport.getIsFlyingMode(spaceRing))))	// 同じく判定追加
                {
                    if (player.moveForward < 0)
                    {
                        player.motionX *= 0.9;
                        player.motionZ *= 0.9;
                    } else if (player.moveForward > 0 && player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ < 3)
                    {
                        player.motionX *= 1.1;
                        player.motionZ *= 1.1;
                    }
                }
            }
        }
    }

	@Override
    public boolean canProvideFlight(ItemStack stack, EntityPlayerMP player)
    {
		ItemStack spaceRing = FlightEventHookPEAA.getStack(player);	// 手持ちに司空の指輪があれば取得する
        return player.getCurrentArmor(0) == stack && spaceRing == null;
    }
}
