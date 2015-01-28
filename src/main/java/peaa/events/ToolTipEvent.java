package peaa.events;

import peaa.gameObjs.ObjHandlerPEAA;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ToolTipEvent
{
	@SuppressWarnings("unused")
	@SubscribeEvent
	public void tTipEvent(ItemTooltipEvent event) {
		ItemStack current = event.itemStack;
		Item currentItem = current.getItem();
		Block currentBlock = Block.getBlockFromItem(currentItem);

		if (current == null) {
			return;
		}

		/**
		 * collectorMK4/MK5 ToolTip
		 */
		if (currentBlock == ObjHandlerPEAA.collectorMK4)
		{
			event.toolTip.add("Generation Rate: 320 EMC/s");
			event.toolTip.add("Max Storage: 60000 EMC");
		}

		if (currentBlock == ObjHandlerPEAA.collectorMK5)
		{
			event.toolTip.add("Generation Rate: 1280 EMC/s");
			event.toolTip.add("Max Storage: 60000 EMC");
		}

		/**
		 * AEGU ToolTip
		 */
		if (currentBlock == ObjHandlerPEAA.aeguMK1_off)
		{
			event.toolTip.add("Generation Rate: 40 EMC/s");
		}

		if (currentBlock == ObjHandlerPEAA.aeguMK2_off)
		{
			event.toolTip.add("Generation Rate: 1000 EMC/s");
		}

		if (currentBlock == ObjHandlerPEAA.aeguMK3_off)
		{
			event.toolTip.add("Generation Rate: 20000 EMC/s");
		}
	}

}
