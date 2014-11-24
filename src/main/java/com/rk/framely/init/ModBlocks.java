package com.rk.framely.init;

import com.rk.framely.block.BlockEngine;
import com.rk.framely.block.BlockFrame;
import com.rk.framely.block.BlockFrameManager;
import com.rk.framely.block.BlockTeleporter;
import com.rk.framely.reference.Reference;
import cpw.mods.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModBlocks {

    public static final BlockFrame frame = new BlockFrame();
    public static final BlockEngine engine = new BlockEngine();
    public static final BlockFrameManager frameManager = new BlockFrameManager();
    public static final BlockTeleporter teleporter = new BlockTeleporter();

    public static void init() {
        GameRegistry.registerBlock(frame, Reference.BLOCK_FRAME_NAME);
        GameRegistry.registerBlock(engine, Reference.BLOCK_ENGINE_NAME);
        GameRegistry.registerBlock(frameManager, Reference.BLOCK_FRAME_MANAGER_NAME);
        GameRegistry.registerBlock(teleporter, Reference.BLOCK_TELEPORTER_NAME);
    }
}
