package brachy84.brachydium.api.render;

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class CycableTexture {

    private final Texture[] textures;

    public CycableTexture(Texture... textures) {
        this.textures = Objects.requireNonNull(textures);
    }

    public static CycableTexture createDoubleTexture(Texture textureTrue, Texture textureFalse) {
        return new CycableTexture(textureTrue, textureFalse);
    }

    public static CycableTexture createWorkableTexture(String face, Identifier basePath) {
        String path = basePath.getPath() + "/overlay_" + face.toLowerCase();
        Texture inactive = new Texture(new Identifier(basePath.getNamespace(), path));
        Texture active = new Texture(new Identifier(basePath.getNamespace(), path + "_active"));
        return createDoubleTexture(active, inactive);
    }

    public @NotNull Sprite getSprite(int index) {
        return textures[index].getSprite();
    }

    public @NotNull Sprite getSprite(boolean active) {
        return textures[active ? 0 : 1].getSprite();
    }
}
