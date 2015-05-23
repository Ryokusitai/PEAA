package peaa.network;

import peaa.PEAACore;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandlerPEAA
{
	// このMOD用のSimpleNetworkWrapperを作成。チャンネルの文字列は固有であれば何でも良い。MODIDの利用を推奨。
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(PEAACore.MODID);

	public static void init()
	{
		// IMessageHandlerクラスとMessageクラスの登録
		INSTANCE.registerMessage(IsFlyingModeSyncPKTHandlerToClient.class, IsFlyingModeSyncPKT.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(IsFlyingModeSyncPKTHandlerToServer.class, IsFlyingModeSyncPKT.class, 0, Side.SERVER);
	}

}
