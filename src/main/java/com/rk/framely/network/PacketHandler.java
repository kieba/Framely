package com.rk.framely.network;

import com.rk.framely.reference.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import java.io.DataInputStream;
import java.io.InputStream;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toLowerCase());
    private final FMLEventChannel channel;


    public static void init() {
        INSTANCE.registerMessage(MessageEngine.class, MessageEngine.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(MessageFrameManager.class, MessageFrameManager.class, 1, Side.CLIENT);
    }

    public PacketHandler(){
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(Reference.NETWORK_CHANNEL_NAME);
        channel.register(this);
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        onPacketData(new ByteBufInputStream(event.packet.payload()),
                ((NetHandlerPlayServer) event.handler).playerEntity);
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        onPacketData(new ByteBufInputStream(event.packet.payload()),
                null);
    }

    public void sendPacket(FMLProxyPacket packet) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            channel.sendToServer(packet);
        } else {
            channel.sendToAll(packet);
        }
    }

    public void sendPacket(FMLProxyPacket packet, EntityPlayerMP player) {
        channel.sendTo(packet, player);
    }

    public void onPacketData(InputStream is, EntityPlayer player) {
        DataInputStream data = new DataInputStream(is);

        try {

            int packetId = data.readByte();

            switch (packetId) {
                case Reference.NETWORK_PACKET_ID_TILE_SIMPLE_ACTION:
                    PacketTileSimpleAction ptsa = new PacketTileSimpleAction();
                    ptsa.readData(data);
                    ((IPacketReceiver)ptsa.getTarget(player.getEntityWorld())).ReceiveServerData(ptsa);
                    break;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
