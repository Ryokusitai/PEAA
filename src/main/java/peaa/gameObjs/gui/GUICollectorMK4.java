package peaa.gameObjs.gui;

import moze_intel.projecte.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import peaa.PEAACore;
import peaa.gameObjs.container.CollectorMK4Container;
import peaa.gameObjs.tiles.CollectorMK4Tile;

public class GUICollectorMK4 extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PEAACore.MODID.toLowerCase(), "textures/gui/collector3.png");
	private CollectorMK4Tile tile;

	public GUICollectorMK4(InventoryPlayer invPlayer, CollectorMK4Tile tile)
	{
		super(new CollectorMK4Container(invPlayer, tile));
		this.tile = tile;
		this.xSize = 218;
		this.ySize = 165;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2)
	{
		this.fontRendererObj.drawString(Integer.toString(tile.displayEmc), 91, 32, 4210752);

		double kleinCharge = tile.displayItemCharge;

		if (kleinCharge != -1)
			this.fontRendererObj.drawString(Constants.EMC_FORMATTER.format(kleinCharge), 91, 44, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;

		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		//Ligh Level. Max is 12
		int progress = tile.getSunLevelScaled(12);
		this.drawTexturedModalRect(x + 160, y + 49 - progress, 220, 13 - progress, 12, progress);

		//EMC storage. Max is 48
		progress = tile.getEmcScaled(48);
		this.drawTexturedModalRect(x + 98, y + 18, 0, 166, progress, 10);

		//Klein Star Charge Progress. Max is 48
		progress = tile.getKleinStarChargeScaled(48);
		this.drawTexturedModalRect(x + 98, y + 58, 0, 166, progress, 10);

		//Fuel Progress. Max is 24.
		progress = tile.getFuelProgressScaled(24);
		this.drawTexturedModalRect(x + 172, y + 55 - progress, 219, 38 - progress, 10, progress + 1);
	}
}

