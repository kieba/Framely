package com.rk.framely.tileentity;

import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pos;
import net.minecraft.block.Block;
import net.minecraft.client.stream.IngestServerTester;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityFrameBase extends TileEntityBase {

    public Pos relativeFrameManagerPos = Pos.NULL;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        int[] tmp = tag.getIntArray("relativeFrameManagerPos");
        relativeFrameManagerPos = new Pos(tmp[0],tmp[1],tmp[2]);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setIntArray("relativeFrameManagerPos", new int[]{relativeFrameManagerPos.x, relativeFrameManagerPos.y, relativeFrameManagerPos.z});
    }

    public void registerFrameManager(Pos posFrameManager){
        relativeFrameManagerPos = new Pos(posFrameManager.x - xCoord,posFrameManager.y-yCoord,posFrameManager.z-zCoord);
    }

    public void removeFrameManager(){
        relativeFrameManagerPos = Pos.NULL;
    }

    public TileEntityFrameManager getFrameManager(){
        if(!relativeFrameManagerPos.equals(Pos.NULL)){
            TileEntity tileEntity = worldObj.getTileEntity(relativeFrameManagerPos.x + xCoord,relativeFrameManagerPos.y + yCoord,relativeFrameManagerPos.z + zCoord);
            if(tileEntity != null && tileEntity instanceof TileEntityFrameManager){
                TileEntityFrameManager frameManager = (TileEntityFrameManager) tileEntity;
                return frameManager;
            }
        }
        return null;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
        TileEntityFrameManager frameManager = getFrameManager();
        if(frameManager==null) return;
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if(frameManager.isBlockInConstruction(x+dir.offsetX,y+dir.offsetY,z+dir.offsetZ)
                    && world.isAirBlock(x+dir.offsetX,y+dir.offsetY,z+dir.offsetZ)
                    && !frameManager.move
                    && !world.isRemote){
                LogHelper.info("Move: " + frameManager.move);
                LogHelper.info("Block: " + x + " " + y + " " + z);
                LogHelper.info("BlockProblem: " + (x+dir.offsetX) + " " + (y+dir.offsetY) + " " + (z+dir.offsetZ) + " " + block.getLocalizedName());
                frameManager.onBlockRemovedFromConstruction();
            }
        }
    }

    public void onBlockRemoved(){
        TileEntityFrameManager frameManager = getFrameManager();
        if(frameManager!= null && !frameManager.move)
            frameManager.onBlockRemovedFromConstruction();
    }
}
