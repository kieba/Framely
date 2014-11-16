package com.rk.framely.init;

import com.rk.framely.block.BlockFrame;
import com.rk.framely.reference.Reference;
import cpw.mods.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModBlocks {

    public static final BlockFrame frame = new BlockFrame();

    public static void init() {
        GameRegistry.registerBlock(frame, "frame");
    }
}
