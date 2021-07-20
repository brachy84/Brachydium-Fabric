package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.util.Face;
import net.minecraft.util.math.Direction;

public interface IOrientable {

    Direction getFrontFace();

    default Direction getFaceDirection(Face face) {
        return face.getDirection(getFrontFace());
    }

    void setFrontFace(Direction direction);
}
