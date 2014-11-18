package com.rk.framely.util;

import com.rk.framely.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CreativeTabFramely extends CreativeTabs {

    public static final CreativeTabs FRAMELY_TAB = new CreativeTabFramely();

    private CreativeTabFramely() {
        super(Reference.MOD_ID);
    }

    @Override
    public Item getTabIconItem() {
        return Items.golden_apple;
    }

    @Override
    public String getTranslatedTabLabel() {
        return Reference.MOD_NAME;
    }
}
