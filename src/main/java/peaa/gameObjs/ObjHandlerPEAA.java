package peaa.gameObjs;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemRelayBlock;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import peaa.config.PEAAConfig;
import peaa.gameObjs.blocks.AEGU;
import peaa.gameObjs.blocks.CollectorPEAA;
import peaa.gameObjs.customRecipes.RecipeAEGUMk3;
import peaa.gameObjs.items.RingFlightTeleport;
import peaa.gameObjs.tiles.CollectorMK4Tile;
import peaa.gameObjs.tiles.CollectorMK5Tile;
import cpw.mods.fml.common.registry.GameRegistry;

public class ObjHandlerPEAA {
	public static Block collectorMK4 = new CollectorPEAA(4);
	public static Block collectorMK5 = new CollectorPEAA(5);

	public static Block aeguMK1_off = new AEGU(1, false);
	public static Block aeguMK1_on  = new AEGU(1, true);
	public static Block aeguMK2_off = new AEGU(2, false);
	public static Block aeguMK2_on  = new AEGU(2, true);
	public static Block aeguMK3_off = new AEGU(3, false);
	public static Block aeguMK3_on  = new AEGU(3, true);

	public static Item ringSpaceTeleport = new RingFlightTeleport();

	public static void register()
	{
		// Blocks
		GameRegistry.registerBlock(collectorMK4, ItemRelayBlock.class, "Collector MK4");
		GameRegistry.registerBlock(collectorMK5, ItemRelayBlock.class, "Collector MK5");

		GameRegistry.registerBlock(aeguMK1_off, "AEGU MK1");
		GameRegistry.registerBlock(aeguMK1_on , "AEGU MK1_on");
		GameRegistry.registerBlock(aeguMK2_off, "AEGU MK2");
		GameRegistry.registerBlock(aeguMK2_on , "AEGU MK2_on");
		GameRegistry.registerBlock(aeguMK3_off, "AEGU MK3");
		GameRegistry.registerBlock(aeguMK3_on , "AEGU MK3_on");

		// TileEntities
		GameRegistry.registerTileEntity(CollectorMK4Tile.class, "Energy Collector MK4 Tile");
		GameRegistry.registerTileEntity(CollectorMK5Tile.class, "Energy Collector MK5 Tile");

		// Items
		GameRegistry.registerItem(ringSpaceTeleport, "ring_space_teleport");

	}

	public static void addRecipes()
	{
		// レシピ
		GameRegistry.addRecipe(new ItemStack(collectorMK4), "CCC", "CMC", "CCC", 'C', ObjHandler.collectorMK3, 'M', new ItemStack(ObjHandler.matter, 1, 1));
		GameRegistry.addRecipe(new ItemStack(collectorMK5), "CCC", "CMC", "CCC", 'C', ObjHandlerPEAA.collectorMK4, 'M', new ItemStack(ObjHandler.matter, 1, 1));

		GameRegistry.addRecipe(new ItemStack(aeguMK1_off), "CCC", "CPC", "CCC", 'C', ObjHandler.collectorMK3, 'P', ObjHandler.dmPedestal);
		GameRegistry.addRecipe(new ItemStack(aeguMK2_off), "AAA", "ASA", "AAA", 'A', aeguMK1_off, 'S', new ItemStack(ObjHandler.kleinStars, 1, 4));
		GameRegistry.addRecipe(new RecipeAEGUMk3());
		RecipeSorter.register("Ultimate AEGU Recipes", RecipeAEGUMk3.class, Category.SHAPED, "");

		// 司空の指輪
		GameRegistry.addShapelessRecipe(new ItemStack(ringSpaceTeleport),
				new ItemStack(ObjHandler.blackHole, 1), new ItemStack(ObjHandler.eternalDensity, 1),
				new ItemStack(ObjHandler.swrg, 1), new ItemStack(ObjHandler.matterBlock, 1, 1), new ItemStack(ObjHandler.matter, 1, 1), new ItemStack(ObjHandler.matter, 1, 1),
				new ItemStack(ObjHandler.matter, 1, 1), new ItemStack(ObjHandler.matter, 1, 1), new ItemStack(ObjHandler.matter, 1, 1));

		// 大天使の指輪 ダークマターではなくレッドマターを要求するようにレシピ変更
		if (PEAAConfig.registerArchangelSmiteRecipe)
		{
			GameRegistry.addRecipe(new ItemStack(ObjHandler.angelSmite), "AFA", "RIR", "AFA", 'A', Items.bow, 'F', Items.feather, 'R', new ItemStack(ObjHandler.matter, 1, 1),
					'I', ObjHandler.ironBand);
		}

	}
}
