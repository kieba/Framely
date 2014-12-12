package com.rk.framely.tileentity;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import com.rk.framely.handler.TeleportRegistry;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pair;
import com.rk.framely.util.Pos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.UUID;

public class TileEntityTeleporter extends TileEntityBase implements IEnergyHandler {

    public enum TeleporterType {
        Frame,
        Player,
        Unknown
    }

    private static final int ENERGY_PER_TELEPORT = 0; //100000;
    private static final int ENERGY_PER_BLOCK = 3200;
    private TeleporterType type;
    private UUID uuid = UUID.randomUUID();
    private EnergyStorage storage = new EnergyStorage(0, 0, 0);

    public TileEntityTeleporter() {}

    public TileEntityTeleporter(TeleporterType type) {
        this.type = type;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if(!worldObj.isRemote) TeleportRegistry.unregister(this);
    }

    @Override
    public void validate() {
        super.validate();
        if(type == TeleporterType.Frame) {
            storage = new EnergyStorage(ENERGY_PER_BLOCK * 1024, 10000, Integer.MAX_VALUE);
        } else  if(type == TeleporterType.Player) {
            storage = new EnergyStorage(ENERGY_PER_TELEPORT * 4, 10000, Integer.MAX_VALUE);
        }
        if(!worldObj.isRemote) TeleportRegistry.register(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        uuid = new UUID(tag.getLong("uuidMSB"), tag.getLong("uuidLSB"));
        type = TeleporterType.values()[tag.getInteger("type")];
        storage.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setLong("uuidMSB", uuid.getMostSignificantBits());
        tag.setLong("uuidLSB", uuid.getLeastSignificantBits());
        tag.setInteger("type", type.ordinal());
        storage.writeToNBT(tag);
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean setUUID(UUID uuid) {
        if(worldObj.isRemote) return false;
        UUID oldUuid = new UUID(this.uuid.getMostSignificantBits(), this.uuid.getLeastSignificantBits());
        this.uuid = uuid;
        TeleportRegistry.unregister(this);
        if(!TeleportRegistry.register(this)) {
            this.uuid = oldUuid;
            TeleportRegistry.register(this);
            return false;
        }
        return true;
    }

    public TeleporterType getType() {
        return type;
    }

    public boolean teleport() {
        Pair<Pos, World> destination = TeleportRegistry.getDestination(this);
        Pos pos = destination.getKey();
        if(pos.equals(Pos.NULL)) return false;
        if(type == TeleporterType.Frame) {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                TileEntity tileEntity = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
                if (tileEntity instanceof TileEntityFrameBase) {
                    TileEntityFrameManager tefm = ((TileEntityFrameBase) tileEntity).getFrameManager();
                    if (tefm != null) {
                        int energyNeeded = tefm.relativeConstruction.size() * ENERGY_PER_BLOCK;
                        if (storage.getEnergyStored() >= energyNeeded) {
                            //offset from the FrameManager to the Teleporter
                            int offsetX = tefm.xCoord - xCoord;
                            int offsetY = tefm.yCoord - yCoord;
                            int offsetZ = tefm.zCoord - zCoord;
                            if (tefm.teleport(destination.getValue(), pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ)) {
                                storage.extractEnergy(energyNeeded, false);
                                return true;
                            }
                        }
                        break;
                    }
                }
            }
        } else if(type == TeleporterType.Player) {
            EntityPlayer player = worldObj.getClosestPlayer(xCoord + 0.5, yCoord + 1.0, zCoord + 0.5, 0.5);
            if(player != null) {
                if(storage.getEnergyStored() >= ENERGY_PER_TELEPORT) {
                    if(destination.getValue() != worldObj) {
                        player.travelToDimension(destination.getValue().provider.dimensionId);
                    }
                    player.setPositionAndUpdate(pos.x + 0.5, pos.y + 1.5, pos.z + 0.5);
                    return true;
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
