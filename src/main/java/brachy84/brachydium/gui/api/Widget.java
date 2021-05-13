package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.impl.GuiHelperImpl;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import brachy84.brachydium.gui.math.Transformation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * A primitive Widget which only can render stuff
 */
public abstract class Widget {

    protected GuiHelper guiHelper;
    protected ModularGui gui;
    protected ISizeProvider sizeProvider;
    private Point parentPosition;
    protected int layer;
    private boolean enabled;
    private Transformation transformation;

    // relative position to parent
    protected final Point relativPos;
    protected Point pos;
    protected Size size;

    public Widget(AABB bounds) {
        this.relativPos = bounds.getTopLeft();
        this.pos = this.relativPos;
        this.size = bounds.getSize();
        this.layer = -1;
        this.enabled = true;
        this.parentPosition = Point.ZERO;
        this.guiHelper = new GuiHelperImpl(new MatrixStack());
        this.transformation = Transformation.ZERO;
    }


    @ApiStatus.Internal
    @Environment(EnvType.CLIENT)
    public void render(MatrixStack matrices, Point mousePos, float delta) {
        if(guiHelper instanceof GuiHelperImpl) {
            ((GuiHelperImpl) guiHelper).setMatrixStack(matrices);
            ((GuiHelperImpl) guiHelper).setTransformation(transformation);
            ((GuiHelperImpl) guiHelper).setZ(layer);
        }
        draw(matrices, mousePos, delta);
    }

    /**
     * called every frame
     * @param matrices current MatrixStack
     * @param mousePos client mouse position
     * @param delta delta time
     */
    @Environment(EnvType.CLIENT)
    public abstract void draw(MatrixStack matrices, Point mousePos, float delta);

    /**
     * called when opening the screen
     */
    @ApiStatus.OverrideOnly
    public void onInit() {}

    /**
     * called when closing the screen
     */
    @ApiStatus.OverrideOnly
    public void onDestroy() {}

    /**
     * @param point position of mouse
     * @return if the client mouse is above this widget
     */
    public boolean isHovering(Point point) {
        return getBounds().isInBounds(point);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /// Internal methods ! Don't call these !

    @ApiStatus.Internal
    public void setGui(ModularGui gui) {
        this.gui = gui;
        this.sizeProvider = gui;
    }

    @ApiStatus.Internal
    public void setParentPosition(Point parentPosition) {
        this.parentPosition = parentPosition;
        //this.bounds = AABB.of(bounds.getSize(), parentPosition.add(bounds.getTopLeft()));
        this.pos = relativPos.add(parentPosition);
        transformation.setRotationVector(getBounds().getCenter(), layer);
    }

    @ApiStatus.Internal
    public final void setLayer(int layer) {
        if(layer < 0) {
            this.layer = layer;
        }
    }

    public <T extends Widget> T setTransformation(Transformation transformation) {
        this.transformation = transformation;
        this.transformation.setRotationVector(getBounds().getCenter(), layer);
        return (T) this;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public int getLayer() {
        return layer;
    }

    public AABB getBounds() {
        return AABB.of(size, pos);
    }

    public Point getPos() {
        return pos;
    }

    public Size getSize() {
        return size;
    }

    public Point getRelativPos() {
        return relativPos;
    }

    public static final Widget NULL = new Widget(AABB.ltwh(0, 0, 0, 0)) {
        @Override
        public void draw(MatrixStack matrices, Point mousePos, float delta) {}

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean isHovering(Point point) {
            return false;
        }
    };
}
