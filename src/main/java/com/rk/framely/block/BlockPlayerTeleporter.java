package com.rk.framely.block;

import com.rk.framely.init.ModItems;
import com.rk.framely.item.ItemLinker;
import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityFrameTeleporter;
import com.rk.framely.tileentity.TileEntityPlayerTeleporter;
import com.rk.framely.util.Pos;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.UUID;

public class BlockPlayerTeleporter extends BlockBase implements ITileEntityProvider {

    public BlockPlayerTeleporter() {
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_PLAYER_TELEPORTER_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityPlayerTeleporter();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityPlayerTeleporter) {
                TileEntityPlayerTeleporter tileEntityPlayerTeleporter = (TileEntityPlayerTeleporter) tile;
                ItemStack stack = player.getHeldItem();
                if(stack != null && stack.getItem() == ModItems.linker) {
                    NBTTagCompound tag = stack.getTagCompound();
                    if(tag != null && tag.hasKey("type")) {
                        if(tag.getInteger("type") == ItemLinker.TeleporterType.Player.ordinal()) {
                            if(tileEntityPlayerTeleporter.setUUID(new UUID(tag.getLong("uuidMSB"), tag.getLong("uuidLSB")))) {
                                //uuid set
                                player.addChatMessage(new ChatComponentText("PlayerTeleporter-Link established!"));
                            } else {
                                //uuid not set
                                player.addChatMessage(new ChatComponentText("PlayerTeleporter-Link not established!"));
                            }
                        }
                    } else {
                        if(tag == null) tag = new NBTTagCompound();
                        tag.setLong("uuidMSB", tileEntityPlayerTeleporter.getUuid().getMostSignificantBits());
                        tag.setLong("uuidLSB", tileEntityPlayerTeleporter.getUuid().getLeastSignificantBits());
                        Pos pos = tileEntityPlayerTeleporter.getPosition();
                        tag.setIntArray("pos",  new int[] {pos.x, pos.y, pos.z});
                        tag.setInteger("type", ItemLinker.TeleporterType.Player.ordinal());
                        tag.setString("dim", world.getWorldInfo().getWorldName());
                        stack.setTagCompound(tag);
                        player.addChatMessage(new ChatComponentText("Saved PlayerTeleporter position!"));
                    }
                } else {
                    tileEntityPlayerTeleporter.teleport();
                }
            }
            return true;
        }
    }
}
