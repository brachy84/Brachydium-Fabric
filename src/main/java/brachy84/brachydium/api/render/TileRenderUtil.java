package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.util.Face;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TileRenderUtil {

    public static void forEachDirection(Consumer<Direction> consumer) {
        for (Direction direction : Direction.values()) {
            consumer.accept(direction);
        }
    }

    public static void renderSide(QuadEmitter emitter, Face face, Direction frontFacing, Sprite sprite) {
        if (face == Face.SIDE) {
            renderSide(emitter, Face.LEFT.getDirection(frontFacing), sprite);
            renderSide(emitter, Face.RIGHT.getDirection(frontFacing), sprite);
        } else {
            renderSide(emitter, face.getDirection(frontFacing), sprite);
        }
    }

    /**
     * Renders a texture on a full side
     *
     * @param emitter   used for rendering
     * @param direction of the rendered sprite
     * @param texture   to render
     */
    public static void renderSide(QuadEmitter emitter, Direction direction, Texture texture) {
        if (texture == null) return;
        renderSide(emitter, direction, texture.getSprite());
    }

    public static void renderSide(QuadEmitter emitter, Direction direction, Sprite sprite) {
        renderSide(emitter, direction, sprite, -1);
    }

    public static void renderSide(QuadEmitter emitter, Direction direction, Sprite sprite, int color) {
        if (sprite == null) {
            Brachydium.LOGGER.fatal("Error rendering Side. Sprite is null");
            return;
        }
        emitter.square(direction, 0f, 0f, 1f, 1f, 0f);
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, color, color, color, color);
        emitter.emit();
    }

    public static void renderCube(QuadEmitter emitter, Texture texture) {
        forEachDirection(direction -> renderSide(emitter, direction, texture));
    }

    public static void renderOrientedCube(QuadEmitter emitter, Direction front, Map<Face, Texture> textureMap) {
        textureMap.forEach((face, texture) -> renderSide(emitter, face.getDirection(front), texture));
    }

    public static void renderOverlays(QuadEmitter emitter, Direction front, Map<Face, List<Texture>> textureMap) {
        textureMap.forEach((face, textures) -> {
            textures.forEach(texture -> renderSide(emitter, face.getDirection(front), texture));
        });
    }

    public static void renderOrientedCube(QuadEmitter emitter, Direction front, Texture side, Texture topBottom) {
        renderOrientedCube(emitter, front, Map.of(Face.TOP, topBottom, Face.BOTTOM, topBottom, Face.FRONT, side, Face.RIGHT, side, Face.BACK, side, Face.LEFT, side));
    }
}
