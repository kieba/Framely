package com.rk.framely.tileentity;

import com.rk.framely.network.MessageEngine;
import com.rk.framely.network.PacketHandler;
import com.rk.framely.util.ConstructionHelper;
import com.rk.framely.util.Pos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityEngine extends TileEntityFrameBase {

    private ForgeDirection dir = ForgeDirection.NORTH;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        dir = ForgeDirection.values()[tag.getInteger("dir")];
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("dir", dir.ordinal());
    }

    public void setDir(ForgeDirection dir) {
        this.dir = dir;
    }

    public ForgeDirection getDir() {
        return dir;
    }

    public void onBlockActivated() {
        //ConstructionHelper.moveConstruction(worldObj, new Pos(xCoord, yCoord, zCoord), dir);
        if(relativeFrameManagerPos==null) return;
        TileEntity entity = worldObj.getTileEntity(relativeFrameManagerPos.x + xCoord, relativeFrameManagerPos.y + yCoord, relativeFrameManagerPos.z + zCoord);

        if(entity instanceof TileEntityFrameManager){
            TileEntityFrameManager frameManager = (TileEntityFrameManager)entity;
            frameManager.moveConstruction(dir);
        }

    }

    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.INSTANCE.getPacketFrom(new MessageEngine(this));
    }

}
