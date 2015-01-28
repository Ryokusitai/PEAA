package peaa.gameObjs;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemRelayBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import peaa.gameObjs.blocks.AEGU;
import peaa.gameObjs.blocks.CollectorPEAA;
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

	public static Item kleinStarVertex = new Item()
			.setCreativeTab(ObjHandler.cTab)
			.setUnlocalizedName("KleinStarVertex")
			.setTextureName("peaa:klein_star_vertex");

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
		GameRegistry.registerItem(kleinStarVertex, "klein_star_vartex");

	}

	public static void addRecipes()
	{
		// レシピ
		GameRegistry.addRecipe(new ItemStack(collectorMK4), "CCC", "CMC", "CCC", 'C', ObjHandler.collectorMK3, 'M', new ItemStack(ObjHandler.matter, 1, 1));
		GameRegistry.addRecipe(new ItemStack(collectorMK5), "CCC", "CMC", "CCC", 'C', ObjHandlerPEAA.collectorMK4, 'M', new ItemStack(ObjHandler.matter, 1, 1));

		GameRegistry.addRecipe(new ItemStack(aeguMK1_off), "CCC", "CFC", "CCC", 'C', ObjHandler.collectorMK3, 'F', ObjHandler.rmFurnaceOff);
		GameRegistry.addRecipe(new ItemStack(aeguMK2_off), "AAA", "AVA", "AAA", 'A', aeguMK1_off, 'V', new ItemStack(ObjHandler.kleinStars, 1, 4));
		GameRegistry.addRecipe(new ItemStack(aeguMK3_off), "AAA", "AVA", "AAA", 'A', aeguMK2_off, 'V', kleinStarVertex);

		GameRegistry.addShapelessRecipe(new ItemStack(kleinStarVertex), new ItemStack(ObjHandler.kleinStars, 1, 5), new ItemStack(ObjHandler.kleinStars, 1, 5), new ItemStack(ObjHandler.kleinStars, 1, 5));

	}
}
