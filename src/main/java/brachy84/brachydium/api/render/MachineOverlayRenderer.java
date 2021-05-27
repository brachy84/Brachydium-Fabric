package brachy84.brachydium.api.render;

import brachy84.brachydium.api.util.Face;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.math.Direction;

public class MachineOverlayRenderer extends OrientedOverlayRenderer {

    private final MachineOverlayTexture texture;

    public MachineOverlayRenderer(MachineOverlayTexture machineOverlayTexture) {
        super(null);
        this.texture = machineOverlayTexture;
    }

    public void render(QuadEmitter emitter, Direction frontFacing, boolean isActive) {
        renderSide(emitter, Face.FRONT.getDirection(frontFacing), texture.getFront(isActive));
        renderSide(emitter, Direction.UP, texture.getTop(isActive));
        renderSide(emitter, Face.LEFT.getDirection(frontFacing), texture.getSide(isActive));
        renderSide(emitter, Face.RIGHT.getDirection(frontFacing), texture.getSide(isActive));
    }

    @Override
    public void render(QuadEmitter emitter, Direction frontFacing) {
        render(emitter, frontFacing, false);
    }
}
