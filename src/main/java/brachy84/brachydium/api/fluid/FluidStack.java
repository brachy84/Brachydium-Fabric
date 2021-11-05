package brachy84.brachydium.api.fluid;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.util.MatchingType;
import brachy84.brachydium.client.BrachydiumClient;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FluidStack {

    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    public static final int DENOMINATOR = 81;

    public static boolean matchesStackExact(FluidStack stack1, FluidStack stack2) {
        if(stack1.isEmpty() && stack2.isEmpty()) return true;
        return matchesStack(stack1, stack2) && stack1.amount == stack2.amount;
    }

    public static boolean matchesStack(FluidStack stack1, FluidStack stack2) {
        return stack1.fluid == stack2.fluid && matchesNbt(stack1, stack2);
    }

    public static boolean matchesNbt(FluidStack stack1, FluidStack stack2) {
        boolean nbt1 = stack1.hasNbt(), nbt2 = stack2.hasNbt();
        if(!nbt1 && !nbt2) return true;
        if(nbt1 ^ nbt2) return false;
        return stack1.nbt.equals(stack2.nbt);
    }

    private int amount;
    private final Fluid fluid;
    @Nullable
    private NbtCompound nbt;
    private Identifier id;

    public FluidStack(Fluid fluid) {
        this(fluid, 1, null);
    }

    public FluidStack(Fluid fluid, int amount) {
        this(fluid, amount, null);
    }

    public FluidStack(Fluid fluid, int amount, @Nullable NbtCompound nbt) {
        this.fluid = fluid;
        this.amount = Math.max(0, amount);
        this.nbt = nbt;
    }

    public FluidStack(FluidVariant variant, long amount) {
        this(variant.getFluid(), (int) amount, variant.copyNbt());
    }

    public FluidVariant asFluidVariant() {
        return FluidVariant.of(fluid, nbt == null ? null : nbt.copy());
    }

    /*public static FluidStack fromNbt(NbtCompound tag) {
        Fluid fluid = Registry.FLUID.get(new Identifier(tag.getString("id")));
        return new FluidStack(fluid, tag.getInt("amount"));
    }*/

    public boolean isEmpty() {
        return amount <= 0 || fluid == Fluids.EMPTY;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(amount, 0);
    }

    public int getAmount() {
        return amount;
    }

    public void increment(int amount) {
        this.amount += amount;
    }

    public void decrement(int amount) {
        this.amount -= Math.min(amount, this.amount);
    }

    public Fluid getFluid() {
        return fluid;
    }

    public boolean isEqual(Fluid fluid) {
        return this.fluid == fluid;
    }

    public boolean isEqual(FluidStack fluidStack) {
        return this.fluid == fluidStack.fluid && amount == fluidStack.amount && matchesNbt(this, fluidStack);
    }

    public FluidStack copy() {
        return new FluidStack(fluid, amount, nbt == null ? null : nbt.copy());
    }

    public FluidStack copyWith(int amount) {
        return new FluidStack(fluid, amount, nbt);
    }

    public FluidStack copyWith(NbtCompound nbt) {
        return new FluidStack(fluid, amount, nbt);
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

    @Nullable
    public NbtCompound getNbt() {
        return nbt;
    }

    public boolean hasNbt() {
        return nbt != null && !nbt.isEmpty();
    }

    public NbtCompound getOrCreateNbt() {
        if(!hasNbt())
            this.nbt = new NbtCompound();
        return nbt;
    }

    public void writeData(PacketByteBuf buf) {
        if (isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeInt(Registry.FLUID.getRawId(fluid));
            buf.writeInt(getAmount());
            buf.writeNbt(nbt);
        }
    }

    public static FluidStack readData(PacketByteBuf buf) {
        if (!buf.readBoolean()) {
            return FluidStack.EMPTY;
        } else {
            int id = buf.readInt();
            int count = buf.readInt();
            return new FluidStack(Registry.FLUID.get(id), count, buf.readNbt());
        }
    }

    @Override
    public String toString() {
        return getId().getPath() + " " + amount;
    }

    public EntryIngredient toEntryStack() {
        return EntryIngredients.of(toArchitecturyFLuidStack());
    }

    public dev.architectury.fluid.FluidStack toArchitecturyFLuidStack() {
        return dev.architectury.fluid.FluidStack.create(fluid, amount, nbt);
    }

    public NbtCompound toNbt() {
        NbtCompound result = new NbtCompound();
        result.putString("fluid", Registry.FLUID.getId(fluid).toString());
        result.putInt("amount", amount);
        if (nbt != null) {
            result.put("tag", nbt.copy());
        }
        return result;
    }

    public static FluidStack fromNbt(NbtCompound compound) {
        try {
            Fluid fluid = Registry.FLUID.get(new Identifier(compound.getString("fluid")));
            NbtCompound nbt = compound.contains("tag") ? compound.getCompound("tag") : null;
            return new FluidStack(fluid, compound.getInt("amount"), nbt);
        } catch (RuntimeException runtimeException) {
            Brachydium.LOGGER.debug("Tried to load an invalid FluidVariant from NBT: {}", compound, runtimeException);
            return FluidStack.EMPTY;
        }
    }

    @Deprecated
    public NbtCompound writeNbt(NbtCompound tag) {
        Identifier id = getId();
        tag.putString("id", id == null ? "minecraft:empty" : id.toString());
        tag.putInt("amount", amount);
        return tag;
    }

    /**
     * ignore amount
     * supports {@link FluidVariant} and {@link dev.architectury.fluid.FluidStack}
     * use {@link #equalsExact(Object)} if you don't want to ignore amount
     * @param o object
     * @return if objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o instanceof FluidStack)
            return matchesStack(this, (FluidStack) o);
        if(o instanceof FluidVariant) {
            FluidVariant fluid = (FluidVariant) o;
            if(isEmpty() && fluid.isBlank()) return true;
            if(getFluid() != fluid.getFluid()) return false;
            boolean nbt1 = hasNbt(), nbt2 = fluid.hasNbt() && !fluid.getNbt().isEmpty();
            if(!nbt1 && !nbt2) return true;
            if(nbt1 ^ nbt2) return false;
            return nbt.equals(fluid.getNbt());
        }
        if(o instanceof dev.architectury.fluid.FluidStack) {
            dev.architectury.fluid.FluidStack stack = (dev.architectury.fluid.FluidStack) o;
            if(isEmpty() && stack.isEmpty()) return true;
            if(getFluid() != stack.getFluid()) return false;
            boolean nbt1 = hasNbt(), nbt2 = stack.hasTag() && !stack.getTag().isEmpty();
            if(!nbt1 && !nbt2) return true;
            if(nbt1 ^ nbt2) return false;
            return nbt.equals(stack.getTag());
        }
        return false;
    }

    public boolean equalsExact(Object o) {
        if(this == o) return true;
        if(!equals(o)) return false;
        if(o instanceof FluidStack)
            return amount == ((FluidStack) o).amount;
        if(o instanceof dev.architectury.fluid.FluidStack)
            return amount == ((dev.architectury.fluid.FluidStack) o).getAmount();
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fluid, nbt);
    }
}
