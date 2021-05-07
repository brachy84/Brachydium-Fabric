package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.math.AABB;

public interface ISizeProvider {


    int getScreenWidth();

    int getScreenHeight();

    default float getWidth() {
        return getBounds().width;
    }

    default float getHeight() {
        return getBounds().height;
    }

    AABB getBounds();
}
