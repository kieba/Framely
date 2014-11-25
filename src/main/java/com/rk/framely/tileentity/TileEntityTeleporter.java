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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sun.rmi.runtime.Log;

import java.util.UUID;

public class TileEntityTeleporter extends TileEntityBase implements IEnergyHandler {

    public enum TeleporterType {
        Frame,
        Player,
        Unknown
    }

    private static final int ENERGY_PER_TELEPORT = 100;
    private static final int ENERGY_PER_BLOCK = 80;
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
        if(!worldObj.isRemote) unregister();
    }

    @Override
    public void validate() {
        super.validate();
        if(type == TeleporterType.Frame) {
            storage = new EnergyStorage(ENERGY_PER_BLOCK * 1024, 10000, Integer.MAX_VALUE);
        } else  if(type == TeleporterType.Player) {
            storage = new EnergyStorage(ENERGY_PER_TELEPORT * 4, 10000, Integer.MAX_VALUE);
        }
        if(!worldObj.isRemote) register();
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
        unregister();
        if(!register()) {
            this.uuid = oldUuid;
            register();
            return false;
        }
        return true;
    }

    public boolean teleport() {
        Pair<Pos, World> destination = getDestination();
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

    private boolean register() {
        if(type == TeleporterType.Frame) {
            return FrameTeleportRegistry.register(this);
        } else if(type == TeleporterType.Player) {
            return PlayerTeleportRegistry.register(this);
        }
        return false;
    }

    private void unregister() {
        if(type == TeleporterType.Frame) {
            FrameTeleportRegistry.unregister(this);
        } else if(type == TeleporterType.Player) {
            PlayerTeleportRegistry.unregister(this);
        }
    }

    private Pair<Pos, World> getDestination() {
        if(type == TeleporterType.Frame) {
            return FrameTeleportRegistry.getDestination(this);
        } else if(type == TeleporterType.Player) {
            return PlayerTeleportRegistry.getDestination(this);
        } else  {
            LogHelper.info("Error!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!6" + type);
            return null;
        }
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
