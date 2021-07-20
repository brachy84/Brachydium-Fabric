package brachy84.brachydium.mixin;

import brachy84.brachydium.api.blockEntity.multiblock.IMultiblockBlockInfo;
import brachy84.brachydium.api.blockEntity.multiblock.MultiBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock implements IMultiblockBlockInfo {

    private MultiBlockEntity multiBlockEntity;

    private BlockMixin() {
        super(null);
    }

    @Override
    public MultiBlockEntity getMultiBlock() {
        return multiBlockEntity;
    }

    @Override
    public void setMultiBlock(MultiBlockEntity multiBlockEntity) {
        this.multiBlockEntity = multiBlockEntity;
    }

    @Override
    public void removeMultiBlock() {
        multiBlockEntity = null;
    }

    @Override
    public boolean hasMultiBlock() {
        return multiBlockEntity != null;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!hasMultiBlock()) return;
        getMultiBlock().sendBlockStateUpdate(pos.subtract(multiBlockEntity.getPos()), newState);
    }
}
