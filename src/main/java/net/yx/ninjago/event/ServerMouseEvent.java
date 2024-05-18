package net.yx.ninjago.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.yx.ninjago.MouseButtons;

/**
 * Fired on {@linkplain net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * Server only
 */
public class ServerMouseEvent extends Event implements IModBusEvent {
    private final MouseButtons button;
    private final ServerPlayer player;


    public ServerMouseEvent(MouseButtons button, ServerPlayer player) {
        this.button = button;
        this.player = player;
    }


    public MouseButtons getButton() {
        return button;
    }


    public ServerPlayer getPlayer() {
        return player;
    }

}
