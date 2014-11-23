package com.rk.framely.network;


public interface IPacketReceiver {
    public void ReceiveClientData(Packet packet);
    public void ReceiveServerData(Packet packet);
}
