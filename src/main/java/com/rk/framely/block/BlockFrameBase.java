package com.rk.framely.block;

import com.rk.framely.tileentity.TileEntityFrameBase;
import com.rk.framely.util.CreativeTabFramely;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockFrameBase extends BlockBase {
    private Block block;

    public BlockFrameBase(Material m) {
        super(m);
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int p_149725_5_) {
        TileEntityFrameBase frameBase = (TileEntityFrameBase)world.getTileEntity(x,y,z);
        frameBase.onBlockRemoved();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntityFrameBase frameBase = (TileEntityFrameBase)world.getTileEntity(x,y,z);
        frameBase.onNeighborBlockChange(world,x,y,z,block);
    }
}
