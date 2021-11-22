package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
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
    private List<SpriteIdentifier> sprites;

    public SpriteLoader() {
        if (!loaded) {
            loaders.add(this);
        }
    }

    @ApiStatus.Internal
    public static void load(Function<SpriteIdentifier, Sprite> textureGetter) {
        if (loaded) return;
        SpriteMap spriteMap = new SpriteMap(textureGetter);
        loaders.forEach(loader -> loader.loadSprites(spriteMap));
        loaded = true;
    }

    public abstract void loadSprites(SpriteMap spriteMap);

    public abstract void addSprites(List<SpriteIdentifier> sprites);

    public static List<SpriteIdentifier> getAllSprites() {
        List<SpriteIdentifier> sprites = new ArrayList<>();
        for(SpriteLoader loader : loaders) {
            sprites.addAll(loader.getSprites());
        }
        return sprites;
    }

    public List<SpriteIdentifier> getSprites() {
        if(sprites == null) {
            sprites = new ArrayList<>();
            addSprites(sprites);
        }
        return sprites;
    }

    public static SpriteIdentifier blockSprite(Identifier id) {
        return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, id);
    }
}
