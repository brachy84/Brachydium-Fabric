package brachy84.brachydium.api.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class OverlayRenderer extends Renderer {

    private Texture texture;
    private Direction direction;

    public OverlayRenderer(Texture texture, Direction direction) {
        this.texture = texture;
        this.direction = direction;
    }

    @Override
    public void render(QuadEmitter emitter, Direction frontFacing) {
        renderSide(emitter, direction, texture);
    }
}
