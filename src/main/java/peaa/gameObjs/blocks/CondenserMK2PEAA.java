package peaa.gameObjs.blocks;

import java.util.Random;

import peaa.gameObjs.tiles.CondenserMK2TilePEAA;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.CondenserMK2;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CondenserMK2PEAA extends CondenserMK2
{
	int x,y,z;

	public CondenserMK2PEAA()
	{
		super();
		this.setBlockName("pe_condenser_mk2");
	}

	@Override
	public Item getItemDropped(int par1, Random random, int par2)
	{
		return Item.getItemFromBlock(ObjHandler.condenserMk2);
	}

	@Override
	public int getRenderType()
	{
		return Constants.CONDENSER_MK2_RENDER_ID;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		TileEntity tile = new CondenserMK2TilePEAA();
		if (!var1.isRemote) {
			((CondenserMK2TilePEAA) tile).checkAroundAEGU(var1, x, y, z);
		}

		return tile;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		//Minecraft.getMinecraft().thePlayer.sendChatMessage("発見しました");
		if (!world.isRemote)
		{
			player.openGui(PECore.instance, Constants.CONDENSER_MK2_GUI, world, x, y, z);
		}

		return true;
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		this.x = x;
		this.y = y;
		this.z = z;

		return metadata;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int noclue)
	{
		IInventory tile = (IInventory) world.getTileEntity(x, y, z);

		if (tile == null)
		{
			return;
		}

		for (int i = 1; i < tile.getSizeInventory(); i++)
		{
			ItemStack stack = tile.getStackInSlot(i);

			if (stack == null)
			{
				continue;
			}

			Utils.spawnEntityItem(world, stack, x, y, z);
		}

		if (!world.isRemote) {
			// PEAAのタイルかどうかチェック
			if(tile instanceof CondenserMK2TilePEAA) {
					// 生成モードだった場合モードを解除
					if (((CondenserMK2TilePEAA)tile).isGenerate) {
						((CondenserMK2TilePEAA)tile).changeGenerate(false);
					}
			}
		}

		world.func_147453_f(x, y, z, block);
		world.removeTileEntity(x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register)
	{
		this.blockIcon = register.registerIcon("obsidian");
	}
}
