package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.*;
import brachy84.brachydium.gui.impl.GuiHelperImpl;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
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
        draw(guiHelper, pos);
    }

    public void draw(GuiHelper helper, Point point) {
        // always draw empty
        helper.drawTextureArea(empty, point, size);
        float u0 = 0, u1 = 1, v0 = 0, v1 = 1;
        float width = size.width(), height = size.height();
        switch (dir) {
            case RIGHT:
                u1 = (float) progress.getAsDouble();
                width *= u1;
                break;
            case LEFT:
                u0 = (float) (1 - progress.getAsDouble());
                width *= 1 - u0;
                break;
            case UP:
                v0 = (float) (1 - progress.getAsDouble());
                height *= 1 - v0;
                break;
            case DOWN:
                v1 = (float) progress.getAsDouble();
                height *= v1;
                break;
        }
        TextureArea partFull = full.getSubArea(u0, v0, u1, v1);
        helper.drawTextureArea(partFull, point, new Size(width, height));
    }

    @Override
    public void getReiWidgets(List<me.shedaniel.rei.api.client.gui.widgets.Widget> widgets, Point origin) {
        GuiHelperImpl guiHelper = new GuiHelperImpl(new MatrixStack());
        me.shedaniel.rei.api.client.gui.widgets.Widget renderer = Widgets.createDrawableWidget(((helper, matrices, mouseX, mouseY, delta) -> {
            guiHelper.setMatrixStack(matrices);
            draw(guiHelper, origin.add(relativPos));
        }));
        widgets.add(renderer);
    }
}
