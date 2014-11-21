package com.rk.framely.tileentity;

import codechicken.multipart.MultipartHelper;
import codechicken.multipart.TileMultipart;
import com.rk.framely.Framely;
import com.rk.framely.block.BlockFrameBase;
import com.rk.framely.network.MessageEngine;
import com.rk.framely.network.MessageFrameManager;
import com.rk.framely.network.PacketHandler;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pos;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class TileEntityFrameManager extends TileEntityFrameBase{
    public List<Pos> relativeConstruction;

    public void onBlockActivated() {
        onConstructionChanged();
    }

    public void onBlockRemoved(){
        if(relativeConstruction!=null) {
            for (int i = 0; i < relativeConstruction.size(); i++) {
                TileEntity entity = worldObj.getTileEntity(relativeConstruction.get(i).x + xCoord, relativeConstruction.get(i).y + yCoord, relativeConstruction.get(i).z + zCoord);
                if (entity instanceof TileEntityFrameBase) {
                    TileEntityFrameBase base = (TileEntityFrameBase) entity;
                    base.removeFrameManager();
                }
            }
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public void onConstructionChanged(){
        List<Pos> tmpPos = new ArrayList<Pos>();
        visitBlock(tmpPos, xCoord, yCoord, zCoord);

        for(int i = 0; i<tmpPos.size();i++){
            TileEntity entity = worldObj.getTileEntity(tmpPos.get(i).x,tmpPos.get(i).y,tmpPos.get(i).z);
            if(entity instanceof TileEntityFrameBase){
                TileEntityFrameBase base = (TileEntityFrameBase) entity;
                base.registerFrameManager(new Pos(xCoord,yCoord,zCoord));
            }

            tmpPos.get(i).x -= xCoord;
            tmpPos.get(i).y -= yCoord;
            tmpPos.get(i).z -= zCoord;
        }
        relativeConstruction = tmpPos;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

    }

    private String debugOut(List<Pos> list){
        String tmp = "";
        for(int i = 0; i<list.size();i++){
            tmp += "("+list.get(i).x + "," + list.get(i).y + ","+ list.get(i).z + "),";
        }
        return "{" + tmp + "}";
    }


    /**
     * srcPos is the position of the engine
     * @param dir
     * @return true if the relativeConstruction is could be moved
     */
    public boolean moveConstruction(ForgeDirection dir) {
        /* check if relativeConstruction is movable */
        if(!isMovable(dir)) {
            return false;
        }

        /* move the relativeConstruction */
        move(dir);

        return true;
    }

    private void visitBlock(List<Pos> construction, int x, int y, int z) {
        Block b = worldObj.getBlock(x, y, z);

        /* don't add the block if its an air block or if the block is replaceable (e.g. water, lava, grass, ...) */
        if(b.isAir(worldObj, x, y, z) || b.isReplaceable(worldObj, x, y, z)) {
            return;
        }

        //TODO: check blacklist blocks!

        Pos p = new Pos(x, y, z);
        if(construction.contains(p)) return;

        construction.add(p);
        if(b instanceof BlockFrameBase) {
            for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                visitBlock(construction, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            }
        }
    }

    /**
     * check all positions if they can be moved
     * @param dir
     * @return
     */
    private boolean isMovable(ForgeDirection dir) {
        boolean isMovable = true;
        for (int i = 0; i < relativeConstruction.size(); i++) {
            Pos absolutPos = relativeConstruction.get(i).clone();
            absolutPos.x += xCoord;
            absolutPos.y += yCoord;
            absolutPos.z += zCoord;
            if(!isMovable(absolutPos, dir)) {
                isMovable = false;
                break;
            }
        }
        return isMovable;
    }

    private boolean isMovable(Pos p, ForgeDirection dir) {
        Pos newPos = new Pos(p.x + dir.offsetX, p.y + dir.offsetY, p.z + dir.offsetZ);
        Block b = worldObj.getBlock(newPos.x, newPos.y, newPos.z);
        if(b.isReplaceable(worldObj, newPos.x, newPos.y, newPos.z)) {
            return true;
        }
        if(relativeConstruction.contains(new Pos(newPos.x - xCoord, newPos.y - yCoord, newPos.z - zCoord))) {
            return true;
        }
        return false;
    }

    private void move(ForgeDirection dir) {
        ArrayList<MovableBlock> blockArrayList = new ArrayList<MovableBlock>();

        /* save old state and remove tile entities*/
        for (int i = 0; i < relativeConstruction.size(); i++) {
            Pos absolutPos = relativeConstruction.get(i).clone();
            absolutPos.x += xCoord;
            absolutPos.y += yCoord;
            absolutPos.z += zCoord;
            MovableBlock mb = getBlock(absolutPos, dir);
            blockArrayList.add(mb);

            worldObj.removeTileEntity(mb.oldPos.x, mb.oldPos.y, mb.oldPos.z);
        }

        /* add air blocks to the position where no new block will be added, only the old block removed */
        for (int i = 0; i < blockArrayList.size(); i++) {
            boolean addAirBlock = true;
            Pos oldPos = blockArrayList.get(i).oldPos;
            if(oldPos == null) continue;

            for (int j = 0; j < blockArrayList.size(); j++) {
                Pos newPos = blockArrayList.get(j).newPos;
                if(oldPos.equals(newPos)) {
                    addAirBlock = false;
                    break;
                }
            }

            if(addAirBlock) {
                MovableBlock mb = new MovableBlock();
                mb.newPos = oldPos;
                mb.block = Blocks.air;
                mb.oldBlock = blockArrayList.get(i).block;
                mb.meta = 0;
                blockArrayList.add(mb);
            }
        }

        /* place new blocks */
        for (int i = 0; i < blockArrayList.size(); i++) {
            placeBlock(worldObj, blockArrayList.get(i));
        }

        /* update all blocks */
        for (int i = 0; i < blockArrayList.size(); i++) {
            MovableBlock mb = blockArrayList.get(i);
            Chunk c = worldObj.getChunkFromBlockCoords(mb.newPos.x, mb.newPos.z);
            worldObj.markAndNotifyBlock(mb.newPos.x, mb.newPos.y, mb.newPos.z, c, mb.oldBlock, mb.block, 3);
        }
    }

    private MovableBlock getBlock(Pos p, ForgeDirection dir) {
        MovableBlock mb = new MovableBlock();
        mb.oldPos = p;
        mb.newPos = new Pos(p.x + dir.offsetX, p.y + dir.offsetY, p.z + dir.offsetZ);
        mb.oldBlock = worldObj.getBlock(mb.newPos.x, mb.newPos.y, mb.newPos.z);
        mb.block = worldObj.getBlock(p.x, p.y, p.z);
        mb.meta = worldObj.getBlockMetadata(p.x, p.y, p.z);
        mb.tileEntity = worldObj.getTileEntity(p.x, p.y, p.z);
        if(mb.tileEntity != null) {
            NBTTagCompound tag = new NBTTagCompound();
            mb.tileEntity.writeToNBT(tag);
            mb.tagCompound = tag;
        }
        return mb;
    }

    private void placeBlock(World world, MovableBlock mb) {
        world.setBlock(mb.newPos.x, mb.newPos.y, mb.newPos.z, mb.block, mb.meta, 0);
        world.setBlockMetadataWithNotify(mb.newPos.x, mb.newPos.y, mb.newPos.z, mb.meta, 0);

        if (mb.tileEntity != null) {
            TileEntity newTile = TileEntity.createAndLoadEntity(mb.tagCompound);

            if(Framely.isFMPLoaded && isMultipart(mb.tileEntity)) {
                newTile = MultipartHelper.createTileFromNBT(world, mb.tagCompound);
            }

            world.setTileEntity(mb.newPos.x, mb.newPos.y, mb.newPos.z, newTile);
            newTile.xCoord = mb.newPos.x;
            newTile.yCoord = mb.newPos.y;
            newTile.zCoord = mb.newPos.z;

            if(Framely.isFMPLoaded && isMultipart(mb.tileEntity)) {
                MultipartHelper.sendDescPacket(world, newTile);
            }
        }
    }

    @cpw.mods.fml.common.Optional.Method(modid = "ForgeMultipart")
    private boolean isMultipart(TileEntity tile) {
        return tile instanceof TileMultipart;
    }

    private class MovableBlock {
        private Pos oldPos;
        private Pos newPos;
        private Block block;
        private Block oldBlock; /* the block at the new pos (before this block has been placed)*/
        private TileEntity tileEntity;
        private NBTTagCompound tagCompound;
        private int meta;
    }


    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        relativeConstruction = new ArrayList<Pos>();

        int[] tmp = tag.getIntArray("relativeConstruction");
        for(int i = 0; i<tmp.length;i+=3){
            relativeConstruction.add(new Pos(tmp[i], tmp[i + 1], tmp[i + 2]));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(relativeConstruction != null){
            int[] tmp = new int[relativeConstruction.size()*3];
            for(int i = 0; i< relativeConstruction.size();i++){
                tmp[i*3] = relativeConstruction.get(i).x;
                tmp[i*3+1] = relativeConstruction.get(i).y;
                tmp[i*3+2] = relativeConstruction.get(i).z;
            }
            tag.setIntArray("relativeConstruction", tmp);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.INSTANCE.getPacketFrom(new MessageFrameManager(this));
    }

}
