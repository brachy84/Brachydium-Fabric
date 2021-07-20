package brachy84.brachydium.api.render;

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class CycableTexture implements IRenderable {

    private final IntSupplier supplier;
    private final IRenderable[] textures;

    public CycableTexture(IntSupplier supplier, IRenderable... textures) {
        this.supplier = Objects.requireNonNull(supplier);
        this.textures = Objects.requireNonNull(textures);
    }

    public static CycableTexture createDoubleTexture(BooleanSupplier supplier, IRenderable textureTrue, IRenderable textureFalse) {
        return new CycableTexture(() -> supplier.getAsBoolean() ? 0 : 1, textureTrue, textureFalse);
    }

    public static CycableTexture createWorkableTexture(BooleanSupplier isActive, String face, Identifier basePath) {
        String path = basePath.getPath() + "/overlay_" + face.toLowerCase();
        Texture inactive = new Texture(new Identifier(basePath.getNamespace(), path));
        Texture active = new Texture(new Identifier(basePath.getNamespace(), path + "_active"));
        return createDoubleTexture(isActive, active, inactive);
    }

    @Override
    public @NotNull Sprite getSprite() {
        return textures[supplier.getAsInt()].getSprite();
    }
}
