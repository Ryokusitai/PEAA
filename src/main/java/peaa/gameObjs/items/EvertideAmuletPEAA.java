package peaa.gameObjs.items;

import moze_intel.projecte.gameObjs.items.EvertideAmulet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import peaa.gameObjs.entity.EntityWaterProjectilePEAA;

public class EvertideAmuletPEAA extends EvertideAmulet
{
	public EvertideAmuletPEAA() {
		super();
	}

	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack)
	{
		World world = player.worldObj;

		if (!world.provider.isHellWorld)
		{
			world.spawnEntityInWorld(new EntityWaterProjectilePEAA(world, player));
			return true;
		}

		return false;
	}

}
