package com.rk.framely.item;

import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityTeleporter;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockTeleporter extends ItemBlock {

    public ItemBlockTeleporter(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata (int damageValue) {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        TileEntityTeleporter.TeleporterType type = TileEntityTeleporter.TeleporterType.values()[stack.getItemDamage()];
        return String.format("item.%s:%s%s", Reference.MOD_ID.toLowerCase(), type.name().toLowerCase(), Reference.BLOCK_TELEPORTER_NAME);
    }

}
