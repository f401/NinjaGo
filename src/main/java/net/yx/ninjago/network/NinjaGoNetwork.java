package net.yx.ninjago.network;

import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.yx.ninjago.NinjaGo;
import net.yx.ninjago.network.server.SPMouseEventsPacket;

public class NinjaGoNetwork {
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(NinjaGo.MOD_ID)
            .networkProtocolVersion(0)
            .acceptedVersions((s, v) -> true).simpleChannel();

    public static void init() {
        int id = 0;
        INSTANCE.messageBuilder(SPMouseEventsPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).decoder(SPMouseEventsPacket::new)
                .encoder(SPMouseEventsPacket::toBytes).consumerMainThread(SPMouseEventsPacket::handle).add();
    }

    public static final <MSG> void sendToServer(MSG meassge) {
        INSTANCE.send(meassge, PacketDistributor.SERVER.noArg());
    }
}
