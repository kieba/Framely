package com.rk.framely.block;

import com.rk.framely.reference.Reference;
import net.minecraft.block.material.Material;

public class BlockFrameManager extends BlockBase {

    public BlockFrameManager(){
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_FRAME_MANAGER_NAME);
    }
}
