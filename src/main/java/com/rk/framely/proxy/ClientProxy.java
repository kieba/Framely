package com.rk.framely.proxy;


import com.rk.framely.client.renderer.TileEntityFrameManagerRenderer;
import com.rk.framely.client.renderer.WorldRendererFrameManager;
import com.rk.framely.tileentity.TileEntityFrameManager;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    public static int renderIdFrameManager;
    private static WorldRendererFrameManager worldRendererFrameManager = new WorldRendererFrameManager();

    @Override
    public void init() {
        renderIdFrameManager = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(renderIdFrameManager, worldRendererFrameManager);

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFrameManager.class, new TileEntityFrameManagerRenderer());
    }
}
