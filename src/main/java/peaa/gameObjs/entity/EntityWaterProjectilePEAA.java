package peaa.gameObjs.entity;

import java.util.List;

import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class EntityWaterProjectilePEAA extends EntityWaterProjectile
{
	public EntityWaterProjectilePEAA(World world)
	{
		super(world);
	}

	public EntityWaterProjectilePEAA(World world, EntityLivingBase entity)
	{
		super(world, entity);
	}

	public EntityWaterProjectilePEAA(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (!this.worldObj.isRemote) {
			//火のオーブと接触しても消える
			List list = this.worldObj.loadedEntityList;
			for (int i = 0; i < list.size(); i++) {
				Entity enti = (Entity)list.get(i);
				if (enti instanceof EntityLavaProjectile) {
					for (int x = (int) (this.posX - 1); x <= this.posX + 1; x++)
						for (int y = (int) (this.posY - 1); y <= this.posY + 1; y++)
							for (int z = (int) (this.posZ - 1); z <= this.posZ + 1; z++)
							{
								if (x == (int)enti.posX && y == (int)enti.posY && z == (int)enti.posZ) {
									this.setDead();
									enti.setDead();
								}
							}
				}
			}
		}

	}
}
