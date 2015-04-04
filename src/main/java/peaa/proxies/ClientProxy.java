package peaa.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import peaa.events.ToolTipEvent;
import peaa.gameObjs.items.RingFlightTeleport;

public class ClientProxy extends CommonProxy
{
	private boolean allowFlying;			// (状況的に)飛べるかどうか
	private int flyToggleTimer = 0;			// Jumpキーの入力間隔
	private int sprintToggleTimer = 0;		// ダッシュの入力間隔

	boolean isHold;
	float flyingSpeed;

	public static final float moveFactor = 0.4F;

	@Override
	public void registerClientOnlyEvents()
	{
		MinecraftForge.EVENT_BUS.register(new ToolTipEvent());
	}

	@Override
	public boolean doFlightOnSide(EntityPlayer player, boolean isFlying, float flyingSpeed, boolean isHold)
	{
		this.isHold = isHold;
		this.flyingSpeed = flyingSpeed;

		this.allowFlying = !(player.capabilities.isCreativeMode || player.capabilities.allowFlying);
		// クリエイティブ時と他MODでクリエイティブの飛行モードが許可されている場合は実行しないように
		if (!this.allowFlying) {
			isFlying = false;
			return isFlying;
		}

		boolean jump = ((EntityPlayerSP)player).movementInput.jump;		// ジャンプ入力があるかどうか
		float var2 = 0.8F;
		boolean var3 = ((EntityPlayerSP)player).movementInput.moveForward >= var2;
		((EntityPlayerSP)player).movementInput.updatePlayerMoveState();		// 入力ステートを更新
		if (this.allowFlying && !jump && ((EntityPlayerSP)player).movementInput.jump)	// jumpキーを押して離したかどうか
		{
			if (this.flyToggleTimer == 0)
			{
				this.flyToggleTimer = 7;
			}
			else {
				isFlying = !isFlying;
				this.flyToggleTimer = 0;
			}
		}

		// ここからダッシュ関係
		boolean var4 = (float)((EntityPlayerSP)player).getFoodStats().getFoodLevel() > 6.0F;
		if (((EntityPlayerSP)player).onGround && !var3 && ((EntityPlayerSP)player).movementInput.moveForward >= var2 && !((EntityPlayer)player).isSprinting() && var4 && !((EntityPlayer)player).isUsingItem() && !((EntityPlayerSP)player).isPotionActive(Potion.blindness))
		{
			if (this.sprintToggleTimer == 0)
			{
				this.sprintToggleTimer = 7;
			}
			else
			{
				((EntityPlayerSP)player).setSprinting(true);
				this.sprintToggleTimer = 0;
			}
		}
		if (this.sprintToggleTimer > 0)
		{
			--this.sprintToggleTimer;
		}
		// ここまでダッシュ関係
		if (this.flyToggleTimer > 0)
		{
			--this.flyToggleTimer;
		}
		if (player.onGround && isFlying)
		{
			isFlying = false;
		}
		if (isFlying)
		{
			movePlayerY(player);
            movePlayerXZ(player);
		}

		return isFlying;
	}

	private void movePlayerY(EntityPlayer player) {
        EntityPlayerSP playerSP = (EntityPlayerSP)player;

        player.motionY = 0.0D;

        if (playerSP.movementInput.sneak && !isHold) {
            player.motionY -= moveFactor;
        }

        if (playerSP.movementInput.jump) {
            player.motionY += moveFactor;
        }
    }

    private void movePlayerXZ(EntityPlayer player) {
        EntityPlayerSP playerSP = (EntityPlayerSP)player;
        float moveForward = playerSP.movementInput.moveForward;
        float moveStrafe = playerSP.movementInput.moveStrafe;

        if (moveForward != 0 || moveStrafe != 0 || RingFlightTeleport.getBaseSpeed() == flyingSpeed) {
            player.motionX = player.motionZ = 0;
        }
        //player.moveFlying(moveStrafe, moveForward, flyingSpeed * 10.0F);//moveFactor * 1.2F);
        player.moveFlying(moveStrafe, moveForward, playerSP.movementInput.sneak?(flyingSpeed * 35.0F):(flyingSpeed * 10.0F));//
    }
}
