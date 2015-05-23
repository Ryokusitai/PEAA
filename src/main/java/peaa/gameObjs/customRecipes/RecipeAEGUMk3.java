package peaa.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import peaa.gameObjs.ObjHandlerPEAA;

public class RecipeAEGUMk3 extends ShapedRecipes
{
	private static final ItemStack fullKleinOmega = new ItemStack(ObjHandler.kleinStars, 1, 5);

    public static final int recipeWidth = 3;
    public static final int recipeHeight = 3;
    public final ItemStack[] recipeItems;
	private static ItemStack output = new ItemStack(ObjHandlerPEAA.aeguMK3_off);

	private static ItemStack mk2 = new ItemStack(ObjHandlerPEAA.aeguMK2_off);
	private static ItemStack[] stack = {mk2, mk2, mk2, mk2, fullKleinOmega, mk2, mk2, mk2, mk2};


	public RecipeAEGUMk3()
	{
		super(recipeWidth, recipeHeight, stack, output);
		KleinStar.setEmc(fullKleinOmega, EMCHelper.getKleinStarMaxEmc(fullKleinOmega));
		recipeItems = stack;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		/*if (this.checkMatch(inv, 0, 0, true))
		{
			return true;
		}
		if (this.checkMatch(inv, 0, 0, false))
		{
			return true;
		}

		return false;*/
		if (inv.getSizeInventory() < 9) { return false;}
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack input = inv.getStackInSlot(i);
			if (input == null)
			{
				return false;
			}

			if (i == 4)
			{
				if (!(ItemStack.areItemStacksEqual(input, fullKleinOmega)
						&& ItemStack.areItemStackTagsEqual(input, fullKleinOmega)))
				{
					return false;
				}
			} else if (!(input.getItem() == Item.getItemFromBlock(ObjHandlerPEAA.aeguMK2_off))) {
				return false;
			}
			System.out.println("call this");
		}
		//output = new ItemStack(ObjHandlerPEAA.aeguMK3_off);
		return true;
	}

	private boolean checkMatch(InventoryCrafting inv, int i, int j, boolean unknown)
	{
		System.out.println(i + " : " + j);
		for (int k = 0; k < 3; k++)
		{
			for (int l = 0; l < 3; l++)
			{
				int i1 = k - i;
				int j1 = l - j;
				ItemStack itemstack = null;
				System.out.println(i1 + " : " + j1 + " : " + recipeWidth + " : " + recipeHeight);
				if (i1 >= 0 && j1 >= 0 && i1 < this.recipeWidth && j1 < this.recipeHeight)
				{
					System.out.println("in");
					if (unknown)
					{
						itemstack = this.recipeItems[this.recipeWidth - j1 - 1 + j1 * this.recipeWidth];
					}
					else
					{
						itemstack = this.recipeItems[i1 + j1 * this.recipeWidth];
					}
				}

				ItemStack itemstack1 = inv.getStackInRowAndColumn(k, l);
System.out.println(itemstack1);
				if (itemstack1 != null || itemstack != null)
				{
					System.out.println(itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null);
					if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null)
					{
						return false;
					}

					System.out.println(itemstack.getItem() != itemstack1.getItem());
					if (itemstack.getItem() != itemstack1.getItem())
					{
						return false;
					}

					System.out.println(itemstack.getItemDamage() != 32767 && itemstack.getItemDamage() != itemstack1.getItemDamage());
					if (itemstack.getItemDamage() != 32767 && itemstack.getItemDamage() != itemstack1.getItemDamage())
					{
						return false;
					}
				}
			}
		}
		System.out.println("true");
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting p_77572_1_)
	{
		return this.getRecipeOutput().copy();
	}

	@Override
	public int getRecipeSize()
	{
		return this.recipeWidth * this.recipeHeight;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return output;
	}

	public static ItemStack getKleinStack()
	{
		if (KleinStar.getEmc(fullKleinOmega) != EMCHelper.getKleinStarMaxEmc(fullKleinOmega))
			KleinStar.setEmc(fullKleinOmega, EMCHelper.getKleinStarMaxEmc(fullKleinOmega));

		return fullKleinOmega;
	}
}
