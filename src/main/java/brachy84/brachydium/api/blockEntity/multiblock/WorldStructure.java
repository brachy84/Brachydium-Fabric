package brachy84.brachydium.api.blockEntity.multiblock;

import com.mojang.datafixers.kinds.IdF;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class WorldStructure {

    private Multiblock multiblock;

    private BlockState[][][] structure;
    private World world;

    public WorldStructure(World world, BlockState[][][] structure) {
        this.world = world;
        this.structure = structure;
    }

    public BlockState getBlockStateAt(Vec3i vec3i) {
        return structure[vec3i.getX()][vec3i.getY()][vec3i.getZ()];
    }

    public World getWorld() {
        return world;
    }

    public void forEach(Consumer<BlockState> consumer) {
        for(int x = 0; x < structure.length; x++) {
            for(int y = 0; y < structure[x].length; y++) {
                for(int z = 0; z < structure[x][y].length; z++) {
                    consumer.accept(structure[x][y][z]);
                }
            }
        }
    }

    public boolean isValid(Vec3i vec3i, BlockState blockState) {
        return multiblock.isValid(vec3i, blockState);
    }
}
