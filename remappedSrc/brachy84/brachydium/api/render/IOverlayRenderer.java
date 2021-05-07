package brachy84.brachydium.api.render;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

public interface IOverlayRenderer {

    Map<Direction, Identifier> getOverlays();

    void render();
}
