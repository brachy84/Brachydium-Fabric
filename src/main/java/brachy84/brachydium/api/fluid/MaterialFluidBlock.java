package brachy84.brachydium.api.fluid;

import brachy84.brachydium.api.unification.material.Material;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;

public class MaterialFluidBlock extends FluidBlock {

    private final Material material;

    public MaterialFluidBlock(FlowableFluid fluid, Material material) {
        super(fluid, FabricBlockSettings.copy(Blocks.WATER));
        this.material = material;
    }
}
