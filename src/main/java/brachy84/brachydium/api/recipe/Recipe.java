package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.item.CountableIngredient;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 *  Has to extend vanilla recipe for REI
 */
public class Recipe {

    private final String id;
    private final List<CountableIngredient> inputs;
    private final List<ItemStack> outputs;
    private final List<FluidStack> fluidInputs;
    private final List<FluidStack> fluidOutputs;

    private final int EUt, duration;
    private final boolean hidden;

    protected Recipe(String name, List<CountableIngredient> inputs, List<ItemStack> outputs, List<FluidStack> fluidInputs, List<FluidStack> fluidOutputs, int eUt, int duration, boolean hidden) {
        this.id = name;
        this.inputs = inputs;
        this.outputs = outputs;
        this.fluidInputs = fluidInputs;
        this.fluidOutputs = fluidOutputs;
        EUt = eUt;
        this.duration = duration;
        this.hidden = hidden;
    }

    public String getName() {
        return id;
    }

    public List<CountableIngredient> getInputs() {
        return Collections.unmodifiableList(inputs);
    }

    public List<ItemStack> getOutputs() {
        return Collections.unmodifiableList(outputs);
    }

    public List<FluidStack> getFluidInputs() {
        return Collections.unmodifiableList(fluidInputs);
    }

    public List<FluidStack> getFluidOutputs() {
        return Collections.unmodifiableList(fluidOutputs);
    }

    public int getEUt() {
        return EUt;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isHidden() {
        return hidden;
    }
}
