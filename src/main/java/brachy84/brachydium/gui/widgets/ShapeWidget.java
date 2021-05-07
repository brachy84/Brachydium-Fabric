package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Color;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Shape;
import net.minecraft.client.util.math.MatrixStack;

public class ShapeWidget extends Widget {

    private final Shape shape;
    private final Color color;

    public ShapeWidget(AABB bounds, Shape shape, Color color) {
        super(bounds);
        this.shape = shape;
        this.color = color;
    }

    @Override
    public void render(MatrixStack matrices, Point mousePos, float delta) {
        super.render(matrices, mousePos, delta);
        guiHelper.drawShape(shape, color);
    }
}
