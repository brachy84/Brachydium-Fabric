package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.ISprite;
import brachy84.brachydium.gui.api.Widget;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import net.minecraft.client.util.math.MatrixStack;

public class SpriteWidget extends Widget {

    private final ISprite sprite;
    private float u, v;
    private Size drawSize;

    public SpriteWidget(ISprite sprite, Point point) {
        this(sprite, point, 0, 0);
    }

    public SpriteWidget(ISprite sprite, Point point, Size drawSize) {
        this(sprite, point, 0, 0, drawSize);
    }

    public SpriteWidget(ISprite sprite, Point point, float u, float v) {
        this(sprite, point, u, v, sprite.getSize());
    }

    public SpriteWidget(ISprite sprite, Point point, float u, float v, Size drawSize) {
        super(AABB.of(sprite.getSize(), point));
        this.sprite = sprite;
        this.u = u;
        this.v = v;
        this.drawSize = drawSize;
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {
        guiHelper.drawSprite(sprite, pos, u, v, drawSize);
    }

    public ISprite getSprite() {
        return sprite;
    }
}
