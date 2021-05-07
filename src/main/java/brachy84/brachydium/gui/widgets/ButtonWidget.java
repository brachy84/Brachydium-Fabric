package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;

public class ButtonWidget extends Widget implements Interactable<ButtonWidget> {

    public ButtonWidget(AABB bounds) {
        super(bounds);
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
}
