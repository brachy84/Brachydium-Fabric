package brachy84.brachydium.api.fluid;

import brachy84.brachydium.api.util.MatchingType;
import brachy84.brachydium.client.BrachydiumClient;
import com.google.common.collect.Lists;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.fractions.Fraction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class FluidStack {

    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    public static final int DENOMINATOR = 81;

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

    public List<Text> getTooltipLines() {
        List<Text> lines = new ArrayList<>();
        boolean showAdvanced = MinecraftClient.getInstance().options.advancedItemTooltips;
        lines.add(fluid.getDefaultState().getBlockState().getBlock().getName());
        lines.add(getTextAmount());
        if(showAdvanced) {
            lines.add((new LiteralText(Registry.FLUID.getId(this.getFluid()).toString())).formatted(Formatting.DARK_GRAY));
        }
        lines.add(BrachydiumClient.getModIdForTooltip(Registry.FLUID.getId(getFluid()).getNamespace()));
        return lines;
    }

    public Text getTextAmount() {
        return new LiteralText(getAmount() / DENOMINATOR + "mb");
    }

    public Identifier getId() {
        if(id == null) {
            id = Registry.FLUID.getId(fluid);
        }
        return id;
    }

    public void writeData(PacketByteBuf buf) {
        if (isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeInt(Registry.FLUID.getRawId(fluid));
            buf.writeInt(getAmount());
        }
    }

    public static FluidStack readData(PacketByteBuf buf) {
        if (!buf.readBoolean()) {
            return FluidStack.EMPTY;
        } else {
            int id = buf.readInt();
            int count = buf.readInt();
            return new FluidStack(Registry.FLUID.get(id), count);
        }
    }

    @Override
    public String toString() {
        return getId().getPath() + " " + amount;
    }

    public List<EntryStack> toEntryStack() {
        return Lists.newArrayList(EntryStack.create(fluid, Fraction.ofWhole(1000)));
    }

    public CompoundTag toTag(CompoundTag tag) {
        Identifier id = getId();
        tag.putString("id", id == null ? "minecraft:empty" : id.toString());
        tag.putInt("amount", amount);
        return tag;
    }
}
