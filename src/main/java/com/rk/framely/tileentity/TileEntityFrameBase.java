package com.rk.framely.tileentity;

import com.rk.framely.util.Pos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityFrameBase extends TileEntity {
    public Pos relativeFrameManagerPos = null;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey("relativeFrameManagerPos")){
            int[] tmp = tag.getIntArray("relativeFrameManagerPos");
            relativeFrameManagerPos = new Pos(tmp[0],tmp[1],tmp[2]);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(relativeFrameManagerPos != null){
            tag.setIntArray("relativeFrameManagerPos", new int[]{relativeFrameManagerPos.x, relativeFrameManagerPos.y, relativeFrameManagerPos.z});
        }
    }

    public void registerFrameManager(Pos posFrameManager){
        relativeFrameManagerPos = new Pos(posFrameManager.x - xCoord,posFrameManager.y-yCoord,posFrameManager.z-zCoord);
    }

    public void removeFrameManager(){
        relativeFrameManagerPos = null;
    }

    public TileEntityFrameManager getFrameManager(){
        if(relativeFrameManagerPos != null)
            return (TileEntityFrameManager)worldObj.getTileEntity(relativeFrameManagerPos.x + xCoord,relativeFrameManagerPos.y + yCoord,relativeFrameManagerPos.z + zCoord);

        return null;
    }

    public void onBlockRemoved(){
        TileEntityFrameManager frameManager = getFrameManager();
        if(frameManager!= null)
            frameManager.onBlockRemovedFromConstruction();
    }
}
