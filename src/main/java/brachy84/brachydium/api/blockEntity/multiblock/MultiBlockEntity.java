package brachy84.brachydium.api.blockEntity.multiblock;

import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;

public abstract class MultiBlockEntity extends MetaBlockEntity {

    private WorldStructure structure;

    public MultiBlockEntity(Identifier id) {
        super(id);
    }

    @Override
    public void onAttach() {
        structure.forEach(blockState -> {
            ((IMultiblockBlockInfo) blockState.getBlock()).setMultiBlock(this);
        });
    }

    public void sendBlockStateUpdate(Vec3i vec3i, BlockState state) {

    }


}
