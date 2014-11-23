package com.rk.framely.network;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class PacketNamedData extends Packet {

    private HashMap<String, Object> namedData;

    public PacketNamedData(){

    }

    public PacketNamedData(int id){
        super(id);
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        super.writeData(data);


    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        super.readData(data);


    }
}
