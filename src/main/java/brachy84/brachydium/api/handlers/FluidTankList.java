package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.blockEntity.InventoryListener;
import brachy84.brachydium.api.fluid.FluidStack;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.DiffKey;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

public class FluidTankList implements ArrayParticipant<Fluid>, InventoryListener {

    private final DiffKey.Array<FluidStack> fluids;
    private final boolean extractable, insertable;
    private final int capacity;

    public FluidTankList(int tanks, int capacity, boolean extractable, boolean insertable) {
        this.fluids = new DiffKey.Array<>(DefaultedList.ofSize(tanks, FluidStack.EMPTY));
        this.capacity = capacity;
        this.extractable = extractable;
        this.insertable = insertable;
    }

    public FluidTankList(int capacity, boolean extractable, boolean insertable, FluidStack... stacks) {
        this.fluids = new DiffKey.Array<>(Arrays.asList(stacks));
        this.capacity = capacity;
        this.extractable = extractable;
        this.insertable = insertable;
    }

    public static FluidTankList fromTag(NbtCompound tag) {
        int size = tag.getInt("slots");
        NbtList list = (NbtList) tag.get("content");
        assert list != null;
        FluidStack[] stacks = new FluidStack[size];
        for(int i = 0; i < size; i++) {
            NbtCompound tag1 = (NbtCompound) list.get(i);
            stacks[i] = FluidStack.fromNbt(tag1);
        }
        return new FluidTankList(tag.getInt("capacity"), tag.getBoolean("ext"), tag.getBoolean("ins"), stacks);
    }

    public static FluidTankList importTanks(int tanks, int capacity) {
        return new FluidTankList(tanks, capacity, false, true);
    }

    public static FluidTankList exportTanks(int tanks, int capacity) {
        return new FluidTankList(tanks, capacity, true, false);
    }

    public static FluidTankList importTanks(int tanks) {
        return importTanks(tanks, 64 * 81000);
    }

    public static FluidTankList exportTanks(int tanks) {
        return exportTanks(tanks, 64 * 81000);
    }

    @Override
    public void addListener(Runnable runnable) {
        fluids.onApply(runnable);
    }

    @Override
    public List<Slot<Fluid>> getSlots() {
        return new AbstractList<Slot<Fluid>>() {
            @Override
            public Slot<Fluid> get(int index) {
                return new FluidTank(fluids, index, capacity, extractable, insertable);
            }

            @Override
            public int size() {
                return fluids.get(null).size();
            }
        };
    }

    @Override
    public boolean supportsExtraction() {
        return extractable;
    }

    @Override
    public boolean supportsInsertion() {
        return insertable;
    }

    @Override
    public void extract(@Nullable Transaction transaction, Insertable<Fluid> insertable) {
        ArrayParticipant.super.extract(transaction, insertable);
    }

    @Override
    public int extract(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
        return ArrayParticipant.super.extract(transaction, type, quantity);
    }

    @Override
    public int insert(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
        return ArrayParticipant.super.insert(transaction, type, quantity);
    }

    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putInt("slots", getSlots().size());
        tag.putBoolean("ins", insertable);
        tag.putBoolean("ext", extractable);
        tag.putInt("capacity", capacity);
        NbtList list = new NbtList();
        for(Slot<Fluid> slot : getSlots()) {
            FluidStack stack = new FluidStack(slot.getKey(null), slot.getQuantity(null));
            list.add(stack.writeNbt(new NbtCompound()));
        }
        tag.put("content", list);
        return tag;
    }
}
