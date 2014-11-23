package com.rk.framely.network.serializer;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class SerializerBase {
    public int id;

    public void setId(int id){
        this.id = id;
    }

    public void writeData(DataOutputStream data, Object obj) throws IOException{
        data.writeInt(id);
    }
    public abstract Object readData(DataInputStream data) throws IOException;

}
