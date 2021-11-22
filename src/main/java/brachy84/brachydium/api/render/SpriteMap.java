package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SpriteMap {

    private static final List<SpriteIdentifier> SPRITES = new ArrayList<>();

    private final Function<SpriteIdentifier, Sprite> textureGetter;

    protected SpriteMap(Function<SpriteIdentifier, Sprite> textureGetter) {
        this.textureGetter = textureGetter;
    }

    public Sprite loadSprite(SpriteIdentifier id) {
        Sprite sprite = textureGetter.apply(id);
        SPRITES.add(id);
        return sprite;
    }

    public Sprite loadSprite(Identifier id) {
        return loadSprite(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, id));
    }

    public static List<SpriteIdentifier> getSprites() {
        return Collections.unmodifiableList(SPRITES);
    }
}
