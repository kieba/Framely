package com.rk.framely.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ILocatedPacket {
    TileEntity getTarget(World world);
}
