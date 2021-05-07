package brachy84.brachydium.api.cover;

import brachy84.brachydium.api.render.Renderer;
import brachy84.brachydium.api.render.Texture;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.math.Direction;

public abstract class Cover {

    public void render(QuadEmitter emitter, Direction side) {
        Renderer.renderSide(emitter, side, getTexture());
    }

    public abstract Texture getTexture();
}
