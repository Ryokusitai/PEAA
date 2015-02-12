package peaa.asm;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

public class PEAACoreContainer extends DummyModContainer
{
	public PEAACoreContainer() {
		super(new ModMetadata());

		ModMetadata meta = getMetadata();
		meta.modId = "PEAACore";
		meta.name = "PEAACore";
		meta.version = "1.3.0";
		meta.authorList = Arrays.asList("ryokusitai");
		meta.description = "PEAACore Mod";
		meta.url = "";
        meta.credits = "";

		this.setEnabledState(true);
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
}
