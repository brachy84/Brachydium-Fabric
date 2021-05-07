package brachy84.brachydium.api.handlers;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluidStack {

    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    private int amount;
    private final Fluid fluid;
    private Identifier id;

    public FluidStack(Fluid fluid, int amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    public FluidStack(Fluid fluid) {
        this(fluid, 1);
    }

    public boolean isEmpty() {
        return amount == 0 || fluid == Fluids.EMPTY;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(amount, 0);
    }

    public int getAmount() {
        return amount;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public boolean isEqual(Fluid fluid) {
        return this.fluid == fluid;
    }

    public boolean isEqual(FluidStack fluidStack) {
        return this.fluid == fluidStack.fluid && amount == fluidStack.amount;
    }

    public FluidStack copy() {
        return new FluidStack(fluid, amount);
    }

    public void renderInGui() {

    }

    public Identifier getId() {
        if(id == null) {
            id = Registry.FLUID.getId(fluid);
        }
        return id;
    }
}
