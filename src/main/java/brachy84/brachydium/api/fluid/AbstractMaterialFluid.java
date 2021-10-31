package brachy84.brachydium.api.fluid;

import brachy84.brachydium.api.unification.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class AbstractMaterialFluid extends FlowableFluid {

    protected final Material material;

    protected AbstractMaterialFluid(Material material) {
        this.material = material;
    }


    @Override
    protected boolean isInfinite() {
        return false;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {

    }

    @Override
    protected int getFlowSpeed(WorldView world) {
        return 0;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 2;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 20;
    }

    @Override
    protected float getBlastResistance() {
        return 100;
    }
}
