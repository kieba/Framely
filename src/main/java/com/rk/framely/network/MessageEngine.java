package com.rk.framely.network;

import com.rk.framely.tileentity.TileEntityEngine;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class MessageEngine implements IMessage, IMessageHandler<MessageEngine, IMessage> {

    private int x, y,z;
    private ForgeDirection dir;

    public MessageEngine() {}

    public MessageEngine(TileEntityEngine tile) {
        x = tile.xCoord;
        y = tile.yCoord;
        z = tile.zCoord;
        dir = tile.getDir();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.dir = ForgeDirection.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(dir.ordinal());
    }

    @Override
    public IMessage onMessage(MessageEngine message, MessageContext ctx) {
        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
        if (tileEntity instanceof TileEntityEngine) {
            ((TileEntityEngine) tileEntity).setDir(message.dir);

            //NAME UPDATE
            FMLClientHandler.instance().getClient().theWorld.func_147451_t(message.x, message.y, message.z);
        }
        return null;
    }

}
