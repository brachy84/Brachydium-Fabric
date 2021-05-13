package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.Widget;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Color;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class TextWidget extends Widget {

    private final Text text;
    private final Color color;

    public TextWidget(Text text, Color color, Point point) {
        super(AABB.of(Size.ZERO, point));
        this.text = text;
        this.color = color;
        size = new Size(MinecraftClient.getInstance().textRenderer.getWidth(text), 9);
    }

    public TextWidget(Text text, Point point) {
        this(text, Color.of(1f, 1f, 1f), point);
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {
        guiHelper.getTextRenderer().draw(matrices, text, pos.getX(), pos.getY(), color.toInt());
    }
}
