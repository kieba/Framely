package com.rk.framely.item;

import com.rk.framely.reference.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.List;

public class ItemLinker extends ItemBase {

    public enum TeleporterType {
        Player,
        Frame
    }

    public ItemLinker() {
        super();
        this.setNoRepair();
        this.setUnlocalizedName(Reference.ITEM_LINKER_NAME);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(!world.isRemote && player.isSneaking()) {
            NBTTagCompound tag = stack.getTagCompound();
            if(tag != null && tag.hasKey("uuidMSB")) {
                tag.removeTag("uuidMSB");
                tag.removeTag("uuidLSB");
                tag.removeTag("pos");
                tag.removeTag("dim");
                tag.removeTag("type");
                player.addChatMessage(new ChatComponentText("Cleared linker position!"));
            }
        }
        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        NBTTagCompound tag = stack.getTagCompound();
        if(tag != null && tag.hasKey("uuidMSB")) {
            int[] pos = tag.getIntArray("pos");
            TeleporterType type = TeleporterType.values()[tag.getInteger("type")];
            list.add(type + "-Teleporter linked to x: " + pos[0] + " y: " + pos[1] + " z: " + pos[2] + " dimension: " + tag.getString("dim"));
        }
    }
}
