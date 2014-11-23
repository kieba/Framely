package com.rk.framely.tileentity;

import com.rk.framely.init.ModBlocks;
import com.rk.framely.network.MessageFrame;
import com.rk.framely.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.IIcon;

public class TileEntityFrame extends TileEntityFrameBase {

    public int[] blockIds = new int[] {
            Block.getIdFromBlock(ModBlocks.frame),
            Block.getIdFromBlock(ModBlocks.frame),
            Block.getIdFromBlock(ModBlocks.frame),
            Block.getIdFromBlock(ModBlocks.frame),
            Block.getIdFromBlock(ModBlocks.frame),
            Block.getIdFromBlock(ModBlocks.frame)
    };

    public int[] metadata = new int[] {0, 0, 0, 0, 0, 0 };
    private IIcon[] sideTextures = new IIcon[6];

    public void setSideTexture(int side, int blockId, int metadata) {
        blockIds[side] = blockId;
        this.metadata[side] = metadata;
        sideTextures[side] = null;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public IIcon getSideTexture(int side) {
        if(sideTextures[side] == null) {
            sideTextures[side] = Block.getBlockById(blockIds[side]).getIcon(side, metadata[side]);
        }
        return sideTextures[side];
    }

    public void removeSideTexture(int side) {
        blockIds[side] = Block.getIdFromBlock(ModBlocks.frame);
        metadata[side] = 0;
        sideTextures[side] = null;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void resetSideTextures() {
        for (int i = 0; i < 6; i++) {
            sideTextures[i] = null;
        }
        if(worldObj.isRemote) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        blockIds = tag.getIntArray("blockIds");
        metadata = tag.getIntArray("metadata");
        for (int i = 0; i < 6; i++) {
            sideTextures[i] = null;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setIntArray("blockIds", blockIds);
        tag.setIntArray("metadata", metadata);
    }

    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.INSTANCE.getPacketFrom(new MessageFrame(this));
    }

}
