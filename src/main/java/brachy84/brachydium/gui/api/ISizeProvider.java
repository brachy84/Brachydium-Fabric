package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Size;

public interface ISizeProvider {

    Size getScreenSize();

    Size getGuiSize();

    AABB getBounds();
}
