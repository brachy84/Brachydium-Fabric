package brachy84.brachydium.api.fluid;

import brachy84.brachydium.api.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class MaterialFluid extends AbstractMaterialFluid {

    private Fluid still;
    private Fluid flowing;
    private Block block;
    private BucketItem item;

    protected MaterialFluid(Material material) {
        super(material);
    }

    @Override
    public Fluid getFlowing() {
        return flowing;
    }

    @Override
    public Fluid getStill() {
        return still;
    }

    @Override
    public Item getBucketItem() {
        return item;
    }

    public void setData(Fluid still, Fluid flowing, BucketItem item) {
        this.still = still;
        this.flowing = flowing;
        this.item = item;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return block.getDefaultState().with(Properties.LEVEL_15, method_15741(state));
    }

    public static class Flowing extends MaterialFluid {

        public Flowing(Material material) {
            super(material);
        }

        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }
    }

    public static class Still extends MaterialFluid {

        public Still(Material material) {
            super(material);
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }

        @Override
        public int getLevel(FluidState state) {
            return 8;
        }
    }
}
