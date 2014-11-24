package com.rk.framely.init;

import com.rk.framely.block.*;
import com.rk.framely.reference.Reference;
import cpw.mods.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModBlocks {

    public static final BlockFrame frame = new BlockFrame();
    public static final BlockEngine engine = new BlockEngine();
    public static final BlockFrameManager frameManager = new BlockFrameManager();
    public static final BlockFrameTeleporter frameTeleporter = new BlockFrameTeleporter();
    public static final BlockPlayerTeleporter playerTeleporter = new BlockPlayerTeleporter();

    public static void init() {
        GameRegistry.registerBlock(frame, Reference.BLOCK_FRAME_NAME);
        GameRegistry.registerBlock(engine, Reference.BLOCK_ENGINE_NAME);
        GameRegistry.registerBlock(frameManager, Reference.BLOCK_FRAME_MANAGER_NAME);
        GameRegistry.registerBlock(frameTeleporter, Reference.BLOCK_FRAME_TELEPORTER_NAME);
        GameRegistry.registerBlock(playerTeleporter, Reference.BLOCK_PLAYER_TELEPORTER_NAME);
    }
}
