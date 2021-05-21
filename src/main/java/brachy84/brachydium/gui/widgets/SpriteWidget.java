package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.api.Widget;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import net.minecraft.client.util.math.MatrixStack;

public class SpriteWidget extends Widget {

    private final TextureArea sprite;
    private Size drawSize;

    public SpriteWidget(TextureArea sprite, Point point) {
        this(sprite, point, sprite.getImageSize());
    }

    public SpriteWidget(TextureArea sprite, Point point, Size drawSize) {
        super(AABB.of(sprite.getImageSize(), point));
        this.sprite = sprite;
        this.drawSize = drawSize;
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {
        guiHelper.drawTextureArea(sprite, pos, drawSize);
    }

    public TextureArea getTexture() {
        return sprite;
    }
}
