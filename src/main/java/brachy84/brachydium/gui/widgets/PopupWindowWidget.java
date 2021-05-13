package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.Widget;
import brachy84.brachydium.gui.math.Point;
import com.google.common.collect.BiMap;
import net.minecraft.client.util.math.MatrixStack;

public class PopupWindowWidget extends ParentWidget {

    public PopupWindowWidget(BackgroundWidget background, BiMap<Integer, Widget> children) {
        super(background, children);
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {

    }
}
