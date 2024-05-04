package dev.huskuraft.effortless.api.networking;

import dev.huskuraft.effortless.api.core.Player;
import dev.huskuraft.effortless.api.core.ResourceLocation;
import dev.huskuraft.effortless.api.platform.Entrance;

public interface Networking {

    static ResourceLocation getChannelId() {
        return Entrance.getInstance().getChannel().getChannelId();
    }

    static int getCompatibilityVersion() {
        return Entrance.getInstance().getChannel().getCompatibilityVersion();
    }

    static String getCompatibilityVersionStr() {
        return Entrance.getInstance().getChannel().getCompatibilityVersionStr();
    }

    void sendToClient(NetByteBuf byteBuf, Player player);

    void sendToServer(NetByteBuf byteBuf, Player player);

    void registerClientReceiver(NetByteBufReceiver receiver);

    void registerServerReceiver(NetByteBufReceiver receiver);

}
