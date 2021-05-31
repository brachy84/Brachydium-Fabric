package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class Texture {

    private static boolean inittialised = false;

    private static final List<Texture> textures = new ArrayList<>();

    public static boolean areInitialized() {
        return inittialised;
    }

    private SpriteIdentifier spriteId;
    private Sprite sprite;

    public Texture(Identifier path) {
        this.spriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, path);
        add(this);
    }

    public Texture(String path) {
        this(Brachydium.id(path));
    }

    public void makeSprite(Function<SpriteIdentifier, Sprite> textureGetter) {
        sprite = textureGetter.apply(spriteId);
        inittialised = true;
    }

    private static void add(Texture texture) {
        textures.add(texture);
    }

    public static List<Texture> getAll() {
        return Collections.unmodifiableList(textures);
    }

    public SpriteIdentifier getSpriteId() {
        return spriteId;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
