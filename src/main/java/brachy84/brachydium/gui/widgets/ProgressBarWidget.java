package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.MoveDirection;
import brachy84.brachydium.gui.api.ProgressTexture;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.api.Widget;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.DoubleSupplier;

public class ProgressBarWidget extends Widget {

    private TextureArea full, empty;

    /**
     * Must give a number between 0 and 1
     */
    private DoubleSupplier progress;
    private MoveDirection dir;

    public ProgressBarWidget(DoubleSupplier progress, TextureArea full, TextureArea empty, AABB bounds, MoveDirection dir) {
        super(bounds);
        this.progress = progress;
        this.full = full;
        this.empty = empty;
        this.dir = dir;
    }

    public ProgressBarWidget(DoubleSupplier progress, ProgressTexture texture, AABB bounds, MoveDirection dir) {
        this(progress, texture.getFull(), texture.getEmpty(), bounds, dir);
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {
        // always draw empty
        guiHelper.drawTextureArea(empty, pos, size);
        float u0 = 0, u1 = 1, v0 = 0, v1 = 1;
        switch (dir) {
            case RIGHT: {
                u1 = (float) progress.getAsDouble();
            }
            case LEFT: {
                u0 = (float) (1 - progress.getAsDouble());
            }
            case UP: {
                v0 = (float) (1 - progress.getAsDouble());
            }
            case DOWN: {
                v1 = (float) progress.getAsDouble();
            }
        }
        TextureArea partFull = full.getSubArea(u0, v0, u1, v1);
        guiHelper.drawTextureArea(partFull, pos, size);
    }
}
