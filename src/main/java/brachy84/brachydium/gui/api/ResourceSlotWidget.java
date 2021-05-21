package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * an abstract slot for handling resources
 * (like ItemStack or FluidStack)
 * @param <T> Resource f.e. ItemStack
 */
public abstract class ResourceSlotWidget<T> extends Widget implements Interactable {

    private final List<TextureArea> textures = new ArrayList<>();
    public ResourceSlotWidget(AABB bounds) {
        super(bounds);
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {
        if(textures.size() > 0) {
            textures.forEach(sprite -> guiHelper.drawTextureArea(sprite, pos, size));
        } else {
            guiHelper.drawTextureArea(getDefaultTexture(), pos);
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

    public abstract TextureArea getDefaultTexture();

    public List<TextureArea> getTextures() {
        return textures;
    }

    public ResourceSlotWidget<T> setBackgroundSprites(TextureArea... sprite) {
        textures.addAll(Arrays.asList(sprite));
        return this;
    }
}
