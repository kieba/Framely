package com.rk.framely.tileentity;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import com.rk.framely.handler.FrameTeleportRegistry;
import com.rk.framely.handler.PlayerTeleportRegistry;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pair;
import com.rk.framely.util.Pos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sun.rmi.runtime.Log;

import java.util.UUID;

public class TileEntityPlayerTeleporter extends TileEntityBase implements IEnergyHandler {

    private static final int ENERGY_PER_TELEPORT = 100;
    private UUID uuid = UUID.randomUUID();
    private EnergyStorage storage = new EnergyStorage(ENERGY_PER_TELEPORT * 40000, 10000, Integer.MAX_VALUE);

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

    @Override
    public void invalidate() {
        super.invalidate();
        if(!worldObj.isRemote) PlayerTeleportRegistry.unregisterFrameTeleporter(this);
    }

    @Override
    public void validate() {
        super.validate();
        if(!worldObj.isRemote) PlayerTeleportRegistry.registerFrameTeleporter(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean setUUID(UUID uuid) {
        if(worldObj.isRemote) return false;
        UUID oldUuid = new UUID(this.uuid.getMostSignificantBits(), this.uuid.getLeastSignificantBits());
        this.uuid = uuid;
        PlayerTeleportRegistry.unregisterFrameTeleporter(this);
        if(!PlayerTeleportRegistry.registerFrameTeleporter(this)) {
            this.uuid = oldUuid;
            PlayerTeleportRegistry.registerFrameTeleporter(this);
            return false;
        }
        return true;
    }

    public boolean teleport() {
        Pair<Pos, World> destination = PlayerTeleportRegistry.getDestination(this);
        Pos pos = destination.getKey();
        if(pos.equals(Pos.NULL)) return false;
        EntityPlayer player = worldObj.getClosestPlayer(xCoord + 0.5, yCoord + 1.0, zCoord + 0.5, 0.5);
        if(player != null) {
            if(storage.getEnergyStored() >= ENERGY_PER_TELEPORT) {
                if(destination.getValue()  != worldObj) {
                    player.travelToDimension(destination.getValue().provider.dimensionId);
                }
                player.setPositionAndUpdate(pos.x + 0.5, pos.y + 1.5, pos.z + 0.5);

                return true;
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
