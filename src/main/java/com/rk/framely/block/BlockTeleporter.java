package com.rk.framely.block;

import com.rk.framely.init.ModItems;
import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityEngine;
import com.rk.framely.tileentity.TileEntityFrameManager;
import com.rk.framely.tileentity.TileEntityTeleporter;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.UUID;

public class BlockTeleporter extends BlockBase implements ITileEntityProvider {

    public BlockTeleporter() {
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_TELEPORTER_NAME);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityTeleporter) {
                TileEntityTeleporter tileEntityTeleporter = (TileEntityTeleporter) tile;
                ItemStack stack = player.getHeldItem();
                if(stack != null && stack.getItem() == ModItems.linker) {
                    NBTTagCompound tag = stack.getTagCompound();
                    if(tag != null && tag.hasKey("uuidMSB")) {
                        if(tileEntityTeleporter.setUUID(new UUID(tag.getLong("uuidMSB"), tag.getLong("uuidLSB")))) {
                            //uuid set
                            player.addChatMessage(new ChatComponentText("Teleporter-Link established!"));
                        } else {
                            //uuid not set
                            player.addChatMessage(new ChatComponentText("Teleporter-Link not established!"));
                        }
                    } else {
                        if(tag == null) tag = new NBTTagCompound();
                        tag.setLong("uuidMSB", tileEntityTeleporter.getUuid().getMostSignificantBits());
                        tag.setLong("uuidLSB", tileEntityTeleporter.getUuid().getLeastSignificantBits());
                        Pos pos = tileEntityTeleporter.getPosition();
                        tag.setIntArray("pos",  new int[] {pos.x, pos.y, pos.z});
                        stack.setTagCompound(tag);
                        player.addChatMessage(new ChatComponentText("Saved teleporter position!"));
                    }
                } else {
                    tileEntityTeleporter.teleport();
                }
            }
            return true;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityTeleporter();
    }
}
