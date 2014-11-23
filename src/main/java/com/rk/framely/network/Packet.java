package com.rk.framely.network;


import com.rk.framely.reference.Reference;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.*;

public class Packet {
    protected int id;

    public Packet(){

    }

    public Packet(int id){
        this.id = id;
    }

    public FMLProxyPacket getPacket() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);

        try {
            data.writeByte(getID());
            writeData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new FMLProxyPacket(Unpooled.wrappedBuffer(bytes.toByteArray()), Reference.NETWORK_CHANNEL_NAME);
    }

    protected NBTTagCompound readNBTTagCompound(DataInputStream data) throws IOException {

        short length = data.readShort();

        if (length < 0)
            return null;
        else {
            byte[] compressed = new byte[length];
            data.readFully(compressed);
            return CompressedStreamTools.readCompressed(new ByteArrayInputStream(compressed));
        }

    }

    protected void writeNBTTagCompound(NBTTagCompound nbttagcompound, DataOutputStream data) throws IOException {

        if (nbttagcompound == null)
            data.writeShort(-1);
        else {
            byte[] compressed = CompressedStreamTools.compress(nbttagcompound);
            data.writeShort((short) compressed.length);
            data.write(compressed);
        }

    }

    public void writeData(DataOutputStream data) throws IOException {
    }

    public void readData(DataInputStream data) throws IOException {
    }

    public int getID() {
        return id;
    }
}
