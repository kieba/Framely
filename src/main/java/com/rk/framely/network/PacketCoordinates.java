package com.rk.framely.network;


import com.rk.framely.util.Pos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketCoordinates extends Packet implements ILocatedPacket{

    public int posX;
    public int posY;
    public int posZ;

    public PacketCoordinates() {

    }

    public PacketCoordinates(int id, int posX, int posY, int posZ){
        super(id);
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public PacketCoordinates(int id, Pos position){
        this(id,position.x,position.y, position.z);
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        super.writeData(data);
        data.writeInt(posX);
        data.writeInt(posY);
        data.writeInt(posZ);

    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        super.readData(data);
        posX = data.readInt();
        posY = data.readInt();
        posZ = data.readInt();

    }

    public Pos getPosition(){
        return new Pos(posX,posY,posZ);
    }

    @Override
    public TileEntity getTarget(World world) {
        return world.getTileEntity(posX,posY,posZ);
    }
}
