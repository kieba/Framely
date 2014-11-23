package com.rk.framely.network;

import com.rk.framely.reference.Reference;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toLowerCase());

    public static void init() {
        INSTANCE.registerMessage(MessageEngine.class, MessageEngine.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(MessageFrame.class, MessageFrame.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(MessageFrameManager.class, MessageFrameManager.class, 2, Side.CLIENT);
    }

}
