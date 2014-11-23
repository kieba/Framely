package com.rk.framely.tileentity;

import com.rk.framely.util.Pos;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBase extends TileEntity {
    public Pos getPosition(){
        return new Pos(xCoord,yCoord,zCoord);
    }
}
