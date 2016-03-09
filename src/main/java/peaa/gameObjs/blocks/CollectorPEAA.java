package peaa.gameObjs.blocks;

import peaa.PEAACore;
import peaa.gameObjs.tiles.CollectorMK4Tile;
import peaa.gameObjs.tiles.CollectorMK5Tile;
import peaa.utils.ConstantsPEAA;
import moze_intel.projecte.gameObjs.blocks.Collector;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CollectorPEAA extends Collector
{
	@SideOnly(Side.CLIENT)
	private IIcon front;
	@SideOnly(Side.CLIENT)
	private IIcon top;
	int tier;

	public CollectorPEAA(int tier)
	{
		super(3);
		this.setBlockName("pe_collector_MK" + tier);
		this.tier = tier;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)

			switch (tier)
			{
				case 4:
					player.openGui(PEAACore.instance, ConstantsPEAA.COLLECTOR4_GUI, world, x, y, z);
					break;
				case 5:
					player.openGui(PEAACore.instance, ConstantsPEAA.COLLECTOR5_GUI, world, x, y, z);
					break;
			}
		return true;
	}

	@Override
	public TileEntity createTileEntity(World var1, int var2)
	{
		if (tier == 4)
		{
			return new CollectorMK4Tile();
		}

		if (tier == 5)
		{
			return new CollectorMK5Tile();
		}

		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register)
	{
		this.blockIcon = register.registerIcon("peaa:collectors/other_"+Integer.toString(tier));
		this.front = register.registerIcon("peaa:collectors/front");
		this.top = register.registerIcon("peaa:collectors/top_"+Integer.toString(tier));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if (meta == 0 && side == 3)
		{
			return front;
		}

		if (side == 1)
		{
			return top;
		}

		return side != meta ? this.blockIcon : front;
	}
}
