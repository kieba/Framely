package com.rk.framely.network;

import com.rk.framely.tileentity.TileEntityFrame;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

public class MessageFrame implements IMessage, IMessageHandler<MessageFrame, IMessage> {

    private int x, y,z;
    private int[] blockIds;
    private int[] metadata;

    public MessageFrame() {}

    public MessageFrame(TileEntityFrame tile) {
        x = tile.xCoord;
        y = tile.yCoord;
        z = tile.zCoord;
        blockIds = tile.blockIds;
        metadata = tile.metadata;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        blockIds = new int[6];
        metadata = new int[6];
        for (int i = 0; i < 6; i++) {
            blockIds[i] = buf.readInt();
            metadata[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        for (int i = 0; i < 6; i++) {
            buf.writeInt(blockIds[i]);
            buf.writeInt(metadata[i]);
        }
    }

    @Override
    public IMessage onMessage(MessageFrame message, MessageContext ctx) {
        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
        if (tileEntity instanceof TileEntityFrame) {
            TileEntityFrame tileEntityFrame = ((TileEntityFrame) tileEntity);
            for (int i = 0; i < 6; i++) {
                tileEntityFrame.blockIds[i] = message.blockIds[i];
                tileEntityFrame.metadata[i] = message.metadata[i];
            }
            tileEntityFrame.resetSideTextures();
            //NAME UPDATE
            FMLClientHandler.instance().getClient().theWorld.func_147451_t(message.x, message.y, message.z);
        }
        return null;
    }

}
