package com.rk.framely.network.serializer;

import com.rk.framely.util.LogHelper;

import java.util.HashMap;

public class SerializerRegistry {



    private static HashMap <Class<?>,SerializerBase> serializers = new HashMap<Class<?>, SerializerBase>();
    private static HashMap <Integer, SerializerBase> deserializers = new HashMap<Integer, SerializerBase>();
    private static int lastId;

    static {
        Register(new SerializerString(),String.class);
    }

    public static void Register(SerializerBase serializer, Class<?> clazz){
        if(serializers.containsKey(clazz)){
            LogHelper.error(clazz.getName() + " is already registered");
            return;
        }
        int id = lastId++;
           serializers.put(clazz,serializer);
           deserializers.put(id,serializer);
    }

    public static SerializerBase getSerializer(Class<?> clazz){
        return serializers.get(clazz);
    }

    public static SerializerBase getDeserializer(int id){
        return deserializers.get(id);
    }


}
