package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.math.Point;

public class BackgroundWidget extends SpriteWidget {

    public BackgroundWidget(TextureArea sprite) {
        super(sprite, Point.ZERO);
    }

    @Override
    public void setParentPosition(Point parentPosition) {
        super.setParentPosition(parentPosition);
        pos = parentPosition;
    }
}
