package net.yx.ninjago.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeyInputEvent {
    
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onMousePressedEvent(InputEvent.InteractionKeyMappingTriggered event) {
    }
}
