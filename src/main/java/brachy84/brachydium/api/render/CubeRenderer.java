package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class CubeRenderer extends Renderer {

    private final Texture texture;

    public CubeRenderer(String path) {
        this(Brachydium.id(path));
    }

    public CubeRenderer(Identifier path) {
        this(new Texture(path));
    }

    public CubeRenderer(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void render(QuadEmitter emitter, Direction frontFacing) {
        for(Direction direction : Direction.values()) {
            renderSide(emitter, direction, texture.getSprite());
        }
    }
}
