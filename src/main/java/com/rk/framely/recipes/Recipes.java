package com.rk.framely.recipes;

import com.rk.framely.init.ModBlocks;
import com.rk.framely.init.ModItems;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class Recipes {
    public static void init(){
        //GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.frameManager, "rlr", "ldl", "rlr", 'l', "gemLapis", 'd', "gemDiamond",'r',"dustRedstone"));
        //GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.frame, "xix", "iii", "xix", 'i', "ingotIron"));
        //GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.engine, "rir", "iii", "rir", 'i', "ingotIron",'r',"dustRedstone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.teleporter, 1, 1), "rir", "iei", "rir", 'i', Blocks.log,'r',"dustRedstone",'e', Items.ender_pearl));
        //GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.teleporter, 1, 0), "rir", "iei", "rfr", 'i', "ingotIron",'r',"dustRedstone",'e', Items.ender_pearl,'f',ModBlocks.frame));

        GameRegistry.addRecipe(new ShapedOreRecipe(ModItems.linker, "e  ", "s  ", "   ", 'e', Items.ender_pearl,'s', Items.stick));
    }

}