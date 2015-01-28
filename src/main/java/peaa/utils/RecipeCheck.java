package peaa.utils;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

/**
 *
 * @author Ryokusitai
 *こちらもレシピ書き換えの勉強用にオリジナルの一部をコピペ&書き換えてメソッドを追加したもの
 *もったいないので残しているだけです
 */
public class RecipeCheck
{
	public static ShapedRecipes createDataRecipe(ItemStack p_92103_1_, Object ... p_92103_2_)
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (p_92103_2_[i] instanceof String[])
        {
            String[] astring = (String[])((String[])p_92103_2_[i++]);

            for (int l = 0; l < astring.length; ++l)
            {
                String s1 = astring[l];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }
        else
        {
            while (p_92103_2_[i] instanceof String)
            {
                String s2 = (String)p_92103_2_[i++];
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }

        HashMap hashmap;

        for (hashmap = new HashMap(); i < p_92103_2_.length; i += 2)
        {
            Character character = (Character)p_92103_2_[i];
            ItemStack itemstack1 = null;

            if (p_92103_2_[i + 1] instanceof Item)
            {
                itemstack1 = new ItemStack((Item)p_92103_2_[i + 1]);
            }
            else if (p_92103_2_[i + 1] instanceof Block)
            {
                itemstack1 = new ItemStack((Block)p_92103_2_[i + 1], 1, 32767);
            }
            else if (p_92103_2_[i + 1] instanceof ItemStack)
            {
                itemstack1 = (ItemStack)p_92103_2_[i + 1];
            }

            hashmap.put(character, itemstack1);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1)
        {
            char c0 = s.charAt(i1);

            if (hashmap.containsKey(Character.valueOf(c0)))
            {
                aitemstack[i1] = ((ItemStack)hashmap.get(Character.valueOf(c0))).copy();
            }
            else
            {
                aitemstack[i1] = null;
            }
        }

        ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, p_92103_1_);
        return shapedrecipes;
    }

	public static boolean checkMatchRecipe(ShapedRecipes recipe, ShapedRecipes recipe2)
	{
		//System.out.println("start check");
		if (recipe.getRecipeOutput().getItem() != recipe2.getRecipeOutput().getItem()) {
			return false;
		}
		System.out.println("pass out put");

		if (recipe.getRecipeOutput().stackSize != recipe2.getRecipeOutput().stackSize) {
			return false;
		}
		System.out.println("pass stackSize");

		for (int i = 0;  i < recipe.recipeItems.length; i++)
		{
			if (recipe.recipeItems[i].getItem() != recipe2.recipeItems[i].getItem())
				return false;
		}
		return true;
	}
}
