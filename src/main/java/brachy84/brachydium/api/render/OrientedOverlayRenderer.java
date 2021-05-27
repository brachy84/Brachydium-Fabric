package brachy84.brachydium.api.render;

import brachy84.brachydium.api.util.Face;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.math.Direction;

public class OrientedOverlayRenderer extends Renderer {

    private Texture texture;
    private Face[] faces;

    public OrientedOverlayRenderer(Texture texture, Face... faces) {
        this.texture = texture;
        this.faces = faces;
    }

    @Override
    public void render(QuadEmitter emitter, Direction frontFacing) {
        for(Face face : faces) {
            renderSide(emitter, face.getDirection(frontFacing), texture);
        }
    }
}
