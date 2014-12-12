package com.rk.framely.tileentity;

import codechicken.multipart.MultipartHelper;
import codechicken.multipart.TileMultipart;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import com.rk.framely.Framely;
import com.rk.framely.block.BlockFrameBase;
import com.rk.framely.network.IPacketReceiver;
import com.rk.framely.network.MessageFrameManager;
import com.rk.framely.network.PacketHandler;
import com.rk.framely.network.PacketTileSimpleAction;
import com.rk.framely.util.Pos;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class TileEntityFrameManager extends TileEntityFrameBase implements IPacketReceiver, IEnergyHandler {

    private static final int ENERGY_PER_BLOCK = 160;

    public static int ANIMATION_TICKS = 20;
    public ForgeDirection direction;
    public int tick = 0;
    public boolean move = false;
    public boolean showConstructionGrid = false;
    public boolean sendAnimationStartMessage = false;
    private EnergyStorage storage = new EnergyStorage(ENERGY_PER_BLOCK * 1024, 10000, Integer.MAX_VALUE);

    public List<Pos> relativeConstruction = new ArrayList<Pos>();

    @Override
    public void updateEntity() {
        if(move) {
            tick++;
            if(tick >= ANIMATION_TICKS) {
                tick = 0;
                /* move the relativeConstruction (server side only) */
                if(!worldObj.isRemote) {
                    move(worldObj, xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
                }
                move = false;
            }
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        double minX = 0;
        double minY = 0;
        double minZ = 0;
        double maxX = 0;
        double maxY = 0;
        double maxZ = 0;
        for (Pos p : relativeConstruction) {
            if(p.x < minX) minX = p.x;
            if(p.x > maxX) maxX = p.x;

            if(p.y < minY) minY = p.y;
            if(p.y > maxY) maxY = p.y;

            if(p.z < minZ) minZ = p.z;
            if(p.z > maxZ) maxZ = p.z;
        }
        minX += this.xCoord;
        minY += this.yCoord;
        minZ += this.zCoord;
        maxX += this.xCoord + 1;
        maxY += this.yCoord + 1;
        maxZ += this.zCoord + 1;
        return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private boolean isOwnFrameManager(){
        return relativeFrameManagerPos.equals(Pos.NULL);
    }

    private void unRegisterFrameManager(){
        if(!relativeConstruction.isEmpty()) {
            for (int i = 0; i < relativeConstruction.size(); i++) {
                TileEntity entity = worldObj.getTileEntity(relativeConstruction.get(i).x + xCoord, relativeConstruction.get(i).y + yCoord, relativeConstruction.get(i).z + zCoord);
                if (entity instanceof TileEntityFrameBase) {
                    TileEntityFrameBase base = (TileEntityFrameBase) entity;
                    base.removeFrameManager();
                }
            }
            relativeConstruction.clear();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public boolean isBlockInConstruction(int x, int y, int z){
        Pos realtivePos = new Pos(x-xCoord, y-yCoord, z-zCoord);
        for(Pos pos:relativeConstruction){
            if(pos.equals(realtivePos)){
                return true;
            }
        }
        return false;
    }

    public void onBlockRemovedFromConstruction(){
        unRegisterFrameManager();
    }

    public void onConstructionChanged(){
        if(!isOwnFrameManager()) return;
        unRegisterFrameManager();

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

    public int getEnergyPerMovement() {
        return ENERGY_PER_BLOCK * relativeConstruction.size();
    }

    /**
     * srcPos is the position of the engine
     * @param dir
     * @return true if the relativeConstruction is could be moved
     */
    public boolean moveConstruction(ForgeDirection dir) {
        if(!worldObj.isRemote) { //server side only
            if(move) return false;
            this.direction = dir;
            this.move = true;

            /* check if relativeConstruction is movable */
            if(!isMovable(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)) {
                this.move = false;
                return false;
            }

            int energyNeeded = getEnergyPerMovement();
            if(storage.getEnergyStored() < energyNeeded) {
                this.move = false;
                return false;
            }

            storage.extractEnergy(energyNeeded, false);
            sendAnimationStartMessage = true;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

            return true;
        }
        return false;
    }

    public boolean teleport(World destWorld, int destX, int destY, int destZ) {
        if(worldObj.isRemote) return false;

        if(move) return false;

        if(!isMovable(destWorld, destX, destY, destZ)) {
            return false;
        }
        move(destWorld, destX, destY, destZ);
        return true;
    }

    private void visitBlock(List<Pos> construction, int x, int y, int z) {
        Block b = worldObj.getBlock(x, y, z);

        /* don't add the block if its an air block or if the block is replaceable (e.g. water, lava, grass, ...) */
        if(b.isAir(worldObj, x, y, z) || b.isReplaceable(worldObj, x, y, z) || b == Blocks.bedrock) {
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
     * @return
     */
    private boolean isMovable(World world, int destX, int destY, int destZ) {
        boolean isMovable = true;
        for (int i = 0; i < relativeConstruction.size(); i++) {
            if(!isMovable(world, relativeConstruction.get(i), destX, destY, destZ)) {
                isMovable = false;
                break;
            }
        }
        return isMovable;
    }

    private boolean isMovable(World world, Pos p, int destX, int destY, int destZ) {
        Pos newPos = new Pos(destX + p.x, destY + p.y, destZ + p.z);
        Block b = world.getBlock(newPos.x, newPos.y, newPos.z);
        if(b.isReplaceable(world, newPos.x, newPos.y, newPos.z)) {
            return true;
        }
        Pos tmp = new Pos(newPos.x - xCoord, newPos.y - yCoord, newPos.z - zCoord);
        if(worldObj == world && relativeConstruction.contains(tmp)) {
            return true;
        }
        return false;
    }

    private void move(World world, int destX, int destY, int destZ) {
        ArrayList<MovableBlock> blockArrayList = new ArrayList<MovableBlock>();

        /* save old state and remove tile entities*/
        for (int i = 0; i < relativeConstruction.size(); i++) {
            Pos relPos = relativeConstruction.get(i);
            Pos oldPos = new Pos(xCoord + relPos.x, yCoord + relPos.y, zCoord + relPos.z);
            Pos newPos = new Pos(destX + relPos.x, destY + relPos.y, destZ + relPos.z);
            MovableBlock mb = getBlock(oldPos, newPos, world);
            blockArrayList.add(mb);
            //worldObj.removeTileEntity(mb.oldPos.x, mb.oldPos.y, mb.oldPos.z);
        }

        ArrayList<MovableBlock> airPosList = new ArrayList<MovableBlock>();

        /* add air blocks to the position where no new block will be added, only the old block removed */
        int size = blockArrayList.size();
        for (int i = 0; i < size; i++) {
            boolean addAirBlock = true;
            MovableBlock mb = blockArrayList.get(i);
            Pos oldPos = mb.oldPos;
            worldObj.setBlock(oldPos.x, oldPos.y, oldPos.z, Blocks.air, 0, 0);

            if(world == worldObj) {
                if(oldPos == null) continue;
                boolean add = true;
                for (int j = 0; j < blockArrayList.size(); j++) {
                    Pos newPos = blockArrayList.get(j).newPos;
                    if(oldPos.equals(newPos)) {
                        add = false;
                        break;
                    }
                }
                if(add) airPosList.add(mb);
            }


            /*if(addAirBlock) {
                MovableBlock mb = new MovableBlock();
                mb.newPos = oldPos;
                mb.block = Blocks.air;
                mb.oldBlock = blockArrayList.get(i).block;
                mb.meta = 0;
                mb.newWorld = worldObj;
                blockArrayList.add(mb);
            }*/
        }

        /* place new blocks */
        for (int i = 0; i < blockArrayList.size(); i++) {
            placeBlock(blockArrayList.get(i));
        }

        /* place new blocks (this time we place blocks that couldn't be placed in the first loop, like RedstoneTorches */
        for (int i = 0; i < blockArrayList.size(); i++) {
            placeBlock(blockArrayList.get(i));
        }

        /* update all blocks */
        for (int i = 0; i < blockArrayList.size(); i++) {
            MovableBlock mb = blockArrayList.get(i);
            Chunk c = mb.newWorld.getChunkFromBlockCoords(mb.newPos.x, mb.newPos.z);
            mb.newWorld.markAndNotifyBlock(mb.newPos.x, mb.newPos.y, mb.newPos.z, c, mb.oldBlock, mb.block, 3);
            if(mb.tileEntity != null) {
                mb.tileEntity.setWorldObj(mb.newWorld);
                mb.tileEntity.xCoord = mb.newPos.x;
                mb.tileEntity.yCoord = mb.newPos.y;
                mb.tileEntity.zCoord = mb.newPos.z;
                mb.newWorld.setTileEntity(mb.newPos.x, mb.newPos.y, mb.newPos.z, mb.tileEntity );
            }


            /*TileEntity newTile = world.getTileEntity(mb.newPos.x, mb.newPos.y, mb.newPos.z);
            if(Framely.isFMPLoaded && isMultipart(mb.tileEntity)) {
               MultipartHelper.sendDescPacket(mb.newWorld, newTile);
            }
            */
        }

        for (int i = 0; i < airPosList.size(); i++) {
            MovableBlock mb = airPosList.get(i);
            Chunk c = worldObj.getChunkFromBlockCoords(mb.oldPos.x, mb.oldPos.z);
            worldObj.markAndNotifyBlock(mb.oldPos.x, mb.oldPos.y, mb.oldPos.z, c, mb.block, Blocks.air, 3);
        }

        for (int i = 0; i < blockArrayList.size(); i++) {
            MovableBlock mb = blockArrayList.get(i);
            TileEntity te = world.getTileEntity(mb.newPos.x, mb.newPos.y, mb.newPos.z);
            if(te instanceof TileEntityFrameBase) {
                ((TileEntityFrameBase) te).registerFrameManager(new Pos(destX, destY, destZ));
            }
        }

    }

    private MovableBlock getBlock(Pos oldPos, Pos newPos, World newWorld) {
        MovableBlock mb = new MovableBlock();
        mb.oldPos = oldPos;
        mb.newPos = newPos;
        mb.oldBlock = worldObj.getBlock(mb.newPos.x, mb.newPos.y, mb.newPos.z);
        mb.block = worldObj.getBlock(oldPos.x, oldPos.y, oldPos.z);
        mb.meta = worldObj.getBlockMetadata(oldPos.x, oldPos.y, oldPos.z);
        mb.tileEntity = worldObj.getTileEntity(oldPos.x, oldPos.y, oldPos.z);
        if(mb.tileEntity != null) {
            if(mb.tileEntity instanceof TileEntityFrameBase) {
                ((TileEntityFrameBase) mb.tileEntity).removeFrameManager();
            }
            NBTTagCompound tag = new NBTTagCompound();
            mb.tileEntity.writeToNBT(tag);
            mb.tagCompound = tag;
        }
        mb.newWorld = newWorld;
        mb.placed = false;
        return mb;
    }

    private void placeBlock(MovableBlock mb) {
        if(!mb.placed) {
            if(mb.block.canPlaceBlockAt(mb.newWorld, mb.newPos.x, mb.newPos.y, mb.newPos.z)) {
                mb.newWorld.setBlock(mb.newPos.x, mb.newPos.y, mb.newPos.z, mb.block, mb.meta, 0);
                mb.newWorld.setBlockMetadataWithNotify(mb.newPos.x, mb.newPos.y, mb.newPos.z, mb.meta, 0);

                if (mb.tileEntity != null) {
                    TileEntity newTile = TileEntity.createAndLoadEntity(mb.tagCompound);

                    if(Framely.isFMPLoaded && isMultipart(mb.tileEntity)) {
                        newTile = MultipartHelper.createTileFromNBT(mb.newWorld, mb.tagCompound);
                    }

                    mb.newWorld.setTileEntity(mb.newPos.x, mb.newPos.y, mb.newPos.z, newTile);
                    newTile.xCoord = mb.newPos.x;
                    newTile.yCoord = mb.newPos.y;
                    newTile.zCoord = mb.newPos.z;
                }

                mb.placed = true;
            }
        }
    }

    @cpw.mods.fml.common.Optional.Method(modid = "ForgeMultipart")
    private boolean isMultipart(TileEntity tile) {
        return tile instanceof TileMultipart;
    }

    @Override
    public void ReceiveClientData(com.rk.framely.network.Packet packet) {

    }

    @Override
    public void ReceiveServerData(com.rk.framely.network.Packet packet) {
        if(packet instanceof PacketTileSimpleAction){
            PacketTileSimpleAction ptsa = (PacketTileSimpleAction) packet;
            if(ptsa.command.equals("buildConstruct")){
                onConstructionChanged();
            }
            if(ptsa.command.equals("removeConstruct")){
                unRegisterFrameManager();
            }
            if(ptsa.command.equals("grid")){
                showConstructionGrid = !showConstructionGrid;
                worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
            }
        }

    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    private class MovableBlock {
        private Pos oldPos;
        private Pos newPos;
        private Block block;
        private Block oldBlock; /* the block at the new pos (before this block has been placed)*/
        private TileEntity tileEntity;
        private NBTTagCompound tagCompound;
        private int meta;
        private World newWorld;
        private boolean placed;
    }


    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        relativeConstruction = new ArrayList<Pos>();
        int[] tmp = tag.getIntArray("relativeConstruction");
        for(int i = 0; i<tmp.length;i+=3){
            relativeConstruction.add(new Pos(tmp[i], tmp[i + 1], tmp[i + 2]));
        }
        storage.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        int[] tmp = new int[relativeConstruction.size()*3];
        for(int i = 0; i< relativeConstruction.size();i++){
            tmp[i*3] = relativeConstruction.get(i).x;
            tmp[i*3+1] = relativeConstruction.get(i).y;
            tmp[i*3+2] = relativeConstruction.get(i).z;
        }
        tag.setIntArray("relativeConstruction", tmp);
        storage.writeToNBT(tag);
    }

    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.INSTANCE.getPacketFrom(new MessageFrameManager(this));
    }

}
