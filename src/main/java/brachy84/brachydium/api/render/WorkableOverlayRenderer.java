package brachy84.brachydium.api.render;

import brachy84.brachydium.api.util.Face;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public class WorkableOverlayRenderer extends OrientedOverlayRenderer {

    protected WorkableOverlayRenderer(Identifier path, Face... faces) {
        super(path, faces);
    }

    @Override
    protected void createTextures(Identifier path, Face... faces) {
        for (Face face : faces) {
            textureMap.put(face, new WorkableTexture(makePath(path, face)));
        }
    }

    @Override
    protected Identifier makePath(Identifier path, Face face) {
        return new Identifier(path.getNamespace(), String.format("%s/overlay_%s", path.getPath(), face.toString().toLowerCase()));
    }

    public void render(QuadEmitter emitter, Direction frontFacing, boolean isActive) {
        for(Map.Entry<Face, Texture> entry : textureMap.entrySet()) {
            if(entry.getValue() instanceof WorkableTexture) {
                renderSide(emitter, entry.getKey(), frontFacing, ((WorkableTexture) entry.getValue()).getSprite(isActive));
            }
        }
    }

    @ApiStatus.Internal
    @Deprecated
    @Override
    public void render(QuadEmitter emitter, Direction frontFacing) {
        render(emitter, frontFacing, false);
    }
}
