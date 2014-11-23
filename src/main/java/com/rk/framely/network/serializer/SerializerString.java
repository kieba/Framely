package com.rk.framely.network.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class SerializerString extends SerializerBase {
    private final Charset charset = Charset.forName("UTF-8");

    @Override
    public Object readData(DataInputStream data) throws IOException {
        int length = data.readInt();
        byte[] byteData = new byte[length];
        data.read(byteData,0,length);
        return new String(byteData,charset);
    }

    @Override
    public void writeData(DataOutputStream data, Object obj) throws IOException{
        super.writeData(data,obj);
        String str = (String)obj;
        byte[] byteData = str.getBytes(charset);
        data.writeInt(byteData.length);
        data.write(byteData);
    }
}
