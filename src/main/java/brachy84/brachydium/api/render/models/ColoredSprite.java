package brachy84.brachydium.api.render.models;

import brachy84.brachydium.gui.math.Color;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.lwjgl.system.CallbackI;

import java.util.function.Function;

public class ColoredSprite {

    private SpriteIdentifier id;
    private Sprite sprite;
    int color;

    public ColoredSprite(Identifier path, int color) {
        this.id = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, path);
        this.color = Color.of((float) (color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F).asInt();
    }

    public boolean hasColor() {
        return color > 0;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public SpriteIdentifier getId() {
        return id;
    }

    public void makeSprite(Function<SpriteIdentifier, Sprite> getter) {
        sprite = getter.apply(id);
    }

    public void emit(QuadEmitter emitter, boolean forceEmit) {
        if(!forceEmit && sprite instanceof MissingSprite) return;
        emitFace(emitter, Direction.NORTH, 7.5f / 16);
        emitFace(emitter, Direction.SOUTH, 7.5f / 16);
    }

    public void emitFace(QuadEmitter emitter, Direction direction, float depth) {
        emitter.square(direction, 0, 0, 1, 1, depth);
        emitter.spriteColor(0, color, color, color, color);
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.emit();
    }
}
