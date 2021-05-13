package brachy84.brachydium.api.cover;

import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

public interface ICoverable {

    Map<Direction, Cover> covers = new HashMap<>();

    default Cover getCover(Direction direction) {
        return covers.get(direction);
    }

    boolean canPutCover(Cover cover);

    default Map<Direction, Cover> getCovers() {
        return covers;
    }
}
