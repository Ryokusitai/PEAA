package peaa.gameObjs.tiles;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.blocks.Relay;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.utils.ItemHelper;
//import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.Facing;
import net.minecraftforge.common.util.ForgeDirection;

public class RMFurnaceTilePEAA extends RMFurnaceTile
{
	private final float EMC_CONSUMPTION = 1.6f;

	public RMFurnaceTilePEAA() {
		super();
	}

	@Override
	public void updateEntity()
	{
		boolean flag = furnaceBurnTime > 0;
		boolean flag1 = false;

		if (furnaceBurnTime > 0)
		{
			--furnaceBurnTime;
		}

		if (!this.worldObj.isRemote)
		{
			pullFromInventories();
			pushSmeltStack();
		}

		if (!worldObj.isRemote)
		{
			if (inventory[0] != null && inventory[0].getItem() == ObjHandler.kleinStars)
			{
				if (KleinStar.getEmc(inventory[0]) >= EMC_CONSUMPTION)
				{
					KleinStar.removeEmc(inventory[0], EMC_CONSUMPTION);
					this.addEMC(EMC_CONSUMPTION);
				}
			}

			if (this.getStoredEmc() >= EMC_CONSUMPTION)
			{
				furnaceBurnTime = 1;
				this.removeEMC(EMC_CONSUMPTION);
			}

			if (furnaceBurnTime == 0 && canSmelt())
			{
				currentItemBurnTime = furnaceBurnTime = getItemBurnTime(inventory[0]);

				if (furnaceBurnTime > 0)
				{
					flag1 = true;

					if (inventory[0] != null)
					{
						--inventory[0].stackSize;

						if (inventory[0].stackSize == 0)
						{
							inventory[0] = inventory[0].getItem().getContainerItem(inventory[0]);
						}
					}
				}
			}

			if (furnaceBurnTime > 0 && canSmelt())
			{
				++furnaceCookTime;

				if (furnaceCookTime == ticksBeforeSmelt)
				{
					furnaceCookTime = 0;
					smeltItem();
					flag1 = true;
				}
			}
			else furnaceCookTime = 0;

			if (flag != furnaceBurnTime > 0)
			{
				flag1 = true;
				Block block = worldObj.getBlock(xCoord, yCoord, zCoord);

				if (!this.worldObj.isRemote && block instanceof MatterFurnace)
				{
					((MatterFurnace) block).updateFurnaceBlockState(furnaceBurnTime > 0, worldObj, xCoord, yCoord, zCoord);
				}
			}
		}

		if (flag1)
		{
			markDirty();
		}

		if (!this.worldObj.isRemote)
		{
			pushOutput();
			pushToInventories();
		}
	}

	private void pushSmeltStack()
	{
		ItemStack stack = inventory[1];

		for (int i = inputStorage[0]; i <= inputStorage[1]; i++)
		{
			ItemStack slotStack = inventory[i];

			if (slotStack != null && (stack == null || ItemHelper.areItemStacksEqual(slotStack, stack)))
			{
				if (stack == null)
				{
					inventory[1] = slotStack.copy();
					inventory[i] = null;
					break;
				}

				int remain = stack.getMaxStackSize() - stack.stackSize;

				if (remain == 0)
				{
					break;
				}
				if (slotStack.stackSize <= remain)
				{
					inventory[i] = null;
					inventory[1].stackSize += slotStack.stackSize;
					break;
				}
				else
				{
					this.decrStackSize(i, remain);
					inventory[1].stackSize += remain;
				}
			}
		}
	}

	private void pushOutput()
	{
		ItemStack output = inventory[outputSlot];

		if (output == null)
		{
			return;
		}

		for (int i = outputStorage[0]; i <= outputStorage[1]; i++)
		{
			ItemStack stack = inventory[i];

			if (stack == null)
			{
				inventory[i] = output;
				inventory[outputSlot] = null;
				return;
			}
			else
			{
				if (ItemHelper.areItemStacksEqual(output, stack) && stack.stackSize < stack.getMaxStackSize())
				{
					int remain = stack.getMaxStackSize() - stack.stackSize;

					if (output.stackSize <= remain)
					{
						inventory[outputSlot] = null;
						inventory[i].stackSize += output.stackSize;
						return;
					}
					else
					{
						this.decrStackSize(outputSlot, remain);
						inventory[i].stackSize += remain;
					}
				}
			}
		}
	}

	private void pullFromInventories()
	{
		TileEntity tile = this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);

		if (tile instanceof ISidedInventory)
		{
			//The bottom side of the tile pulling from (ForgeDirection.DOWN)
			final int side = 0;
			ISidedInventory inv = (ISidedInventory) tile;

			int[] slots = inv.getAccessibleSlotsFromSide(side);

			if (slots.length > 0)
			{
				for (int i : slots)
				{
					ItemStack stack = inv.getStackInSlot(i);

					if (stack == null)
					{
						continue;
					}

					if (inv.canExtractItem(i, stack, side))
					{
						if (TileEntityFurnace.isItemFuel(stack) || stack.getItem() == ObjHandler.kleinStars)
						{
							if (inventory[0] == null)
							{
								inventory[0] = stack;
								inv.setInventorySlotContents(i, null);
								break;
							}
							else if (ItemHelper.areItemStacksEqual(stack, inventory[0]))
							{
								int remain = inventory[0].getMaxStackSize() - inventory[0].stackSize;

								if (stack.stackSize <= remain)
								{
									inventory[0].stackSize += stack.stackSize;
									inv.setInventorySlotContents(i, null);
									break;
								}
								else
								{
									inventory[0].stackSize += remain;
									stack.stackSize -= remain;
								}
							}

							continue;
						}

						for (int j = inputStorage[0]; j < inputStorage[1]; j++)
						{
							ItemStack otherStack = inventory[j];

							if (otherStack == null)
							{
								inventory[j] = stack;
								inv.setInventorySlotContents(i, null);
								break;
							}
							else if (ItemHelper.areItemStacksEqual(stack, otherStack))
							{
								int remain = otherStack.getMaxStackSize() - otherStack.stackSize;

								if (stack.stackSize <= remain)
								{
									inventory[j].stackSize += stack.stackSize;
									inv.setInventorySlotContents(i, null);
									break;
								}
								else
								{
									inventory[j].stackSize += remain;
									stack.stackSize -= remain;
								}
							}
						}
					}
				}
			}
		}
		else if (tile instanceof IInventory)
		{
			IInventory inv = (IInventory) tile;

			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				ItemStack stack = inv.getStackInSlot(i);

				if (stack == null)
				{
					continue;
				}

				if (TileEntityFurnace.isItemFuel(stack) || stack.getItem() == ObjHandler.kleinStars)
				{
					if (inventory[0] == null)
					{
						inventory[0] = stack;
						inv.setInventorySlotContents(i, null);
						break;
					}
					else if (ItemHelper.areItemStacksEqual(stack, inventory[0]))
					{
						int remain = inventory[0].getMaxStackSize() - inventory[0].stackSize;

						if (stack.stackSize <= remain)
						{
							inventory[0].stackSize += stack.stackSize;
							inv.setInventorySlotContents(i, null);
							break;
						}
						else
						{
							inventory[0].stackSize += remain;
							stack.stackSize -= remain;
						}
					}

					continue;
				}
				else if (FurnaceRecipes.smelting().getSmeltingResult(stack) == null)
				{
					continue;
				}

				for (int j = inputStorage[0]; j < inputStorage[1]; j++)
				{
					ItemStack otherStack = inventory[j];

					if (otherStack == null)
					{
						inventory[j] = stack;
						inv.setInventorySlotContents(i, null);
						break;
					}
					else if (ItemHelper.areItemStacksEqual(stack, otherStack))
					{
						int remain = otherStack.getMaxStackSize() - otherStack.stackSize;

						if (stack.stackSize <= remain)
						{
							inventory[j].stackSize += stack.stackSize;
							inv.setInventorySlotContents(i, null);
							break;
						}
						else
						{
							inventory[j].stackSize += remain;
							stack.stackSize -= remain;
						}
					}
				}
			}
		}
	}


	private void pushToInventories()
	{
		int iSide = 0;

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			if (dir.offsetY > 0)
			{
				continue;
			}

			int x = this.xCoord + dir.offsetX;
			int y = this.yCoord + dir.offsetY;
			int z = this.zCoord + dir.offsetZ;

			TileEntity tile = this.worldObj.getTileEntity(x, y, z);

			if (tile == null)
			{
				continue;
			}

			//「反物質炉」「マターかまど」のいずれでもないことを確認
			Block block = this.worldObj.getBlock(x, y, z);
			if(block instanceof Relay ||
					block instanceof MatterFurnace) {
				continue;
			}

			if (tile != null && tile instanceof ISidedInventory)
			{
				ISidedInventory inv = (ISidedInventory) tile;

				if (inv != null)
				{
					int[] slots = inv.getAccessibleSlotsFromSide(ForgeDirection.OPPOSITES[iSide]);

					if (slots.length > 0)
					{
						for (int j = outputStorage[0]; j < outputStorage[1]; j++)
						{
							ItemStack stack = inventory[j];

							if (stack == null)
							{
								continue;
							}

							for (int k : slots)
							{
								if (inv.canInsertItem(k, stack, Facing.oppositeSide[iSide]))
								{
									ItemStack otherStack = inv.getStackInSlot(k);

									if (otherStack == null)
									{
										inv.setInventorySlotContents(k, stack);
										inventory[j] = null;
										break;
									}
									else if (ItemHelper.areItemStacksEqual(stack, otherStack))
									{
										int remain = otherStack.getMaxStackSize() - otherStack.stackSize;

										if (stack.stackSize <= remain)
										{
											otherStack.stackSize += stack.stackSize;
											inventory[j] = null;
											break;
										}
										else
										{
											otherStack.stackSize += remain;
											inventory[j].stackSize -= remain;
										}
									}
								}
							}
						}
					}
				}
			}
			else if (tile instanceof IInventory)
			{
				for (int j = outputStorage[0]; j <= outputStorage[1]; j++)
				{
					ItemStack stack = inventory[j];

					if (stack != null)
					{
						ItemStack result = ItemHelper.pushStackInInv((IInventory) tile, stack);

						if (result == null)
						{
							inventory[j] = null;
							break;
						}
						else
						{
							inventory[j].stackSize = result.stackSize;
						}
					}
				}
			}
		}
	}

	private void smeltItem()
	{
		ItemStack toSmelt = inventory[1];
		ItemStack smeltResult = FurnaceRecipes.smelting().getSmeltingResult(toSmelt).copy();
		ItemStack currentSmelted = getStackInSlot(outputSlot);

		if (ItemHelper.getOreDictionaryName(toSmelt).startsWith("ore"))
		{
			smeltResult.stackSize *= 2;
		}

		if (currentSmelted == null)
		{
			setInventorySlotContents(outputSlot, smeltResult);
		}
		else
		{
			currentSmelted.stackSize += smeltResult.stackSize;
		}

		decrStackSize(1, 1);
	}

	private boolean canSmelt()
	{
		ItemStack toSmelt = inventory[1];

		if (toSmelt == null)
		{
			return false;
		}

		ItemStack smeltResult = FurnaceRecipes.smelting().getSmeltingResult(toSmelt);
		if (smeltResult == null)
		{
			return false;
		}

		ItemStack currentSmelted = getStackInSlot(outputSlot);

		if (currentSmelted == null)
		{
			return true;
		}
		if (!smeltResult.isItemEqual(currentSmelted))
		{
			return false;
		}

		int result = currentSmelted.stackSize + smeltResult.stackSize;
		return result <= currentSmelted.getMaxStackSize();
	}

	private int getItemBurnTime(ItemStack stack)
	{
		int val = TileEntityFurnace.getItemBurnTime(stack);
		return (val * ticksBeforeSmelt) / 200 * efficiencyBonus;
	}
}
