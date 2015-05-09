package peaa.gameObjs.tiles;

import peaa.gameObjs.blocks.AEGU;
import peaa.utils.ConstantsPEAA;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;

/*
public class CollectorMK4Tile extends CollectorMK1Tile
{
	public CollectorMK4Tile() {
		super(Constants.COLLECTOR_MK4_MAX, Constants.COLLECTOR_MK4_GEN, 17, 18);
	}


}*/
public class CollectorMK4Tile extends CollectorMK1Tile
{

	public CollectorMK4Tile()
	{
		super(ConstantsPEAA.COLLECTOR_MK4_MAX, ConstantsPEAA.COLLECTOR_MK4_GEN, 17, 18);
	}

	public CollectorMK4Tile(int maxEmc, int emcGen, int upgradedSlot, int lockSlot)
	{
		super(maxEmc, emcGen, upgradedSlot, lockSlot);
	}

	/**
	 * mk4とmk5はAEGUがあるかどうかで判断
	 */
	public int getSunLevel()
	{
		if (worldObj.getBlock(xCoord, yCoord + 1, zCoord) instanceof AEGU)
		{
			return 16;
		}
		return 0;
	}

}

