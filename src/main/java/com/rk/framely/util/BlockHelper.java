package com.rk.framely.util;

import codechicken.multipart.MultipartHelper;
import codechicken.multipart.TileMultipart;
import com.rk.framely.Framely;
import cpw.mods.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import javax.swing.text.html.parser.Entity;

public class BlockHelper {

    public static boolean moveBlock(World world, int srcX, int srcY, int srcZ, ForgeDirection dir) {
        return moveBlock(world, srcX, srcY, srcZ, srcX + dir.offsetX, srcY + dir.offsetY, srcZ + dir.offsetZ);
    }

    public static boolean moveBlock(World world, int srcX, int srcY, int srcZ, int destX, int destY, int destZ) {
        Block b = world.getBlock(srcX, srcY, srcZ);

        int meta = world.getBlockMetadata(srcX, srcY, srcZ);

        if(b.equals(Blocks.air) || !world.getBlock(destX, destY, destZ).isReplaceable(world, destX, destY, destZ)) {
            return false;
        }

        TileEntity tile = world.getTileEntity(srcX, srcY, srcZ);
        NBTTagCompound tag = new NBTTagCompound();

        if(tile != null) {
            tile.writeToNBT(tag);
        }

        world.setBlock(destX, destY, destZ, b, meta, 3);
        world.setBlockMetadataWithNotify(destX, destY, destZ, meta, 3);

        if (tile != null) {
            TileEntity newTile = TileEntity.createAndLoadEntity(tag);

            if(Framely.isFMPLoaded && isMultipart(tile)) {
                newTile = MultipartHelper.createTileFromNBT(world, tag);
            }

            world.setTileEntity(destX, destY, destZ, newTile);
            newTile.xCoord = destX;
            newTile.yCoord = destY;
            newTile.zCoord = destZ;

            if(Framely.isFMPLoaded && isMultipart(tile)) {
                MultipartHelper.sendDescPacket(world, newTile);
            }
        }

        world.removeTileEntity(srcX, srcY, srcZ);
        world.setBlockToAir(srcX, srcY, srcZ);
        return true;
    }

    @Optional.Method(modid = "ForgeMultipart")
    public static boolean isMultipart(TileEntity tile) {
        return tile instanceof TileMultipart;
    }

}
