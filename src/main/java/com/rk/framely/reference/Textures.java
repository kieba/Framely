package com.rk.framely.reference;


import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

public class Textures {
    public static final String GUI_PATH = "textures/gui/";
    public static ResourceLocation frameManagerGui = Textures.getLocation(GUI_PATH + "frameManager.png");



    public static ResourceLocation getLocation(String path) {
        return new ResourceLocation(Reference.MOD_ID.toLowerCase(), path);
    }

    @SideOnly(Side.CLIENT)
    public static void loadTexture(String path) {
        loadTexture(Textures.getLocation(path));
    }

    @SideOnly(Side.CLIENT)
    public static void loadTexture(ResourceLocation location) {
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(location);
    }
}
