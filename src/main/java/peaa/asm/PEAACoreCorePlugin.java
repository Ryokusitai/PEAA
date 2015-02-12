package peaa.asm;

import java.util.Map;
import java.util.logging.Logger;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class PEAACoreCorePlugin implements IFMLLoadingPlugin
{

	public static Logger logger = Logger.getLogger("PEAACore");

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"peaa.asm.PEAACoreTransformer", "peaa.asm.MK2TextureTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return "peaa.asm.PEAACoreContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
