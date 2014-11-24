package com.rk.framely.block;

import com.rk.framely.init.ModItems;
import com.rk.framely.item.ItemLinker;
import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityFrameTeleporter;
import com.rk.framely.util.Pos;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.UUID;

public class BlockFrameTeleporter extends BlockBase implements ITileEntityProvider {

    public BlockFrameTeleporter() {
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_FRAME_TELEPORTER_NAME);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityFrameTeleporter) {
                TileEntityFrameTeleporter tileEntityFrameTeleporter = (TileEntityFrameTeleporter) tile;
                ItemStack stack = player.getHeldItem();
                if(stack != null && stack.getItem() == ModItems.linker) {
                    NBTTagCompound tag = stack.getTagCompound();
                    if(tag != null && tag.hasKey("type")) {
                        if(tag.getInteger("type") == ItemLinker.TeleporterType.Frame.ordinal()) {
                            if(tileEntityFrameTeleporter.setUUID(new UUID(tag.getLong("uuidMSB"), tag.getLong("uuidLSB")))) {
                                //uuid set
                                player.addChatMessage(new ChatComponentText("FrameTeleporter-Link established!"));
                            } else {
                                //uuid not set
                                player.addChatMessage(new ChatComponentText("FrameTeleporter-Link not established!"));
                            }
                        }
                    } else {
                        if(tag == null) tag = new NBTTagCompound();
                        tag.setLong("uuidMSB", tileEntityFrameTeleporter.getUuid().getMostSignificantBits());
                        tag.setLong("uuidLSB", tileEntityFrameTeleporter.getUuid().getLeastSignificantBits());
                        Pos pos = tileEntityFrameTeleporter.getPosition();
                        tag.setIntArray("pos",  new int[] {pos.x, pos.y, pos.z});
                        tag.setInteger("type", ItemLinker.TeleporterType.Frame.ordinal());
                        tag.setString("dim", world.getWorldInfo().getWorldName());
                        stack.setTagCompound(tag);
                        player.addChatMessage(new ChatComponentText("Saved FrameTeleporter position!"));
                    }
                } else {
                    tileEntityFrameTeleporter.teleport();
                }
            }
            return true;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityFrameTeleporter();
    }
}
