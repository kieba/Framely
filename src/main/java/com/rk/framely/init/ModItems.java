package com.rk.framely.init;

import com.rk.framely.item.ItemLinker;
import com.rk.framely.reference.Reference;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.world.WorldSettings;

@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModItems {

    public static final ItemLinker linker = new ItemLinker();

    public static void init() {
        GameRegistry.registerItem(linker, Reference.ITEM_LINKER_NAME);
    }
}
