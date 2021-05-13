package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * an abstract slot for handling resources
 * (like ItemStack or FluidStack)
 * @param <T> Resource f.e. ItemStack
 */
public abstract class ResourceSlotWidget<T> extends Widget implements Interactable, ISyncedWidget {

    private final List<ISprite> textures = new ArrayList<>();
    public ResourceSlotWidget(AABB bounds) {
        super(bounds);
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {
        if(textures.size() > 0) {
            textures.forEach(sprite -> guiHelper.drawSprite(sprite, pos));
        } else {
            guiHelper.drawSprite(getDefaultTexture(), pos);
        }
        renderResource(matrices);
    }

    @Environment(EnvType.CLIENT)
    public abstract void renderResource(MatrixStack matrices);

    @Override
    public ResourceSlotWidget<T> getParent() {
        return this;
    }

    public abstract T getResource();

    public abstract void setResource(T resource);

    public abstract boolean isEmpty();

    public abstract ISprite getDefaultTexture();

    public ResourceSlotWidget<T> setBackgroundSprites(ISprite... sprite) {
        textures.addAll(Arrays.asList(sprite));
        return this;
    }

}
