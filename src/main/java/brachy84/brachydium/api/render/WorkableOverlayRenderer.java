package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.util.Face;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import java.util.EnumMap;
import java.util.Map;

public class WorkableOverlayRenderer extends SpriteLoader {

    private final Map<Face, Sprite> textures = new EnumMap<>(Face.class);
    private final Map<Face, Sprite> activeTextures = new EnumMap<>(Face.class);

    private final String base;
    private final Face[] faces;

    public WorkableOverlayRenderer(String base, Face... faces) {
        this.base = base;
        this.faces = faces;
    }

    @Override
    public void loadSprites(SpriteMap spriteMap) {
        for (Face face : faces) {
            String basePath = String.format("block/machines/%s/overlay_%s", base, face.name);
            textures.put(face, spriteMap.loadSprite(Brachydium.id(basePath)));
            basePath += "_active";
            activeTextures.put(face, spriteMap.loadSprite(Brachydium.id(basePath)));
        }
    }

    public void render(QuadEmitter emitter, Direction frontFace, boolean active) {
        if (active) {
            TileRenderUtil.renderOrientedCube(emitter, frontFace, activeTextures);
        } else {
            TileRenderUtil.renderOrientedCube(emitter, frontFace, textures);
        }
    }
}
