package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.Widget;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;

public class ButtonWidget extends Widget implements Interactable {

    public ButtonWidget(AABB bounds) {
        super(bounds);
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {

    }

    @Override
    public void onClick(Point point, int buttonId) {

    }

    @Override
    public ButtonWidget getParent() {
        return this;
    }

    @Override
    public boolean isMouseOver(Point point) {
        return isHovering(point);
    }


    @Override
    public void receiveData(PacketByteBuf data) {

    }

    @Override
    public void writeData(PacketByteBuf data) {

    }
}
