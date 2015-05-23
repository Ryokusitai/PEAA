package peaa.config;

import java.io.File;

import peaa.PEAACore;
import net.minecraftforge.common.config.Configuration;

public class PEAAConfig
{
	public static boolean registerArchangelSmiteRecipe;

	public static void init(File configFile)
	{
		Configuration config = new Configuration(configFile);

		try
		{
			config.load();

			registerArchangelSmiteRecipe = config.getBoolean("enableArchangelSmiteRecipe", "recipes", false, "can create ArchangelSmiteRecipe in survival");
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
