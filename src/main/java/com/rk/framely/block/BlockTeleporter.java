package com.rk.framely.block;

import com.rk.framely.init.ModItems;
import com.rk.framely.item.ItemLinker;
import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityEngine;
import com.rk.framely.tileentity.TileEntityTeleporter;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sun.rmi.runtime.Log;

import java.util.List;
import java.util.UUID;

public class BlockTeleporter extends BlockBase implements ITileEntityProvider {

    private IIcon[] icons = new IIcon[2];

    public BlockTeleporter() {
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_TELEPORTER_NAME);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
        super.onNeighborBlockChange(world, x, y, z, b);
        if(!world.isRemote && world.getStrongestIndirectPower(x, y, z) > 0) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityEngine) {
                ((TileEntityEngine) tile).move();
            }
        }
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    @Override
    public int damageDropped (int metadata) {
        return metadata;
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
                if (stack != null && stack.getItem() == ModItems.linker) {
                    NBTTagCompound tag = stack.getTagCompound();
                    if (tag != null && tag.hasKey("type")) {
                        int tagType = tag.getInteger("type");
                        if (tagType == world.getBlockMetadata(x, y, z)) {
                            TileEntityTeleporter.TeleporterType type = TileEntityTeleporter.TeleporterType.values()[tagType];
                            if (tileEntityTeleporter.setUUID(new UUID(tag.getLong("uuidMSB"), tag.getLong("uuidLSB")))) {
                                //uuid set
                                player.addChatMessage(new ChatComponentText(type.name().toUpperCase() + "-Teleporter-Link established!"));
                            } else {
                                //uuid not set
                                player.addChatMessage(new ChatComponentText(type.name().toUpperCase() + "-Teleporter-Link not established!"));
                            }
                        }
                    } else {
                        if (tag == null) tag = new NBTTagCompound();
                        TileEntityTeleporter.TeleporterType type = TileEntityTeleporter.TeleporterType.values()[world.getBlockMetadata(x, y, z)];
                        tag.setLong("uuidMSB", tileEntityTeleporter.getUuid().getMostSignificantBits());
                        tag.setLong("uuidLSB", tileEntityTeleporter.getUuid().getLeastSignificantBits());
                        Pos pos = tileEntityTeleporter.getPosition();
                        tag.setIntArray("pos", new int[]{pos.x, pos.y, pos.z});
                        tag.setInteger("type", type.ordinal());
                        tag.setString("dim", world.getWorldInfo().getWorldName());
                        stack.setTagCompound(tag);
                        player.addChatMessage(new ChatComponentText("Saved " + type.name().toUpperCase() + "-Teleporter position!"));
                    }
                } else {
                    tileEntityTeleporter.teleport();
                }
            }
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityTeleporter(TileEntityTeleporter.TeleporterType.values()[metadata]);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(String.format("%s:frame%s", Reference.MOD_ID.toLowerCase(), Reference.BLOCK_TELEPORTER_NAME));
        icons[1] = iconRegister.registerIcon(String.format("%s:player%s", Reference.MOD_ID.toLowerCase(), Reference.BLOCK_TELEPORTER_NAME));
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return icons[blockAccess.getBlockMetadata(x, y, z)];
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[meta];
    }

}
