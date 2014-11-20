package com.rk.framely.block;

import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityEngine;
import com.rk.framely.tileentity.TileEntityFrameManager;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFrameManager extends BlockFrameBase implements ITileEntityProvider {

    public BlockFrameManager(){
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_FRAME_MANAGER_NAME);
    }


    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityFrameManager();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityFrameManager) {
                ((TileEntityFrameManager) tile).onBlockActivated();
            }
            return true;
        }
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int p_149725_5_) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityFrameManager) {
            ((TileEntityFrameManager) tile).onBlockRemoved();
        }
    }
}
