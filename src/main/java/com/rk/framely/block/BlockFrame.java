package com.rk.framely.block;

import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityEngine;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFrame extends BlockFrameBase implements ITileEntityProvider {

    public BlockFrame() {
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_FRAME_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityEngine();
    }
}
