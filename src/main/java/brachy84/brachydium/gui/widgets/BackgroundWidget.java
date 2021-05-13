package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.ISprite;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;

public class BackgroundWidget extends SpriteWidget {

    public BackgroundWidget(ISprite sprite) {
        super(sprite, Point.ZERO);
    }

    @Override
    public void setParentPosition(Point parentPosition) {
        super.setParentPosition(parentPosition);
        pos = parentPosition;
    }
}
