package peaa.gameObjs.blocks;

import java.util.Random;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import peaa.gameObjs.ObjHandlerPEAA;
import peaa.gameObjs.tiles.CondenserMK2TilePEAA;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AEGU extends Block
{

//	@SideOnly(Side.CLIENT)
//	private IIcon generateIcon;

	private int tier;

	protected boolean isGenerate;
	String xMK2 = "", yMK2 = "", zMK2 = "";	// nullという状態を持たせたいため、intではなくstrで作成する

	public AEGU(int tier, boolean isGenerate) {
		super(Material.grass);
		if (!isGenerate)
			setCreativeTab(ObjHandler.cTab);
		setBlockName("AEGU_MK" + tier);
		setHardness(0.3F);
		setLightLevel(1.0F);
		setStepSound(soundTypeGlass);		// オリジナル準拠 この音好きなんです

		this.tier = tier;
		this.isGenerate = isGenerate;
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		//ブロックを設置した際の動作(setBlock による設置は含まない)
		if (!world.isRemote) {
			if(this.checkMK2(world, x, y, z) == true) {
				this.notifyToMK2(world, x, y, z);
			}
		}

		return metadata;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int size, float posX, float posY, float posZ)
	{
		// ブロックを右クリックした時の動作
		//changeGenerate(world, x, y, z);
		if (!world.isRemote)
			openMK2GUI(world, x, y, z, player);
		return isGenerate;
	}

	public void changeGenerate(World world, int x, int y, int z) {
		switch (tier) {
		case 1:
			if (this.isGenerate) {
				world.setBlock(x, y, z, ObjHandlerPEAA.aeguMK1_off);
			} else {
				world.setBlock(x, y, z, ObjHandlerPEAA.aeguMK1_on);
			}
			break;
		case 2:
			if (this.isGenerate) {
				world.setBlock(x, y, z, ObjHandlerPEAA.aeguMK2_off);
			} else {
				world.setBlock(x, y, z, ObjHandlerPEAA.aeguMK2_on);
			}
			break;
		case 3:
			if (this.isGenerate) {
				world.setBlock(x, y, z, ObjHandlerPEAA.aeguMK3_off);
			} else {
				world.setBlock(x, y, z, ObjHandlerPEAA.aeguMK3_on);
			}
			break;
		}

	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata)
	{
		if (!world.isRemote) {
			//System.out.println("ブロックが破壊されました");
			notifyBreak(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon("peaa:aegu/aegu" + tier + (isGenerate ? "gene" : ""));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		// 生成中かどうか判断して、blockIconもしくはgenerateIconを返す
		return this.blockIcon;
	}

	/**
	 * ドロップアイテムの指定
	 * @param no
	 * @param rnd
	 * @param clue
	 * @return
	 */
	@Override
	public Item getItemDropped(int no, Random rnd, int clue)
	{
		switch (tier) {
		case 1:
			return Item.getItemFromBlock(ObjHandlerPEAA.aeguMK1_off);
		case 2:
			return Item.getItemFromBlock(ObjHandlerPEAA.aeguMK2_off);
		case 3:
			return Item.getItemFromBlock(ObjHandlerPEAA.aeguMK3_off);
		}
		return null;
	}

	/**
	 * MK2ブロックが周囲にあるか検索する
	 */
	public boolean checkMK2(World world, int xCoord, int yCoord, int zCoord) {
		//System.out.println("xC " + xCoord + " yC " + yCoord + " zC " + zCoord);
		xMK2 = "";yMK2 = "";zMK2 = "";

		for (int x = xCoord - 1; x <= xCoord + 1; x++) {
			for (int y = yCoord - 1; y <= yCoord + 1; y++) {
				for (int z = zCoord - 1; z <= zCoord + 1; z++) {
					//System.out.println("x "+ x + " y " + y + " z " + z + " block name : " + world.getBlock(x, y, z));
					if (world.getBlock(x, y, z) instanceof CondenserMK2PEAA) {
						// 既に登録しているものを再登録しようとしているなら複数設置ではないので問題なし。
						//if (xMK2.equals(String.valueOf(x)) && yMK2.equals(String.valueOf(y)) && yMK2.equals(String.valueOf(y)))
						//		return true;

						//System.out.println("xMK2 = " + xMK2 + " yMK2 = " + yMK2 + " zMK2 = " + zMK2);
						if (xMK2 == "" && yMK2 == "" && zMK2 == "") {
							xMK2 = String.valueOf(x);
							yMK2 = String.valueOf(y);
							zMK2 = String.valueOf(z);
							//Minecraft.getMinecraft().thePlayer.sendChatMessage("MK2を発見しました");
						} else {
							Minecraft.getMinecraft().thePlayer.sendChatMessage("複数のMK2に対しての設置は出来ません");
							return false;
						}
					}
				}
			}
		}
		if (xMK2 != "" && yMK2 != "" && zMK2 != "") {
			//Minecraft.getMinecraft().thePlayer.sendChatMessage("登録しました");
			return true;
		}
		return false;
	}

	/**
	 * MK2に自身の存在を通知する
	 * @param world
	 */
	public void notifyToMK2(World world, int xCoord, int yCoord, int zCoord)
	{
		int x = Integer.parseInt(xMK2);
		int y = Integer.parseInt(yMK2);
		int z = Integer.parseInt(zMK2);
		CondenserMK2TilePEAA tile = (CondenserMK2TilePEAA)world.getTileEntity(x, y, z);
		tile.storeAEGUCoord(this, xCoord, yCoord, zCoord);
	}

	/**
	 * ブロック破壊時にMK2ブロックが周囲にあるか検索する
	 */
	public boolean notifyBreak(World world, int xCoord, int yCoord, int zCoord) {
		//System.out.println("xC " + xCoord + " yC " + yCoord + " zC " + zCoord);
		xMK2 = "";yMK2 = "";zMK2 = "";

		for (int x = xCoord - 1; x <= xCoord + 1; x++) {
			for (int y = yCoord - 1; y <= yCoord + 1; y++) {
				for (int z = zCoord - 1; z <= zCoord + 1; z++) {
					//System.out.println("x "+ x + " y " + y + " z " + z + " block name : " + world.getBlock(x, y, z));
					if (world.getBlock(x, y, z) instanceof CondenserMK2PEAA) {
						CondenserMK2TilePEAA tile = (CondenserMK2TilePEAA)world.getTileEntity(x, y, z);
						if (tile.destoreAEGUCoord(this, xCoord, yCoord, zCoord) == true) {
							//Minecraft.getMinecraft().thePlayer.sendChatMessage("情報削除完了");
							return true;
						}
					}
				}
			}
		}
		if (xMK2 != "" && yMK2 != "" && zMK2 != "") {
			Minecraft.getMinecraft().thePlayer.sendChatMessage("MK2があるのにもかかわらずこのAEGUは登録されていません。それはそれでおかしいです。");
			return false;
		}
		return false;
	}

	/**
	 * 右クリック時にMK2のGUIを開けるかどうかチェックし、可能なら開く
	 */
	public boolean openMK2GUI(World world, int xCoord, int yCoord, int zCoord, EntityPlayer player) {
		xMK2 = "";yMK2 = "";zMK2 = "";

		for (int x = xCoord - 1; x <= xCoord + 1; x++) {
			for (int y = yCoord - 1; y <= yCoord + 1; y++) {
				for (int z = zCoord - 1; z <= zCoord + 1; z++) {
					if (world.getBlock(x, y, z) instanceof CondenserMK2PEAA) {
						CondenserMK2TilePEAA tile = (CondenserMK2TilePEAA)world.getTileEntity(x, y, z);
						if (tile.checkStore(xCoord, yCoord, zCoord) != -1) {
							System.out.println(world.getBlock(x, y, z));
							if (tile.isGenerate)
								player.openGui(PECore.instance, Constants.CONDENSER_MK2_GUI, world, x, y, z);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isGenerate() {
		return this.isGenerate;
	}

	public int getTier() {
		return this.tier;
	}

}
