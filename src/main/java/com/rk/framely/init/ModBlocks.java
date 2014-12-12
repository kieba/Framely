package com.rk.framely.init;

import com.rk.framely.block.*;
import com.rk.framely.item.ItemBlockTeleporter;
import com.rk.framely.reference.Reference;
import cpw.mods.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModBlocks {

    public static final BlockFrame frame = new BlockFrame();
    public static final BlockEngine engine = new BlockEngine();
    public static final BlockFrameManager frameManager = new BlockFrameManager();
    public static final BlockTeleporter teleporter = new BlockTeleporter();

    public static void init() {
        //GameRegistry.registerBlock(frame, Reference.BLOCK_FRAME_NAME);
        //GameRegistry.registerBlock(engine, Reference.BLOCK_ENGINE_NAME);
        //GameRegistry.registerBlock(frameManager, Reference.BLOCK_FRAME_MANAGER_NAME);
        GameRegistry.registerBlock(teleporter, ItemBlockTeleporter.class, Reference.BLOCK_TELEPORTER_NAME);
    }
}
