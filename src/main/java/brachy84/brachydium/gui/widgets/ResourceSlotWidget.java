package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

public abstract class ResourceSlotWidget<T> extends Widget implements Interactable<ResourceSlotWidget<T>> {

    public ResourceSlotWidget(AABB bounds) {
        super(bounds);
    }

    @Override
    public void render(MatrixStack matrices, Point mousePos, float delta) {
        super.render(matrices, mousePos, delta);
        renderResource();
    }

    @Environment(EnvType.CLIENT)
    public abstract void renderResource();

    @Override
    public ResourceSlotWidget<T> getParent() {
        return this;
    }
}
