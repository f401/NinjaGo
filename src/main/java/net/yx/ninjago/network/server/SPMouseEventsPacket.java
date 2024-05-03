package net.yx.ninjago.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.yx.ninjago.MouseButtons;
import net.yx.ninjago.event.InputEventListener;
import net.yx.ninjago.event.ServerMouseEvent;

/**
 * 这个包专门用来读取客户端的鼠标输入，
 * 并在服务端触发事件
 *
 * @see ServerMouseEvent
 * @see InputEventListener
 */
public class SPMouseEventsPacket {
    private final MouseButtons btn;

    public SPMouseEventsPacket(FriendlyByteBuf buff) {
        this.btn = MouseButtons.from(buff);
    }

    public SPMouseEventsPacket(MouseButtons btn) {
        this.btn = btn;
    }

    public MouseButtons getButtons() {
        return btn;
    }

    public static void handle(SPMouseEventsPacket thiz, final Context context) {
        context.enqueueWork(() -> { 
            MinecraftForge.EVENT_BUS.post(new ServerMouseEvent(thiz.getButtons(), context.getSender()));
            context.setPacketHandled(true);
        });
    }

    public static void toBytes(SPMouseEventsPacket event, FriendlyByteBuf buff) {
        event.btn.write(buff);
    }
}
