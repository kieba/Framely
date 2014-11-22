package com.rk.framely.client.renderer;

import com.rk.framely.tileentity.TileEntityFrameManager;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pos;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class TileEntityFrameManagerRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
        if(tileEntity instanceof TileEntityFrameManager) {
            TileEntityFrameManager tile = (TileEntityFrameManager) tileEntity;
            if(tile.move) {
                ConstructionRenderer.renderConstructionMovement(tile.relativeConstruction, x, y, z, tile.direction, tile.tick, TileEntityFrameManager.ANIMATION_TICKS);
            } else {
                ConstructionRenderer.renderConstruction(tile.relativeConstruction, x, y, z);
            }

        }
    }

}
