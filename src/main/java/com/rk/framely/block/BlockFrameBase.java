package com.rk.framely.block;

import com.rk.framely.tileentity.TileEntityFrameBase;
import com.rk.framely.util.CreativeTabFramely;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public abstract class BlockFrameBase extends BlockBase {
    public BlockFrameBase(Material m) {
        super(m);
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int p_149725_5_) {
        TileEntityFrameBase frameBase = (TileEntityFrameBase)world.getTileEntity(x,y,z);
        frameBase.onBlockRemoved();
    }
}
