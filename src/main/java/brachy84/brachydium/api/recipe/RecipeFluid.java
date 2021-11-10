package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.unification.LoadableTag;
import com.google.common.collect.Lists;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RecipeFluid implements Predicate<FluidStack>, Iterable<FluidStack> {

    private final List<FluidStack> values;
    private final List<Tag<Fluid>> tags;
    private final List<FluidStack> allValid = new ArrayList<>();
    private final int amount;
    private final float chance;

    private boolean loadedLoadables;

    public RecipeFluid(List<FluidStack> values, List<Tag<Fluid>> tags, int amount, float chance) {
        this.values = Objects.requireNonNull(values);
        this.tags = Objects.requireNonNull(tags);
        this.amount = amount;
        this.chance = chance;
        if(values.size() == 0 && tags.size() == 0) {
            throw new IllegalArgumentException("Ingredient can't be empty");
        }
        if(amount < 0)
            throw new IllegalArgumentException("Amount in ingredient can't be null");
        loadedLoadables = LoadableTag.isLoaded();
        buildValidList();
    }

    public RecipeFluid(int amount, float chance, Tag<Fluid>... tags) {
        this(new ArrayList<>(), Lists.newArrayList(tags), amount, chance);
    }

    public RecipeFluid(int amount, float chance, FluidStack... values) {
        this(Lists.newArrayList(values), new ArrayList<>(), amount, chance);
    }

    public RecipeFluid(FluidStack stack, float chance) {
        this(stack.getAmount(), chance, stack);
    }

    public RecipeFluid(FluidStack stack) {
        this(stack.getAmount(), 1f, stack);
    }

    public static RecipeFluid ofTagId(Identifier id, int amount, float chance) {
        return new RecipeFluid(amount, chance, LoadableTag.getFluidTag(id));
    }

    @Override
    public boolean test(FluidStack stack) {
        if(stack == null || stack.isEmpty())
            return false;
        for(FluidStack t : getAllValid()) {
            if(areStacksEqual(stack, t))
                return true;
        }
        return false;
    }

    public int getAmount() {
        return amount;
    }

    public float getChance() {
        return chance;
    }

    private static boolean areStacksEqual(FluidStack stack, FluidStack stack1) {
        if(stack.getFluid() != stack1.getFluid())
            return false;
        NbtCompound nbt = stack.getNbt();
        NbtCompound nbt1 = stack1.getNbt();
        if(nbt == null && nbt1 == null)
            return true;
        if(nbt == null || nbt1 == null)
            return false;
        return nbt.equals(nbt1);
    }

    public List<FluidStack> getAllValid() {
        if(!loadedLoadables && LoadableTag.isLoaded()) {
            buildValidList();
            loadedLoadables = true;
        }
        return Collections.unmodifiableList(allValid);
    }

    private void buildValidList() {
        allValid.clear();
        allValid.addAll(values);
        for(Tag<Fluid> tag : tags) {
            allValid.addAll(tag.values().stream().map(FluidStack::new).collect(Collectors.toList()));
        }
    }

    @NotNull
    @Override
    public Iterator<FluidStack> iterator() {
        return getAllValid().iterator();
    }

    public static class Builder {
        private final List<FluidStack> values = new ArrayList<>();
        private final List<Tag<Fluid>> tags= new ArrayList<>();
        private int amount = 0;
        private float chance = 1f;

        public Builder() {
        }

        public Builder setAmount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder setChance(float chance) {
            this.chance = chance;
            return this;
        }

        public Builder setNotConsumed() {
            this.chance = 0f;
            return this;
        }

        public Builder addValues(FluidStack... values) {
            for(FluidStack t : values) {
                this.values.add(Objects.requireNonNull(t));
            }
            return this;
        }

        public Builder addTags(Tag<Fluid>... values) {
            for(Tag<Fluid> t : values) {
                this.tags.add(Objects.requireNonNull(t));
            }
            return this;
        }

        public RecipeFluid build() {
            return new RecipeFluid(values, tags, amount, chance);
        }
    }
}
