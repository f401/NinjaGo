package net.yx.ninjago.network;

import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import net.yx.ninjago.NinjaGo;

public class NinjaGoNetwork {
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(NinjaGo.MOD_ID)
        .networkProtocolVersion(0)
        .acceptedVersions((s, v) -> true).simpleChannel();

    public static void init() {}
}
