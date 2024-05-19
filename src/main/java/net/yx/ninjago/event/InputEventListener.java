package net.yx.ninjago.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yx.ninjago.MouseButtons;
import net.yx.ninjago.network.NinjaGoNetwork;
import net.yx.ninjago.network.server.SPMouseEventsPacket;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InputEventListener {

    private static final Logger LOGGER = LogManager.getLogger();
    
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onMousePressedEvent(InputEvent.InteractionKeyMappingTriggered event) {
        int value = 0;
        if (event.isAttack()) {
            value |= MouseButtons.LEFT;
        }

        if (event.isPickBlock()) {
            value |= MouseButtons.MIDDLE;
        }

        if (event.isUseItem()) {
            value |= MouseButtons.RIGHT;
        }

        LOGGER.info("Client Trigged Mouse event, sending to server");

        NinjaGoNetwork.sendToServer(new SPMouseEventsPacket(MouseButtons.of(value)));
    }

}
