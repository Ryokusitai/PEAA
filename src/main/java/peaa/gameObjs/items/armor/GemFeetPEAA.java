package peaa.gameObjs.items.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import peaa.config.PEAAConfig;
import peaa.events.FlightEventHookPEAA;
import peaa.gameObjs.items.RingFlightTeleport;
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

            if (isStepAssistEnabled(stack))
            {
                if (playerMP.stepHeight != 1.0f)
                {
                    playerMP.stepHeight = 1.0f;
                    PlayerHelper.updateClientStepHeight(playerMP, 1.0F);
                    PlayerChecks.addPlayerStepChecks(playerMP);
                }
            }

            if (!player.capabilities.allowFlying && spaceRing == null)	// 判定一つ追加
            {
                PlayerHelper.enableFlight(playerMP);
            }
            if (player.capabilities.allowFlying && spaceRing != null)	// 判定一つ追加
            {
                PlayerHelper.disableFlight(playerMP);
            }
        }
        else
        {
            if (!player.onGround)
            {
                if (player.motionY <= 0)
                {
                    player.motionY *= 0.90;
                }
                if (!player.capabilities.isFlying
                		&& (PEAAConfig.isHighSpeedMoveWhenLanding ? !RingFlightTeleport.getIsFlyingMode(spaceRing):spaceRing == null))	// 同じく判定追加
                {
                    player.motionX *= 1.1;
                    player.motionZ *= 1.1;
                }
            }
        }
    }
}
