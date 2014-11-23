package com.rk.framely.block;

import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityEngine;
import com.rk.framely.tileentity.TileEntityFrame;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFrame extends BlockFrameBase implements ITileEntityProvider {

    public BlockFrame() {
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_FRAME_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityFrame();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if(world.isRemote) {
            return true;
        }
        if(player.isSneaking()) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if(tileEntity instanceof TileEntityFrame) {
                ((TileEntityFrame)tileEntity).removeSideTexture(side);
            }
        } else {
            ItemStack stack = player.getHeldItem();
            if(stack == null) return true;
            Block block = Block.getBlockFromItem(player.getHeldItem().getItem());

            //blacklist blocks
            if(block == Blocks.air) return true;
            if(!block.isOpaqueCube()) return true;
            if(block instanceof BlockColored) return true;

            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if(tileEntity instanceof TileEntityFrame) {
                ((TileEntityFrame)tileEntity).setSideTexture(side, Block.getIdFromBlock(block), world.getBlockMetadata(x, y, z));
            }
        }
        return true;
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity tileEntity = blockAccess.getTileEntity(x, y, z);
        if(tileEntity instanceof TileEntityFrame) {
            return ((TileEntityFrame)tileEntity).getSideTexture(side);
        }
        return blockIcon;
    }

}
