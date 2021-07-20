package brachy84.brachydium.api.cover;

import brachy84.brachydium.Brachydium;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class CoverableApi {

    public static final Identifier ID = Brachydium.id("coverable_api");

    public static final BlockApiLookup<ICoverable, Direction> LOOKUP = BlockApiLookup.get(ID, ICoverable.class, Direction.class);

    public static void init() {

    }
}
