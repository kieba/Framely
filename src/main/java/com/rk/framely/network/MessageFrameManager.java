package com.rk.framely.network;

import com.rk.framely.tileentity.TileEntityEngine;
import com.rk.framely.tileentity.TileEntityFrameManager;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pos;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class MessageFrameManager implements IMessage, IMessageHandler<MessageFrameManager, IMessage> {

    private int x, y,z;
    private List<Pos> construction = new ArrayList<Pos>();

    public MessageFrameManager() {}

    public MessageFrameManager(TileEntityFrameManager tile) {
        x = tile.xCoord;
        y = tile.yCoord;
        z = tile.zCoord;
        if(tile.relativeConstruction != null) {
            construction = tile.relativeConstruction;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            construction.add(new Pos(buf.readInt(), buf.readInt(), buf.readInt()));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(construction.size());
        for (int i = 0; i < construction.size(); i++) {
            Pos p = construction.get(i);
            buf.writeInt(p.x);
            buf.writeInt(p.y);
            buf.writeInt(p.z);
        }
    }

    @Override
    public IMessage onMessage(MessageFrameManager message, MessageContext ctx) {
        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
        if (tileEntity instanceof TileEntityFrameManager) {
            ((TileEntityFrameManager) tileEntity).relativeConstruction = message.construction;
            //NAME UPDATE
            FMLClientHandler.instance().getClient().theWorld.func_147451_t(message.x, message.y, message.z);
        }
        return null;
    }

}
