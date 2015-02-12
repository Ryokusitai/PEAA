package peaa.gameObjs.blocks;

import peaa.gameObjs.tiles.RMFurnaceTilePEAA;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MatterFurnacePEAA extends MatterFurnace
{
	private boolean isHighTier;

	public MatterFurnacePEAA(boolean active, boolean isRM)
	{
		super(active, isRM);
		isHighTier = isRM;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		if (isHighTier) return new RMFurnaceTilePEAA();
		return new DMFurnaceTile();
	}
}
