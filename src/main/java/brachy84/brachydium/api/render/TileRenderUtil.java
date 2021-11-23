package brachy84.brachydium.api.render;

import brachy84.brachydium.api.util.Face;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;

public class TileRenderUtil {

    /**
     * Renders a sprite in a face relative to a direction.
     *
     * @param frontFacing face is relative to this
     * @param sprite      sprite to render
     * @param face        face to render on
     */
    public static void renderFace(QuadEmitter emitter, Direction frontFacing, Sprite sprite, Face face) {
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
     * @param emitter    used for rendering
     * @param direction  of the rendered sprite
     * @param renderable to render
     */
    public static void renderSide(QuadEmitter emitter, Direction direction, Sprite renderable) {
        renderSide(emitter, direction, renderable, -1);
    }

    public static void renderSide(QuadEmitter emitter, Direction direction, Sprite sprite, int color) {
        emitter.square(direction, 0f, 0f, 1f, 1f, 0f);
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, color, color, color, color);
        emitter.emit();
    }

    public static void renderCube(QuadEmitter emitter, Sprite texture) {
        for (Direction direction : Direction.values()) {
            renderSide(emitter, direction, texture);
        }
    }

    public static void renderOrientedCube(QuadEmitter emitter, Direction front, Map<Face, Sprite> textureMap) {
        textureMap.forEach((face, texture) -> renderFace(emitter, front, texture, face));
    }

    public static void renderOverlays(QuadEmitter emitter, Direction front, Map<Face, List<Sprite>> textureMap) {
        textureMap.forEach((face, textures) -> {
            textures.forEach(texture -> renderFace(emitter, front, texture, face));
        });
    }

    public static void renderOrientedCube(QuadEmitter emitter, Direction front, Sprite side, Sprite topBottom) {
        renderFace(emitter, front, topBottom, Face.TOP);
        renderFace(emitter, front, topBottom, Face.BOTTOM);
        renderFace(emitter, front, side, Face.FRONT);
        renderFace(emitter, front, side, Face.LEFT);
        renderFace(emitter, front, side, Face.RIGHT);
        renderFace(emitter, front, side, Face.BACK);
    }
}
