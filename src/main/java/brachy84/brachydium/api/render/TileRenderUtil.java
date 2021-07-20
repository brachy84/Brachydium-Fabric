package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.blockEntity.IOrientable;
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

    public static void renderFace(QuadEmitter emitter, IOrientable orientable, IRenderable sprite, Face... faces) {
        renderFace(emitter, orientable.getFrontFace(), sprite, faces);
    }

    public static void renderFace(QuadEmitter emitter, Direction frontFacing, IRenderable sprite, Face... faces) {
        for (Face face : faces)
            renderSide(emitter, face.getDirection(frontFacing), sprite);
    }

    /**
     * Renders a texture on a full side
     *
     * @param emitter    used for rendering
     * @param direction  of the rendered sprite
     * @param renderable to render
     */
    public static void renderSide(QuadEmitter emitter, Direction direction, IRenderable renderable) {
        renderSide(emitter, direction, renderable, -1);
    }

    public static void renderSide(QuadEmitter emitter, Direction direction, IRenderable renderable, int color) {
        if (renderable == null) {
            Brachydium.LOGGER.fatal("Error rendering Side. Sprite is null");
            return;
        }
        emitter.square(direction, 0f, 0f, 1f, 1f, 0f);
        emitter.spriteBake(0, renderable.getSprite(), MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, color, color, color, color);
        emitter.emit();
    }

    public static void renderCube(QuadEmitter emitter, IRenderable texture) {
        forEachDirection(direction -> renderSide(emitter, direction, texture));
    }

    public static void renderOrientedCube(QuadEmitter emitter, Direction front, Map<Face, IRenderable> textureMap) {
        textureMap.forEach((face, texture) -> renderSide(emitter, face.getDirection(front), texture));
    }

    public static void renderOverlays(QuadEmitter emitter, Direction front, Map<Face, List<IRenderable>> textureMap) {
        textureMap.forEach((face, textures) -> {
            textures.forEach(texture -> renderSide(emitter, face.getDirection(front), texture));
        });
    }

    public static void renderOrientedCube(QuadEmitter emitter, Direction front, IRenderable side, IRenderable topBottom) {
        renderOrientedCube(emitter, front, Map.of(Face.TOP, topBottom, Face.BOTTOM, topBottom, Face.FRONT, side, Face.RIGHT, side, Face.BACK, side, Face.LEFT, side));
    }
}
