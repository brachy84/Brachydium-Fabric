package brachy84.brachydium.gui.wrapper;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.handlers.McInventory;
import brachy84.brachydium.api.handlers.PlayerInventoryParticipant;
import brachy84.brachydium.gui.widgets.ItemSlotWidget;
import com.mojang.datafixers.util.Pair;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class McSlot extends Slot {

    private ArrayParticipant<ItemKey> inv;
    private ItemSlotWidget slotWidget;
    private boolean canInsert, canTake;

    public McSlot(ItemSlotWidget slotWidget, ArrayParticipant<ItemKey> inv, int index, boolean canInsert, int x, int y) {
        super(new McInventory(inv), index, x, y);
        this.inv = inv;
        this.slotWidget = slotWidget;
        this.canInsert = canInsert;
        this.canTake = true;
    }

    public void setInsertable(boolean canInsert) {
        this.canInsert = canInsert;
    }

    public void setExtractable(boolean canTake) {
        this.canTake = canTake;
    }

    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.markDirty();
    }

    public boolean canInsert(ItemStack stack) {
        return canInsert;
    }

    public ItemStack getStack() {
        return slotWidget.getResource();
        //return this.inventory.getStack(getIndex());
    }

    public boolean hasStack() {
        return !this.getStack().isEmpty();
    }

    public void setStack(ItemStack stack) {
        //this.inventory.setStack(getIndex(), stack);
        slotWidget.setResource(stack);
        this.markDirty();
    }

    public void markDirty() {
        this.inventory.markDirty();
    }

    public int getMaxItemCount() {
        return this.inventory.getMaxCountPerStack();
    }

    public int getMaxItemCount(ItemStack stack) {
        return Math.min(this.getMaxItemCount(), stack.getMaxCount());
    }

    @Nullable
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return null;
    }

    public ItemStack takeStack(int amount) {
        Brachydium.LOGGER.info("Taking stack * {}", amount);
        return this.inventory.removeStack(getIndex(), amount);
    }

    public boolean canTakeItems(PlayerEntity playerEntity) {
        return canTake;
    }

    public boolean isEnabled() {
        return true;
    }

    public Optional<ItemStack> tryTakeStackRange(int min, int max, PlayerEntity player) {
        Brachydium.LOGGER.info("Try taking stack range {} - {} in {}", min, max, getIndex());
        if (!this.canTakeItems(player)) {
            return Optional.empty();
        } else if (!this.canTakePartial(player) && max < this.getStack().getCount()) {
            return Optional.empty();
        } else {
            min = Math.min(min, max);
            ItemStack itemStack = this.takeStack(min);
            if (this.getStack().isEmpty()) {
                this.setStack(ItemStack.EMPTY);
            }

            return Optional.of(itemStack);
        }
    }

    public ItemStack takeStackRange(int min, int max, PlayerEntity player) {
        Optional<ItemStack> optional = this.tryTakeStackRange(min, max, player);
        optional.ifPresent((stack) -> {
            this.onTakeItem(player, stack);
        });
        return (ItemStack)optional.orElse(ItemStack.EMPTY);
    }

    public ItemStack insertStack(ItemStack stack) {
        return this.insertStack(stack, stack.getCount());
    }

    public ItemStack insertStack(ItemStack stack, int count) {
        Brachydium.LOGGER.info("Try inserting {} * {}", stack, count);
        if (!stack.isEmpty() && this.canInsert(stack)) {
            ItemStack itemStack = this.getStack();
            int i = Math.min(Math.min(count, stack.getCount()), this.getMaxItemCount(stack) - itemStack.getCount());
            Brachydium.LOGGER.info("Current stack: {} | i: {}", itemStack, i);
            if (itemStack.isEmpty()) {
                this.setStack(stack.split(i));
            } else if (ItemStack.canCombine(itemStack, stack)) {
                stack.decrement(i);
                itemStack.increment(i);
                this.setStack(itemStack);
            }

            return stack;
        } else {
            return stack;
        }
    }

    public boolean canTakePartial(PlayerEntity player) {
        return this.canTakeItems(player) && this.canInsert(this.getStack());
    }

    public ItemSlotWidget getSlotWidget() {
        return slotWidget;
    }
}
