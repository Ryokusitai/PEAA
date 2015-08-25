package peaa.config;

import java.io.File;

import peaa.PEAACore;
import net.minecraftforge.common.config.Configuration;

public class PEAAConfig
{
	public static boolean registerArchangelSmiteRecipe;
	public static boolean isHighSpeedMoveWhenLanding;

	public static void init(File configFile)
	{
		Configuration config = new Configuration(configFile);

		try
		{
			config.load();

			registerArchangelSmiteRecipe = config.getBoolean("enableArchangelSmiteRecipe", "recipes", false, "can create ArchangelSmiteRecipe in survival");
			isHighSpeedMoveWhenLanding = config.getBoolean("enableHighSpeedMovementAbilityWhenLanding", "spaceRing", false, "enable GemFeet's High-Speed movement ability when landing");
		}
		catch (Exception e)
		{
			PEAACore.logger.info("コンフィグファイルのロードに失敗しました");
			e.printStackTrace();
		}
		finally
		{
			if (config.hasChanged())
			{
				config.save();
			}
		}
	}

}
