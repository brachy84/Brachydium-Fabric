package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class Texture implements IRenderable {

    private static boolean loaded = false;

    private static final List<Texture> textures = new ArrayList<>();

    private final SpriteIdentifier spriteId;
    private Sprite sprite;

    public Texture(Identifier path) {
        this.spriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, path);
        add(this);
    }

    public Texture(String namespace, String path) {
        this(new Identifier(namespace, path));
    }

    public Texture(String path) {
        this(Brachydium.id(path));
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

    @Override
    public @NotNull Sprite getSprite() {
        if(sprite == null) {
            if (!loaded)
                throw new IllegalStateException("Can't get Sprite when they are not loaded");
            this.sprite = MinecraftClient.getInstance().getSpriteAtlas(spriteId.getAtlasId()).apply(spriteId.getTextureId());
        }
        return sprite;
    }

    @Override
    public String toString() {
        return spriteId.getTextureId().toString();
    }

    @ApiStatus.Internal
    public static void loadSprites(Function<SpriteIdentifier, Sprite> textureGetter) {
        if (loaded) return;
        for (Texture texture : textures) {
            texture.sprite = textureGetter.apply(texture.spriteId);
        }
        loaded = true;
    }
}
