package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Shape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.ApiStatus;

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
    protected Shape shape;
    public ResourceSlotWidget(AABB bounds) {
        super(bounds);
        shape = Shape.rect(bounds.getSize());
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

    /**
     * Called when the player tries to insert something
     * @param resource to insert
     * @param player who inserts
     * @return if resource should be inserted
     */
    public boolean canPut(T resource, PlayerEntity player) {
        return true;
    }

    /**
     * Called when the player tries to take something
     * @param player who takes
     * @return if resource can be taken
     */
    public boolean canTake(PlayerEntity player) {
        return true;
    }

    public abstract T getResource();

    /**
     * @param resource to set
     * @param action see {@link Action}
     * @return if the resource was successfully set
     */
    public boolean setResource(T resource, Action action) {
        if(action == Action.TAKE) {
            if(!canTake(gui.player)) return false;
        } else if(action == Action.PUT) {
            if(!canPut(resource, gui.player)) return false;
        }
        return setResource(resource);
    }

    @ApiStatus.OverrideOnly
    public abstract boolean setResource(T resource);

    public abstract boolean isEmpty();

    public abstract TextureArea getDefaultTexture();

    public List<TextureArea> getTextures() {
        return textures;
    }

    public ResourceSlotWidget<T> setBackgroundSprites(TextureArea... sprite) {
        textures.addAll(Arrays.asList(sprite));
        return this;
    }

    public enum Action {

        /**
         * When the resource gets taken from the slot
         * Triggers {@link #canTake(PlayerEntity)}
         */
        TAKE,

        /**
         * When the resource is put into the slot
         * Triggers {@link #canPut(Object, PlayerEntity)}
         */
        PUT,

        /**
         * When the slot is synced to client
         * Triggers nothing
         */
        SYNC
    }
}
