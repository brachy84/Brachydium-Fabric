package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.Widget;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Color;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Shape;
import net.minecraft.client.util.math.MatrixStack;

public class ShapeWidget extends Widget {

    private final Shape shape;
    private final Color color;
    private final Point point;

    public ShapeWidget(Shape shape, Color color, Point point) {
        super(AABB.of(shape.calculateSize(), point));
        this.shape = shape;
        this.color = color;
        this.point = point;
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {
        guiHelper.drawShape(pos, shape, color);
    }
}
