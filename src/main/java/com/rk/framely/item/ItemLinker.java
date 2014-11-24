package com.rk.framely.item;

import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityTeleporter;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ItemLinker extends ItemBase {

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
                player.addChatMessage(new ChatComponentText("Cleared teleporter position!"));
            }
        }
        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        NBTTagCompound tag = stack.getTagCompound();
        if(tag != null && tag.hasKey("uuidMSB")) {
            int[] pos = tag.getIntArray("pos");
            list.add("Linked to x: " + pos[0] + " y: " + pos[1] + " z: " + pos[2]);
        }
    }
}
