package peaa.eaemc;

import ic2.api.item.IC2Items;

import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EAEMC
{
	private static boolean isLoadBCTp = false;
	private static boolean isLoadBCSl = false;
	private static boolean isLoadIC2 = false;

	/**
	 * ic2のAPIを参考にしました。
	 *
	 * 雛形↓
	 * ProjectEAPI.registerCustomEMC(IC2Items.getItem(""), );
	 * ↑
	 */
	public static void extendedRegistrationEMC() {
		checkLoad();

		// バニラの板ガラスを追加(1EMC)
		ProjectEAPI.registerCustomEMC(new ItemStack(Blocks.glass_pane, 1), 1);

		if (EAEMC.isLoadBCTp) {
			try {
				Class<?> bcTp = Class.forName("buildcraft.BuildCraftTransport");

				Item item = null;
				Object ret = bcTp.getField("pipeWire").get(null);
				if (ret instanceof Item)
					item = (Item)ret;

				ProjectEAPI.registerCustomEMC(new ItemStack(item, 1, 0), 840);
				ProjectEAPI.registerCustomEMC(new ItemStack(item, 1, 1), 2960);
				ProjectEAPI.registerCustomEMC(new ItemStack(item, 1, 2), 840);
				ProjectEAPI.registerCustomEMC(new ItemStack(item, 1, 3), 840);
				// ゲートにEMCを設定するには EMC登録の為の項目にNBTまで取得して判断するようにしないといけないかも
				//ProjectEAPI.registerCustomEMC(ItemGate.makeGateItem(GateDefinition.GateMaterial.GOLD, GateDefinition.GateLogic.AND), 1);
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		if (EAEMC.isLoadBCSl) {
			try {
				Class bcSl = Class.forName("buildcraft.BuildCraftSilicon");

				Item item = null;
				Object ret = bcSl.getField("redstoneChipset").get(null);
				if (ret instanceof Item)
					item = (Item)ret;

				ProjectEAPI.registerCustomEMC(new ItemStack(item, 1, 0), 2560);
				ProjectEAPI.registerCustomEMC(new ItemStack(item, 1, 1), 6400);
				ProjectEAPI.registerCustomEMC(new ItemStack(item, 1, 2), 42240);
				ProjectEAPI.registerCustomEMC(new ItemStack(item, 1, 3), 165120);
				ProjectEAPI.registerCustomEMC(new ItemStack(item, 1, 4), 21760);
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		if (EAEMC.isLoadIC2) {
			// 合金
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("mixedMetalIngot"), 2016);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("advancedAlloy"), 2016);

			// プレート
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("platecopper"), 128);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("platetin"),  256);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("platebronze"), 160);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("plategold"), 2048);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("plateiron"), 256);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("platelead"), 512);

			// ケーシング
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("casingcopper"), 64);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("casingtin"), 128);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("casingbronze"), 80);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("casinggold"), 1024);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("casingiron"), 128);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("casinglead"), 256);

			// ケーブル
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("copperCableItem"), 64);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("tinCableItem"), 85);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("goldCableItem"), 512);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("ironCableItem"), 64);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("insulatedCopperCableItem"), 96);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("insulatedTinCableItem"), 117);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("insulatedGoldCableItem"), 576);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("insulatedIronCableItem"), 160);

			// 強化ガラス・石材
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("reinforcedGlass"), 4039);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("reinforcedStone"), 4039);

			// 電池
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("reBattery"), 757);

			// セル
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("cell"), 85);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("lavaCell"), 149);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("waterCell"), 85);

			// フェンス
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("ironFence"), 128);

			// 足場
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("scaffold"), 9);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("ironScaffold"), 120);

			// ツリータップ
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("treetap"), 40);

			// ラバーシート
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("rubberSapling"), 64);

			// コーヒー
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("coffeeBeans"), 64);
			ProjectEAPI.registerCustomEMC(IC2Items.getItem("coffeePowder"), 64);

		}

	}

	private static void checkLoad()
	{
		for (ModContainer mod : Loader.instance().getModList()) {
			if (mod.getModId().equals("BuildCraft|Transport"))
				EAEMC.isLoadBCTp = true;
			if (mod.getModId().equals("BuildCraft|Silicon"))
				EAEMC.isLoadBCSl = true;
			if (mod.getModId().equals("IC2"))
				EAEMC.isLoadIC2 = true;
		}

	}
}