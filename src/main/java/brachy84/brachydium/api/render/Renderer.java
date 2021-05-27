package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public abstract class Renderer {

    public abstract void render(QuadEmitter emitter, Direction frontFacing);

    /**
     * Renders a texture on a full side
     * @param emitter used for rendering
     * @param direction of the rendered sprite
     * @param texture to render
     */
    public static void renderSide(QuadEmitter emitter, Direction direction, Texture texture) {
        if(texture == null) return;
        renderSide(emitter, direction, texture.getSprite());
    }

    public static void renderSide(QuadEmitter emitter, Direction direction, Sprite sprite) {
        if(sprite == null) {
            Brachydium.LOGGER.fatal("Error rendering Side. Sprite is null");
            return;
        }
        if(sprite instanceof MissingSprite) return;
        emitter.square(direction, 0f, 0f, 1f, 1f, 0f);
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();
    }
}
