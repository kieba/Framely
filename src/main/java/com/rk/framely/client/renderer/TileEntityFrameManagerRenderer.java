package com.rk.framely.client.renderer;

import com.rk.framely.tileentity.TileEntityFrameManager;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pos;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class TileEntityFrameManagerRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {

        if(tileEntity instanceof TileEntityFrameManager) {
            TileEntityFrameManager tile = (TileEntityFrameManager) tileEntity;

            if(tile.move) {
                float progress = tile.tick / (float)TileEntityFrameManager.ANIMATION_TICKS;

                float offsetX = tile.direction.offsetX * progress;
                float offsetY = tile.direction.offsetY * progress;
                float offsetZ = tile.direction.offsetZ * progress;

                List<Pos> tmp = tile.relativeConstruction;
                if(tmp != null) ConstructionRenderer.renderConstruction(tmp, x + offsetX, y + offsetY, z + offsetZ);
            } else {
                List<Pos> tmp = tile.relativeConstruction;
                if(tmp != null) ConstructionRenderer.renderConstruction(tmp, x, y, z);
            }


        }

    }

}
