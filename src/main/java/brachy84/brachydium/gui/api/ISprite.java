package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.impl.Sprite;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 *  A simple sprite interface
 *  see {@link Sprite} for implementation
 */
public interface ISprite {

    /**
     * @return the path of the texture f.e. "brachydium:textures/gui/base/background"
     */
    Identifier getPath();

    /**
     * @return the size of the image
     */
    Size getSize();
}
