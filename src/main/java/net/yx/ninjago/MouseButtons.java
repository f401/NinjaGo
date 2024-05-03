package net.yx.ninjago;

import net.minecraft.network.FriendlyByteBuf;

public class MouseButtons {
    public static final byte LEFT = 1;
    public static final byte MIDDLE = 2;
    public static final byte RIGHT = 4;

    public static MouseButtons from(FriendlyByteBuf buffer) {
        return new MouseButtons(buffer.readByte());
    }

    public static MouseButtons of(int buttons) {
        return new MouseButtons((byte) buttons);
    }

    /*public static enum Action {
        // 2 bits
        REPEAT(1), PRESSED(2), RELEASE(0);
        private final short actionValue;

        private Action(int actionValue) {
            this.actionValue = (short) actionValue;
        }

        public short value() {
            return this.actionValue;
        }
    }*/

    private byte triggered;

    public MouseButtons(byte pressed) {
        this.triggered = pressed;
    }

    public boolean buttonTriggered(byte button) {
        return (triggered & button) != 0;
    }

    public boolean rightButtonTriggered() {
        return buttonTriggered(RIGHT);
    }

    public boolean leftButtonTriggered() {
        return buttonTriggered(LEFT);
    }

    public boolean middleButtonTriggered() {
        return buttonTriggered(MIDDLE);
    }

    /*public void setAction(byte button, Action action) {
        int targetBits = (button - 1) * 2;
        // 清除原有设定
        actions &= ~(0x3 << targetBits);
        // 真正设置
        actions |= action.value() << targetBits;
    }

    public boolean triggeredAction(byte button, Action action) {
        return buttonTriggered(button) && (
            (actions >>> (button - 1) * 2) & 0x3) == action.value();
    }*/
    
    public void write(FriendlyByteBuf buffer) {
        buffer.writeByte(triggered);
        //buffer.writeShort(actions);
    }
}
