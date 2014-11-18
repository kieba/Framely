package com.rk.framely.init;

import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityEngine;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModTileEntities {

    public static void init() {
        GameRegistry.registerTileEntityWithAlternatives(TileEntityEngine.class, Reference.BLOCK_ENGINE_NAME, "tile." +  Reference.BLOCK_ENGINE_NAME);
    }
}
