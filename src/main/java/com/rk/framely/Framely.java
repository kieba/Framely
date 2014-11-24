package com.rk.framely;

import com.rk.framely.handler.ConfigurationHandler;
import com.rk.framely.handler.GuiHandler;
import com.rk.framely.init.ModBlocks;
import com.rk.framely.init.ModItems;
import com.rk.framely.init.ModTileEntities;
import com.rk.framely.network.PacketHandler;
import com.rk.framely.proxy.IProxy;
import com.rk.framely.reference.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid= Reference.MOD_ID, name=Reference.MOD_NAME, version=Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
public class Framely {

    public static final boolean DEBUG = true;
    public static boolean isFMPLoaded;

    public static GuiHandler guiHandler = new GuiHandler();

    public static PacketHandler packetHandler;

    @Mod.Instance(Reference.MOD_ID)
    public static Framely INSTANCE;

    @SidedProxy(clientSide= Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());


        PacketHandler.init();

        ModBlocks.init();

        ModItems.init();

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());
        packetHandler = new PacketHandler();
        ModTileEntities.init();
        PROXY.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.isFMPLoaded = Loader.isModLoaded("ForgeMultipart");
    }
}
