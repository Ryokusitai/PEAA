package peaa.gameObjs.items;

import peaa.PEAACore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemCharge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A.K.さんが作成していらっしゃるトピック
 * 「[1.2.5 - 1.7.10]クリエイティブのような飛行モードの実装の仕方[Forge]」
 * を思いっきり参考にさせていただきました。
 * また、テレポート部分は同じくA.K.さんのMOD「EnchantChanger」の
 * 処理をほぼそのまま利用させていただいています。
 *
 * また、EnderIOのようなテレポート処理の実現に当たっては(このアイテムに実装はしていません)
 * ・http://www.geisya.or.jp/~mwm48961/electro/3d_line1.htm
 * ・http://www.sousakuba.com/Programming/gs_distance_2point.html
 * ・http://qiita.com/Hoshi_7/items/d04936883ff3eb1eed2d
 * ・http://detail.chiebukuro.yahoo.co.jp/qa/question_detail/q1222867742
 * ・http://www.sousakuba.com/Programming/gs_two_vector_angle.html
 * ・http://www24.atpages.jp/venvenkazuya/math1/trigonometric_ratio6_table1.php
 * と、非常にたくさんのサイトの情報を利用させていただきました。
 *
 * この場をお借りしてお礼申し上げます。
 * 有難う御座いました。
 *
 */
public class RingFlightTeleport extends ItemCharge
{
	@SideOnly(Side.CLIENT)
	private IIcon ringOff;
	@SideOnly(Side.CLIENT)
	private IIcon ringOn;

	private boolean canFlying;				// (アイテムの所持状態的に)飛べるかどうか
	private boolean isFlying;				// 飛んでいるかどうか
	// 飛行速度の最低値とチャージ時の速度
	private static float flyBaseSpeed = 0.022f;
	private static float[] flySpeed = {flyBaseSpeed, flyBaseSpeed * 2.0F, flyBaseSpeed * 4.0F, flyBaseSpeed * 8.0F};
	// 飛行中の最低消費Emcとチャージ時の消費Emc
	private static float flyBaseEmc = 0.16F;
	private static float[] flyEmc = {flyBaseEmc, flyBaseEmc * 2.0F, flyBaseEmc * 4.0F, flyBaseEmc * 8.0F};
	// 現在設定されている速度
	private float flyingSpeed;
	// 手に持っているかどうか
	private boolean isHeld;

	public RingFlightTeleport()
	{
		super("RingFlightTeleport", (byte)3);
		this.setNoRepair();
		this.setMaxDamage(0);
		this.setCreativeTab(ObjHandler.cTab);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeldItem)
	{
		if (world.isRemote)
			return;
		if (invSlot > 8 || !(entity instanceof EntityPlayer)) {
			canFlying = false;
			return;
		}

		// getCharge命令を使用するために必須
		if (!stack.hasTagCompound())
		{
			stack.stackTagCompound = new NBTTagCompound();
		}

		EntityPlayer player = (EntityPlayer) entity;

		// リングを手に持っているかどうか確認
		this.isHeld = isHeldItem;

		// EMC補充関係
		if (getEmc(stack) <= 0 && !consumeFuel(player, stack, 256, false))
		{
			if (canFlying)
			{
				canFlying = false;
			}

			return;
		}

		canFlying = true;
		flyingSpeed = flySpeed[this.getCharge(stack)];

		if (this.isFlying) {
			removeEmc(stack, flyEmc[this.getCharge(stack)]);
		}

	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		boolean isCharge = false;
		if (!world.isRemote) {
			isCharge = consumeFuel(player, stack, 3072, false);
		}

		if(teleportPlayer(world, player) && !world.isRemote && isCharge) {
			removeEmc(stack, 3072);
		}

		return stack;
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
		canFlying = false;
		return true;
	}

	public static float getBaseSpeed() {
		return flyBaseSpeed;
	}

	// onUpdateではplayerMPが渡されているためflight命令が実行できない
	@SubscribeEvent
	public void LivingUpdate(LivingUpdateEvent event) {
		if (event.entityLiving != null && event.entityLiving instanceof EntityPlayer && event.entityLiving.worldObj.isRemote
				&& canFlying)
		{
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			Flight(player);
		}
	}

	public void Flight(EntityPlayer player)
	{
		//if (player.worldObj.isRemote) {
            this.isFlying = PEAACore.proxy.doFlightOnSide(player, isFlying, flyingSpeed, isHeld);
		//}
	}

	//落下時ダメージ無効化処理。LivingFallEventが実装されたバージョンのみ
	@SubscribeEvent
	public void onPlayerFall(LivingFallEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			if (this.canFlying) {
				event.setCanceled(true);
			}
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg)
	{
		if (isFlying)
		{
			return ringOn;
		}

		else
		{
			return ringOff;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		ringOff = register.registerIcon("peaa:ring/ringFlightTeleport_off");
		ringOn = register.registerIcon("peaa:ring/ringFlightTeleport_on");
	}

	//-------------------------------------------------------------------------
	public boolean teleportPlayer(World world, EntityPlayer entityplayer)
	{
		Vec3 point;
		point = setTeleportPoint(world, entityplayer);
		if (point != null) {
			teleportToChunkCoord(world, entityplayer, point, entityplayer.isSneaking(), false,
					world.provider.dimensionId);
			return true;
		}

		return false;
	}

	public  Vec3 setTeleportPoint(World world, EntityPlayer entityplayer) {
        double var1 = 1.0D;
		double distLimit = 30.0D;
		double viewX = entityplayer.getLookVec().xCoord;
		double viewY = entityplayer.getLookVec().yCoord;
		double viewZ = entityplayer.getLookVec().zCoord;
/*        double playerPosX = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * var1;
        double playerPosY = entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) * var1 + entityplayer.getYOffset();
        double playerPosZ = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * var1;*/
        double playerPosX = entityplayer.posX;
        double playerPosY = entityplayer.posY + 1.62D/*1.62D - entityplayer.getYOffset()*/;
        double playerPosZ = entityplayer.posZ;
		Vec3 playerPosition = Vec3.createVectorHelper(playerPosX,
				playerPosY, playerPosZ);
		Vec3 playerLookVec = playerPosition.addVector(viewX * distLimit, viewY * distLimit, viewZ * distLimit);
		MovingObjectPosition MOP = world.rayTraceBlocks(playerPosition, playerLookVec, false);
		if (MOP != null && MOP.typeOfHit == MovingObjectType.BLOCK) {
            int blockSide = MOP.sideHit;
            ForgeDirection direction = ForgeDirection.getOrientation(blockSide);
			double blockPosX = MOP.blockX + 0.5D + direction.offsetX;
			double blockPosY = MOP.blockY + direction.offsetY;
			double blockPosZ = MOP.blockZ + 0.5D + direction.offsetZ;
            if (blockSide == 0) blockPosY--;
			return Vec3.createVectorHelper(blockPosX, blockPosY, blockPosZ);
		} else {
			return null;
		}
		// EnderIOのトラベルスタッフのようなテレポート(テレポート先のブロックの有無判定は入れていません)
		/*double distLimit = 5.0D;	// テレポートの距離

		double viewX = entityplayer.getLookVec().xCoord;
		double viewY = entityplayer.getLookVec().yCoord;
		double viewZ = entityplayer.getLookVec().zCoord;

		double viewX2 = viewX;
		double viewZ2 = viewZ;

		//向いている方向(角度?)
		float direction = MathHelper.wrapAngleTo180_float(entityplayer.rotationYaw);

		Vec3 playerLookVec = Vec3.createVectorHelper(viewX, viewY, viewZ);
		Vec3 playerLookVecXZ = Vec3.createVectorHelper(viewX, 0, viewZ);
		Vec3 playerLookVecX = Vec3.createVectorHelper(viewX2, 0, 0);

		double radian = AngleOf2Vector(playerLookVec, playerLookVecXZ);
		System.out.println("radi : " + (radian * 180.0 / Math.PI));

		double radianXZ = AngleOf2Vector(playerLookVecXZ, playerLookVecX);
		System.out.println("rXZ : " + (radianXZ * 180.0 / Math.PI));

		double x2 = Math.cos(radian) * distLimit * Math.cos(radianXZ);
		double y2 = Math.sin(radian) * distLimit;
		double z2 = Math.cos(radian) * distLimit * Math.sin(radianXZ);
		// NaNのときの代わりの数値
		if (Double.isNaN(x2))
			x2 = distLimit;
		if (Double.isNaN(y2))
			y2 = distLimit;
		if (Double.isNaN(z2))
			z2 = 0;

		double telepoPosX = (x2 * (direction >= 0 ? -1 : 1)) + entityplayer.posX;
		double telepoPosY = (y2 * (viewY < 0 ? -1 : 1)) + entityplayer.posY;
		double telepoPosZ = (z2 * (direction < -90 || direction > 90 ? -1 : 1)) + entityplayer.posZ;

		System.out.println("x2 : " + telepoPosX + " y2 : " + y2 +  " z2 : " + telepoPosZ);

		return Vec3.createVectorHelper(telepoPosX, telepoPosY, telepoPosZ);*/
	}

	private void teleportToChunkCoord(World world, EntityPlayer entityplayer, Vec3 vector,
			boolean isSneaking, boolean telepoDim, int dimID) {
        if (!world.isRemote) {
        	entityplayer.fallDistance = 0.0F;
        } else {
        	for (int var2 = 0; var2 < 32; ++var2) {
                world.spawnParticle("portal", entityplayer.posX, entityplayer.posY + world.rand.nextDouble() * 2.0D,
                        entityplayer.posZ, world.rand.nextGaussian(), 0.0D, world.rand.nextGaussian());
            }
        }
        entityplayer.setPositionAndUpdate(vector.xCoord, vector.yCoord, vector.zCoord);
	}

/*  // EnderIO風テレポートを使用する際に使用します。
	//ベクトルの長さを計算する
	double get_vector_length( Vec3 v ) {
		return Math.pow( ( v.xCoord * v.xCoord ) + ( v.yCoord * v.yCoord ) + ( v.zCoord * v.zCoord ), 0.5 );
	}

	//ベクトル内積
	double dot_product(Vec3 vl, Vec3 vr) {
		//System.out.println(vl.xCoord * vr.xCoord + " " + vl.yCoord * vr.yCoord + " " + vl.zCoord * vr.zCoord);
		return vl.xCoord * vr.xCoord + vl.yCoord * vr.yCoord + vl.zCoord * vr.zCoord;
	}

	//２つのベクトルABのなす角度θを求める
	double AngleOf2Vector(Vec3 A, Vec3 B )
	{
		//※ベクトルの長さが0だと答えが出ませんので注意してください。

		//ベクトルAとBの長さを計算する
		double length_A = get_vector_length(A);
		double length_B = get_vector_length(B);

		//内積とベクトル長さを使ってcosθを求める
		double cos_sita = dot_product(A,B) / ( length_A * length_B );

		//cosθからθを求める
		double sita = Math.acos( cos_sita );

		//ラジアンでなく0～180の角度でほしい場合はコメント外す
		//sita = sita * 180.0 / Math.PI;

		return sita;
	}
	*/
}
