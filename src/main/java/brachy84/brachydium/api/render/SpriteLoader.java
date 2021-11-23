package brachy84.brachydium.api.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class SpriteLoader {

    private static final List<SpriteLoader> loaders = new ArrayList<>();
    private static boolean loaded = false;

    public SpriteLoader() {
        if (!loaded) {
            loaders.add(this);
        }
    }

    /**
     * Helper method to create {@link SpriteIdentifier}
     *
     * @param id texture path
     * @return Sprite Identifier
     */
    protected static SpriteIdentifier blockSprite(Identifier id) {
        return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, id);
    }

    @ApiStatus.Internal
    public static List<SpriteIdentifier> gatherDependencies() {
        SpriteMap map = new SpriteMap(null);
        loaders.forEach(loader -> loader.loadSprites(map));
        return SpriteMap.getSprites();
    }

    @ApiStatus.Internal
    public static void load(Function<SpriteIdentifier, Sprite> textureGetter) {
        if (loaded) return;
        SpriteMap spriteMap = new SpriteMap(textureGetter);
        loaders.forEach(loader -> loader.loadSprites(spriteMap));
        loaded = true;
    }

    /**
     * Load you sprites here. This is important. Sprites that are not loaded with {@link SpriteMap#loadSprite(SpriteIdentifier)} will not be rendered.
     * Will be called twice. First to get the ID's to make sure they are loaded and Second when the are actually loaded and Sprite is returned
     *
     * @param spriteMap sprites to load to
     */
    protected abstract void loadSprites(SpriteMap spriteMap);


}
