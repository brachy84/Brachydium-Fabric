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
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class Texture extends SpriteLoader {

    private final Identifier spriteId;
    private Sprite sprite;

    public Texture(Identifier path) {
        this.spriteId = path;
    }

    public Texture(String namespace, String path) {
        this(new Identifier(namespace, path));
    }

    public Texture(String path) {
        this(Brachydium.id(path));
    }

    public static Texture of(Sprite sprite) {
        Texture texture = new Texture(sprite.getId());
        texture.sprite = sprite;
        return texture;
    }

    @Override
    public void loadSprites(SpriteMap spriteMap) {
        sprite = spriteMap.loadSprite(spriteId);
    }

    @Override
    public void addSprites(List<SpriteIdentifier> sprites) {
        sprites.add(blockSprite(spriteId));
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void render(QuadEmitter emitter, Direction direction) {
        TileRenderUtil.renderSide(emitter, direction, sprite);
    }
}
