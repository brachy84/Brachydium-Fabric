package brachy84.brachydium.api.render;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

public class OrientedCubeRender extends Renderer{

    private Map<Face, Texture> textures = new HashMap<>();

    public OrientedCubeRender(Texture texture) {
        for(Face face : Face.values()) {
            textures.put(face, texture);
        }
    }

    public OrientedCubeRender(Texture front, Texture top, Texture side) {
        textures.put(Face.FRONT, front);
        textures.put(Face.TOP, top);
        textures.put(Face.SIDE, side);
        textures.put(Face.BACK, side);
        textures.put(Face.BOTTOM, side);
    }

    public OrientedCubeRender(Texture front, Texture back, Texture top, Texture bottom, Texture side) {
        textures.put(Face.FRONT, front);
        textures.put(Face.TOP, top);
        textures.put(Face.SIDE, side);
        textures.put(Face.BACK, back);
        textures.put(Face.BOTTOM, bottom);
    }

    public void addTexture(Face direction, Texture texture) {
        textures.put(direction, texture);
    }

    @Override
    public void render(QuadEmitter emitter, Direction frontFacing) {
        for(Direction direction : Direction.values()) {
            renderSide(emitter, direction, getSprite(direction, frontFacing));
        }
    }

    public Sprite getSprite(Direction dir, Direction front) {
        return textures.get(Face.getFace(dir, front)).getSprite();
    }

    public static enum Face {
        FRONT, BACK, TOP, BOTTOM, SIDE;

        public static Face getFace(Direction direction, Direction front) {
            if(direction == front)
                return FRONT;
            else if(direction.getOpposite() == front)
                return BACK;
            else if(direction == Direction.UP)
                return TOP;
            else if(direction == Direction.DOWN)
                return BOTTOM;
            else
                return SIDE;
        }
    }
}
