package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.math.Color;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Shape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import org.jetbrains.annotations.ApiStatus;

/**
 * You don't need to implement this
 */
@ApiStatus.NonExtendable
@Environment(EnvType.CLIENT)
public abstract class GuiHelper extends DrawableHelper {

    public abstract void drawText();

    public abstract void drawShape(Shape shape, Color color);
}
