package com.rk.framely.client.renderer;

import com.rk.framely.tileentity.TileEntityFrameManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityFrameManagerRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
        if(tileEntity instanceof TileEntityFrameManager) {
            TileEntityFrameManager tile = (TileEntityFrameManager) tileEntity;
            if(tile.move) {
                ConstructionRenderer.renderConstructionMovement(tile.relativeConstruction, x, y, z, tile.direction, tile.tick, TileEntityFrameManager.ANIMATION_TICKS);
            } else {
                if(tile.showConstructionGrid)
                    ConstructionRenderer.renderConstruction(tile.relativeConstruction, x, y, z);
            }

        }
    }

}
