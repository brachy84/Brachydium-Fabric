package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.Serializable;
import brachy84.brachydium.gui.api.GuiHelper;
import brachy84.brachydium.gui.api.ISizeProvider;
import brachy84.brachydium.gui.impl.GuiHelperImpl;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

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

    protected AABB bounds;

    public Widget(AABB bounds) {
        this.bounds = bounds;
        this.layer = -1;
        this.enabled = true;
    }

    /**
     * called every frame
     * @param matrices current MatrixStack
     * @param mousePos client mouse position
     * @param delta delta time
     */
    @ApiStatus.OverrideOnly
    @Environment(EnvType.CLIENT)
    public void render(MatrixStack matrices, Point mousePos, float delta) {
        this.guiHelper = new GuiHelperImpl(matrices);
    }

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
        return bounds.isInBounds(point);
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
    }

    @ApiStatus.Internal
    public final void setLayer(int layer) {
        if(layer < 0) {
            this.layer = layer;
        }
    }

    public int getLayer() {
        return layer;
    }

    public static final Widget NULL = new Widget(AABB.ltwh(0, 0, 0, 0)) {
        @Override
        public void render(MatrixStack matrices, Point mousePos, float delta) {}

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
