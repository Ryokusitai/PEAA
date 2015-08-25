package peaa.events;

import static peaa.gameObjs.items.RingFlightTeleport.getCanFlying;
import static peaa.gameObjs.items.RingFlightTeleport.getFlyingSpeed;
import static peaa.gameObjs.items.RingFlightTeleport.getInvSlot;
import static peaa.gameObjs.items.RingFlightTeleport.getIsFlyingMode;
import static peaa.gameObjs.items.RingFlightTeleport.getIsHeld;
import static peaa.gameObjs.items.RingFlightTeleport.setIsFlyingMode;

import java.security.InvalidParameterException;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import peaa.PEAACore;
import peaa.gameObjs.items.RingFlightTeleport;
import peaa.network.IsFlyingModeSyncPKT;
import peaa.network.PacketHandlerPEAA;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FlightEventHookPEAA
{
	// onUpdateではplayerMPが渡されているためflight命令が実行できない
	@SubscribeEvent
	public void LivingUpdate(LivingUpdateEvent event) {
		if (event.entityLiving.worldObj.isRemote && event.entityLiving != null && event.entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			ItemStack stack = getStack(player);
			if (stack != null && stack.hasTagCompound())
			{
				if (getCanFlying(stack))
					Flight(player, stack);
				else if(getIsFlyingMode(stack))
					setIsFlyingMode(stack, false);
			}

		}
	}

	public void Flight(EntityPlayer player, ItemStack stack)
	{
		if (!(stack.getItem() instanceof RingFlightTeleport)) {
			throw new IllegalArgumentException("司空の指輪以外のアイテムスタックが渡されました");
		}
		boolean ringsIsFlyingMode = getIsFlyingMode(stack);
		boolean isFlying = PEAACore.proxy.doFlightOnSide(player, ringsIsFlyingMode, getFlyingSpeed(stack), getIsHeld(stack));
		syncIsFlyingMode(stack, isFlying, ringsIsFlyingMode);
	}

	//落下時ダメージ無効化処理。LivingFallEventが実装されたバージョンのみ
	@SubscribeEvent
	public void onPlayerFall(LivingFallEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			ItemStack stack = getStack(player);
			if (stack != null && getCanFlying(stack))
			{
				event.setCanceled(true);
			}
		}

	}

	/**
	 * 飛行モードのon/offが切り替わった場合は同期を行う
	 * 切り替わっていない場合は同期の必要なし
	 * @param stack					司空の指輪のアイテムスタック
	 * @param isFlying				飛行しているかどうか
	 * @param ringsIsFlyingMode		司空の指輪の飛行モード(飛行能力発動中か否か)
	 * @return	同期処理を行ったかどうか(同期の必要がなかった場合false, 同期を行った場合true)
	 */
	public boolean syncIsFlyingMode(ItemStack stack, boolean isFlying, boolean ringsIsFlyingMode)
	{
		if (isFlying == ringsIsFlyingMode) { return false;}

		setIsFlyingMode(stack, isFlying);
		PacketHandlerPEAA.INSTANCE.sendToServer(new IsFlyingModeSyncPKT(getInvSlot(stack), isFlying));
		return true;
	}

	/**
	 * 手持ちスロットの中から司空の指輪を検索してstackを返す
	 * @param player
	 * @param invSlot
	 * @return
	 */
	public static ItemStack getStack(EntityPlayer player)
	{
		int slot = -1;
		ItemStack stack;

		for (int i = 0; i < 9; i++)
		{
			stack = player.inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() instanceof RingFlightTeleport)
			{
				if (!stack.hasTagCompound())
				{
					stack.stackTagCompound = new NBTTagCompound();
				}
				// 初めて見つけたなら即登録
				if (slot == -1)
				{
					slot = i;
				}
				else
				{
					// 見つけたのが飛行モード中
					if (getIsFlyingMode(stack))
					{
						// 1つ前に見つけたのは飛行モードじゃない
						if (!getIsFlyingMode(player.inventory.getStackInSlot(slot)))
						{
							slot = i;
						}
						// どっちも飛行モード
						else
						{
							// そんなことは想定していないのでエラーを返す
							throw new InvalidParameterException("手持ちに複数の飛行モード中の司空の指輪があります。"
									+ "この現象は想定外です。");
						}
					}
					// 飛行モードではなかった場合slotの更新はしない
				}
			}
		}
		if (slot == -1)
		{
			return null;
		}
		else
		{
			return player.inventory.getStackInSlot(slot);
		}
	}

}
