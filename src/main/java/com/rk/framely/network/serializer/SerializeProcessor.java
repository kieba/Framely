package com.rk.framely.network.serializer;

import com.rk.framely.util.LogHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SerializeProcessor {
    public static void writeData(DataOutputStream data, Object obj) throws IOException {
        SerializerBase serializer = SerializerRegistry.getSerializer(obj.getClass());
        if(serializer == null){
            LogHelper.error("No Serializer for " + obj.getClass().getName());
            return;
        }
        serializer.writeData(data, obj);
    }
    public static Object readData(DataInputStream data) throws IOException {
        int id = data.readInt();
        SerializerBase serializer = SerializerRegistry.getDeserializer(id);
        if(serializer == null){
            LogHelper.error("No Serializer for " + id);
            return null;
        }

        return serializer.readData(data);
    }
}
