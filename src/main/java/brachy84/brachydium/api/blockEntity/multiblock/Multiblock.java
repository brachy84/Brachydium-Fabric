package brachy84.brachydium.api.blockEntity.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.Vec3i;

public class Multiblock {

    private BlockStatePredicate[][][] states;

    public BlockStatePredicate getPredicate(Vec3i vec3i) {
        return states[vec3i.getX()][vec3i.getY()][vec3i.getZ()];
    }

    public boolean isValid(Vec3i vec3i, BlockState state) {
        return getPredicate(vec3i).test(state);
    }

}
