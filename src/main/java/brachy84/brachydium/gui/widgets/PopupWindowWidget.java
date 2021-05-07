package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.math.AABB;

import java.util.List;

public class PopupWindowWidget extends SingleChildWidget {

    public PopupWindowWidget(AABB bounds) {
        super(bounds);
    }

    @Override
    public List<Widget> getChildren() {
        return null;
    }
}
