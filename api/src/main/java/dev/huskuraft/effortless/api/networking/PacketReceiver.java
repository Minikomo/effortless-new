package dev.huskuraft.effortless.api.networking;

import dev.huskuraft.effortless.api.core.Player;

public interface PacketReceiver extends NetByteBufReceiver {

    void receivePacket(Packet packet, Player player);

    default void receivePacket(Packet packet) {
        receivePacket(packet, null);
    }

}
