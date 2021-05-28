package brachy84.brachydium.api.render;

import brachy84.brachydium.api.util.Face;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OrientedOverlayRenderer extends Renderer {

    protected final Map<Face, Texture> textureMap = new HashMap<>();

    protected OrientedOverlayRenderer(Identifier path, Face... faces) {
        createTextures(path, faces);
    }

    protected void createTextures(Identifier path, Face... faces) {
        for (Face face : faces) {
            textureMap.put(face, new Texture(makePath(path, face)));
        }
    }

    protected Identifier makePath(Identifier path, Face face) {
        return new Identifier(path.getNamespace(), path.getPath() + "/" + face.toString().toLowerCase());
    }

    @Override
    public void render(QuadEmitter emitter, Direction frontFacing) {
        for(Map.Entry<Face, Texture> entry : textureMap.entrySet()) {
            renderSide(emitter, entry.getKey(), frontFacing, entry.getValue().getSprite());
        }
    }
}
