package brachy84.brachydium.api.fluid;

import brachy84.brachydium.api.util.MatchingType;
import com.google.common.collect.Lists;
import me.shedaniel.rei.api.EntryStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class FluidStack {

    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    private int amount;
    private final Fluid fluid;
    private Identifier id;

    public FluidStack(Fluid fluid, int amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    public static FluidStack fromTag(CompoundTag tag) {
        Fluid fluid = Registry.FLUID.get(new Identifier(tag.getString("id")));
        return new FluidStack(fluid, tag.getInt("amount"));
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

    public boolean matches(FluidStack stack, MatchingType type) {
        if(type == MatchingType.EXACT)
            return amount == stack.getAmount() && stack.getFluid() == fluid;
        else if(type == MatchingType.AT_LEAST)
            return amount <= stack.getAmount() && stack.getFluid() == fluid;
        else if(type == MatchingType.IGNORE_AMOUNT)
            return stack.getFluid() == fluid;
        return false;
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

    @Override
    public String toString() {
        return getId().getPath() + " " + amount;
    }

    public List<EntryStack> toEntryStack() {
        return Lists.newArrayList(EntryStack.create(fluid, amount));
    }

    public CompoundTag toTag(CompoundTag tag) {
        Identifier id = getId();
        tag.putString("id", id == null ? "minecraft:empty" : id.toString());
        tag.putInt("amount", amount);
        return tag;
    }
}
