package com.rk.framely.handler;

import com.rk.framely.client.gui.GuiFrameManager;
import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityFrameManager;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        switch (ID){
            case Reference.GUI_FRAME_MANAGER:
                return new Container() {
                    @Override
                    public boolean canInteractWith(EntityPlayer var1) {
                        return true;
                    }
                };
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        switch (ID){
            case Reference.GUI_FRAME_MANAGER:
                return new GuiFrameManager(player.inventory, (TileEntityFrameManager) tileEntity);
        }
        return null;
    }
}
