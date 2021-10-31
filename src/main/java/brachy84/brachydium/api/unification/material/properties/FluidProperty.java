package brachy84.brachydium.api.unification.material.properties;

import brachy84.brachydium.api.fluid.FluidStack;
import com.google.common.base.Preconditions;
import net.minecraft.fluid.Fluid;
import org.jetbrains.annotations.NotNull;

public class FluidProperty implements IMaterialProperty<FluidProperty> {

    public static final int BASE_TEMP = 300;

    /**
     * Internal material fluid field
     */
    private Fluid fluid;

    private boolean hasBlock;
    private boolean isGas;
    private int fluidTemperature = BASE_TEMP;

    public FluidProperty(boolean isGas, boolean hasBlock) {
        this.isGas = isGas;
        this.hasBlock = hasBlock;
    }

    /**
     * Default values of: no Block, not Gas.
     */
    public FluidProperty() {
        this(false, false);
    }

    public boolean isGas() {
        return isGas;
    }

    /**
     * internal usage only
     */
    public void setFluid(@NotNull Fluid materialFluid) {
        Preconditions.checkNotNull(materialFluid);
        this.fluid = materialFluid;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public boolean hasBlock() {
        return hasBlock;
    }

    public void setHasBlock(boolean hasBlock) {
        this.hasBlock = hasBlock;
    }

    public void setIsGas(boolean isGas) {
        this.isGas = isGas;
    }

    @NotNull
    public FluidStack getFluid(int amount) {
        return new FluidStack(fluid, amount);
    }

    public void setFluidTemperature(int fluidTemperature) {
        Preconditions.checkArgument(fluidTemperature > 0, "Invalid temperature");
        this.fluidTemperature = fluidTemperature;
    }

    public int getFluidTemperature() {
        return fluidTemperature;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        if (properties.hasProperty(PropertyKey.PLASMA)) {
            hasBlock = false;
        }
    }
}
