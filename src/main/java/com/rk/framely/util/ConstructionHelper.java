package com.rk.framely.util;

import codechicken.multipart.MultipartHelper;
import codechicken.multipart.TileMultipart;
import com.rk.framely.Framely;
import com.rk.framely.block.BlockEngine;
import com.rk.framely.block.BlockFrame;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class ConstructionHelper {

    /**
     * srcPos is the position of the engine
     * @param world
     * @param srcPos
     * @param dir
     * @return true if the construction is could be moved
     */
    public static boolean moveConstruction(World world, Pos srcPos, ForgeDirection dir) {

        /* build construction */
        List<Pos> construction = new ArrayList<Pos>();
        visitBlock(construction, world, srcPos.x, srcPos.y, srcPos.z);

        /* check if construction is movable */
        if(!isMovable(construction, world, dir)) {
            return false;
        }

        /* move the construction */
        move(construction, world, dir);

        return true;
    }

    private static void visitBlock(List<Pos> construction, World world, int x, int y, int z) {
        Block b = world.getBlock(x, y, z);

        /* don't add the block if its an air block or if the block is replaceable (e.g. water, lava, grass, ...) */
        if(b.isAir(world, x, y, z) || b.isReplaceable(world, x, y, z)) {
            return;
        }

        //TODO: check blacklist blocks!

        Pos p = new Pos(x, y, z);
        if(construction.contains(p)) return;

        construction.add(p);
        if(b instanceof BlockFrame || b instanceof BlockEngine) {
            for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                visitBlock(construction, world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            }
        }
    }

    /**
     * check all positions if they can be moved
     * @param dir
     * @return
     */
    private static boolean isMovable(List<Pos> construction, World world, ForgeDirection dir) {
        boolean isMovable = true;
        for (int i = 0; i < construction.size(); i++) {
            if(!isMovable(construction, world, construction.get(i), dir)) {
                isMovable = false;
                break;
            }
        }
        return isMovable;
    }

    private static boolean isMovable(List<Pos> construction, World world, Pos p, ForgeDirection dir) {
        Pos newPos = new Pos(p.x + dir.offsetX, p.y + dir.offsetY, p.z + dir.offsetZ);
        Block b = world.getBlock(newPos.x, newPos.y, newPos.z);
        if(b.isReplaceable(world, newPos.x, newPos.y, newPos.z)) {
            return true;
        }
        if(construction.contains(newPos)) {
            return true;
        }
        return false;
    }

    private static void move(List<Pos> construction, World world, ForgeDirection dir) {
        ArrayList<MovableBlock> blockArrayList = new ArrayList<MovableBlock>();

        /* save old state and remove tile entities*/
        for (int i = 0; i < construction.size(); i++) {
            MovableBlock mb = getBlock(world, construction.get(i), dir);
            blockArrayList.add(mb);

            world.removeTileEntity(mb.oldPos.x, mb.oldPos.y, mb.oldPos.z);
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
            placeBlock(world, blockArrayList.get(i));
        }

        /* update all blocks */
        for (int i = 0; i < blockArrayList.size(); i++) {
            MovableBlock mb = blockArrayList.get(i);
            Chunk c = world.getChunkFromBlockCoords(mb.newPos.x, mb.newPos.z);
            world.markAndNotifyBlock(mb.newPos.x, mb.newPos.y, mb.newPos.z, c, mb.oldBlock, mb.block, 3);
        }
    }

    private static MovableBlock getBlock(World world, Pos p, ForgeDirection dir) {
        MovableBlock mb = new MovableBlock();
        mb.oldPos = p;
        mb.newPos = new Pos(p.x + dir.offsetX, p.y + dir.offsetY, p.z + dir.offsetZ);
        mb.oldBlock = world.getBlock(mb.newPos.x, mb.newPos.y, mb.newPos.z);
        mb.block = world.getBlock(p.x, p.y, p.z);
        mb.meta = world.getBlockMetadata(p.x, p.y, p.z);
        mb.tileEntity = world.getTileEntity(p.x, p.y, p.z);
        if(mb.tileEntity != null) {
            NBTTagCompound tag = new NBTTagCompound();
            mb.tileEntity.writeToNBT(tag);
            mb.tagCompound = tag;
        }
        return mb;
    }

    private static void placeBlock(World world, MovableBlock mb) {
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
    private static boolean isMultipart(TileEntity tile) {
        return tile instanceof TileMultipart;
    }

    private static class MovableBlock {
        private Pos oldPos;
        private Pos newPos;
        private Block block;
        private Block oldBlock; /* the block at the new pos (before this block has been placed)*/
        private TileEntity tileEntity;
        private NBTTagCompound tagCompound;
        private int meta;
    }
}
