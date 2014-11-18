package com.rk.framely.block;

import com.rk.framely.reference.Reference;
import net.minecraft.block.material.Material;

public class BlockFrame extends BlockBase {

    public BlockFrame() {
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_FRAME_NAME);
        this.setBlockTextureName(Reference.BLOCK_FRAME_NAME);
    }
}
