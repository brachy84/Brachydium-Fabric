package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.fluid.FluidStack;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FluidTankList implements ArrayParticipant<Fluid> {

    private final List<Slot<Fluid>> tanks;
    private final boolean extractable, insertable;
    private final int capacity;

    public FluidTankList(int tanks, int capacity, boolean extractable, boolean insertable) {
        this.tanks = DefaultedList.ofSize(tanks, new FluidTank(capacity, extractable, insertable));
        this.capacity = capacity;
        this.extractable = extractable;
        this.insertable = insertable;
    }

    public FluidTankList(int capacity, boolean extractable, boolean insertable, FluidStack... stacks) {
        this.tanks = Arrays.stream(stacks).map(stack -> new FluidTank(stack, capacity, extractable, insertable)).collect(Collectors.toList());
        this.capacity = capacity;
        this.extractable = extractable;
        this.insertable = insertable;
    }

    public static FluidTankList fromTag(CompoundTag tag) {
        int size = tag.getInt("slots");
        ListTag list = (ListTag) tag.get("content");
        assert list != null;
        FluidStack[] stacks = new FluidStack[size];
        for(int i = 0; i < size; i++) {
            CompoundTag tag1 = (CompoundTag) list.get(i);
            stacks[i] = FluidStack.fromTag(tag1);
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
    public List<Slot<Fluid>> getSlots() {
        return Collections.unmodifiableList(tanks);
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
        if(!supportsExtraction()) return;
        ArrayParticipant.super.extract(transaction, insertable);
    }

    @Override
    public int extract(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
        if(!supportsExtraction()) return 0;
        return ArrayParticipant.super.extract(transaction, type, quantity);
    }

    @Override
    public int insert(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
        if(!supportsInsertion()) return 0;
        return ArrayParticipant.super.insert(transaction, type, quantity);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("slots", getSlots().size());
        tag.putBoolean("ins", insertable);
        tag.putBoolean("ext", extractable);
        tag.putInt("capacity", capacity);
        ListTag list = new ListTag();
        for(Slot<Fluid> slot : getSlots()) {
            FluidStack stack = new FluidStack(slot.getKey(null), slot.getQuantity(null));
            list.add(stack.toTag(new CompoundTag()));
        }
        tag.put("content", list);
        return tag;
    }
}
