package brachy84.brachydium.api.unification.material.properties;

import brachy84.brachydium.api.fluid.FluidStack;
import com.google.common.base.Preconditions;
import net.minecraft.fluid.Fluid;
import org.jetbrains.annotations.NotNull;

public class PlasmaProperty implements IMaterialProperty<PlasmaProperty> {

    /**
     * Internal material plasma fluid field
     */
    private Fluid plasma;

    @Override
    public void verifyProperty(MaterialProperties properties) {
    }

    /**
     * internal usage only
     */
    public void setPlasma(@NotNull Fluid plasma) {
        Preconditions.checkNotNull(plasma);
        this.plasma = plasma;
    }

    public Fluid getPlasma() {
        return plasma;
    }

    @NotNull
    public FluidStack getPlasma(int amount) {
        return new FluidStack(plasma, amount);
    }
}
