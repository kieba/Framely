package com.rk.framely.init;

import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityEngine;
import com.rk.framely.tileentity.TileEntityFrame;
import com.rk.framely.tileentity.TileEntityFrameBase;
import com.rk.framely.tileentity.TileEntityFrameManager;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModTileEntities {

    public static void init() {
        GameRegistry.registerTileEntityWithAlternatives(TileEntityEngine.class, Reference.BLOCK_ENGINE_NAME, "tile." +  Reference.BLOCK_ENGINE_NAME);
        GameRegistry.registerTileEntityWithAlternatives(TileEntityFrameManager.class, Reference.BLOCK_FRAME_MANAGER_NAME, "tile." +  Reference.BLOCK_FRAME_MANAGER_NAME);
        GameRegistry.registerTileEntityWithAlternatives(TileEntityFrame.class, Reference.BLOCK_FRAME_NAME, "tile." +  Reference.BLOCK_FRAME_NAME);
    }
}
