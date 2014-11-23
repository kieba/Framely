package com.rk.framely.network;

import com.rk.framely.network.serializer.SerializeProcessor;
import com.rk.framely.util.Pos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketTileSimpleAction extends PacketCoordinates {

    public String command;
    public PacketTileSimpleAction(){

    }

    public PacketTileSimpleAction(int id, int posX, int posY, int posZ, String command){
        super(id, posX, posY, posZ);
        this.command=command;
    }

    public PacketTileSimpleAction(int id, Pos pos, String command){
        this(id,pos.x,pos.y,pos.z,command);
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        super.writeData(data);
        SerializeProcessor.writeData(data,command);
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        super.readData(data);
        command = (String)SerializeProcessor.readData(data);
    }
}
