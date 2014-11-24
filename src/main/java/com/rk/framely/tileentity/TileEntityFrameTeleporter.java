package com.rk.framely.tileentity;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import com.rk.framely.handler.FrameTeleportRegistry;
import com.rk.framely.util.Pair;
import com.rk.framely.util.Pos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.UUID;

public class TileEntityFrameTeleporter extends TileEntityBase implements IEnergyHandler {

    private static final int ENERGY_PER_BLOCK = 3200;
    private UUID uuid = UUID.randomUUID();
    private EnergyStorage storage = new EnergyStorage(ENERGY_PER_BLOCK * 1024, 10000, Integer.MAX_VALUE);

    @Override
    public void invalidate() {
        super.invalidate();
        if(!worldObj.isRemote) FrameTeleportRegistry.unregisterFrameTeleporter(this);
    }

    @Override
    public void validate() {
        super.validate();
        if(!worldObj.isRemote) FrameTeleportRegistry.registerFrameTeleporter(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        uuid = new UUID(tag.getLong("uuidMSB"), tag.getLong("uuidLSB"));
        storage.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setLong("uuidMSB", uuid.getMostSignificantBits());
        tag.setLong("uuidLSB", uuid.getLeastSignificantBits());
        storage.writeToNBT(tag);
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean setUUID(UUID uuid) {
        if(worldObj.isRemote) return false;
        UUID oldUuid = new UUID(this.uuid.getMostSignificantBits(), this.uuid.getLeastSignificantBits());
        this.uuid = uuid;
        FrameTeleportRegistry.unregisterFrameTeleporter(this);
        if(!FrameTeleportRegistry.registerFrameTeleporter(this)) {
            this.uuid = oldUuid;
            FrameTeleportRegistry.registerFrameTeleporter(this);
            return false;
        }
        return true;
    }

    public boolean teleport() {
        Pair<Pos, World> destination = FrameTeleportRegistry.getDestination(this);
        Pos pos = destination.getKey();
        if(pos.equals(Pos.NULL)) return false;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
            if(tileEntity instanceof TileEntityFrameBase)  {
                TileEntityFrameManager tefm = ((TileEntityFrameBase) tileEntity).getFrameManager();
                if(tefm != null) {
                    int energyNeeded = tefm.relativeConstruction.size() * ENERGY_PER_BLOCK;
                    if(storage.getEnergyStored() >= energyNeeded) {

                        //offset from the FrameManager to te Teleporter
                        int offsetX = tefm.xCoord - xCoord;
                        int offsetY = tefm.yCoord - yCoord;
                        int offsetZ = tefm.zCoord - zCoord;

                        if(tefm.teleport(destination.getValue(), pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ)) {
                            storage.extractEnergy(energyNeeded, false);
                            return true;
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }
}
