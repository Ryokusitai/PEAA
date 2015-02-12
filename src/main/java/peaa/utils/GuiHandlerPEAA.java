package peaa.utils;

import peaa.gameObjs.container.CollectorMK4Container;
import peaa.gameObjs.gui.GUICollectorMK4;
import peaa.gameObjs.gui.GUICollectorMK5;
import peaa.gameObjs.tiles.CollectorMK4Tile;
import peaa.gameObjs.tiles.CollectorMK5Tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandlerPEAA implements IGuiHandler
{
	/*サーバー側の処理*/
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (ID == ConstantsPEAA.COLLECTOR4_GUI) {
			if (tile != null && tile instanceof CollectorMK4Tile)
				return new CollectorMK4Container(player.inventory, (CollectorMK4Tile) tile);

		} else if (ID == ConstantsPEAA.COLLECTOR5_GUI) {
			if (tile != null && tile instanceof CollectorMK5Tile)
				return new CollectorMK4Container(player.inventory, (CollectorMK5Tile) tile);
		}

		return null;
	}


	/*クライアント側の処理*/
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (ID == ConstantsPEAA.COLLECTOR4_GUI) {
			if (tile != null && tile instanceof CollectorMK4Tile)
				return new GUICollectorMK4(player.inventory, (CollectorMK4Tile) tile);
		}
		if (ID == ConstantsPEAA.COLLECTOR5_GUI) {
			if (tile != null && tile instanceof CollectorMK5Tile)
				return new GUICollectorMK5(player.inventory, (CollectorMK5Tile) tile);
		}

		return null;
	}

}
