package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.item.FluidCell;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
//import io.github.astrarre.transfer.internal.compat.PlayerInventoryParticipant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidCellHandler implements Participant<Fluid> {

    private final FluidCell cell;
    public final ReplacingParticipant<ItemKey> context;
    private final int capacity;

    public final Key.Object<ItemKey> current;

    public FluidCellHandler(FluidCell cell, ReplacingParticipant<ItemKey> context, ItemKey current) {
        this.context = context;
        this.current = new ObjectKeyImpl<>(current);
        this.cell = cell;
        this.capacity = cell.getCapacity();
    }

    @Nullable
    public static Participant<Fluid> getCellParticipant(@Nullable Direction direction, ItemStack stack, PlayerEntity player) {
        return FabricParticipants.FLUID_ITEM.get().get(direction, ItemKey.of(stack), stack.getCount(), new PlayerInventoryParticipant(player.getInventory()).getCursorItemReplacingParticipant());
    }

    public Fluid getFluid(Transaction transaction) {
        return Registry.FLUID.get(new Identifier(current.get(transaction).getTag().getString("fluid")));
    }

    @Override
    public void extract(@Nullable Transaction transaction, Insertable<Fluid> insertable) {
        ItemKey current = this.current.get(transaction);
        Fluid fluid = getFluid(transaction);
        if (fluid != Fluids.EMPTY) {
            int amount = current.getTag().getInt("amount");
            try (Transaction action = Transaction.create()) {
                int remainder = amount - insertable.insert(action, fluid, amount);
                ItemKey newKey;
                if (remainder == 0) {
                    newKey = ItemKey.of(cell);
                } else {
                    newKey = current.withTag(current.getTag().toBuilder().putInt("amount", remainder));
                }
                if (!this.context.replace(action, current, 1, newKey, 1)) {
                    action.abort();
                }
            }
        }
    }

    @Override
    public int insert(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
        if (quantity == 0 || Fluids.EMPTY == type) {
            return 0;
        }
        ItemKey current = this.current.get(transaction);
        Fluid fluid = Registry.FLUID.get(new Identifier(current.getTag().getString("fluid")));
        if (fluid == Fluids.EMPTY || fluid == type) {
            int count = current.getTag().getInt("amount");
            count = Math.min(count + quantity, capacity);
            try (Transaction action = Transaction.create()) {
                ItemKey newKey = current.withTag(current.getTag().toBuilder().putInt("amount", count).putString("fluid", Registry.FLUID.getId(type).toString()));
                if (!this.context.replace(action, current, 1, newKey, 1)) {
                    action.abort();
                    return 0;
                } else {
                    return quantity;
                }
            }
        }
        return 0;
    }
}
