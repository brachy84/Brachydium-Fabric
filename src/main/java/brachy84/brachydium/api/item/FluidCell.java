package brachy84.brachydium.api.item;

import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.api.blockEntity.InventoryListener;
import brachy84.brachydium.api.handlers.FluidCellHandler;
import brachy84.brachydium.api.handlers.oldAstrarre.SingleFluidTank;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.fabric.provider.ItemProvider;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.compat.PlayerInventoryParticipant;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FluidCell extends Item implements ItemProvider {

    private final int capacity;
    private final Slot<Fluid> container;

    public FluidCell(int capacity) {
        super(new FabricItemSettings().group(ItemGroups.GENERAL));
        this.capacity = capacity;
        this.container = new SingleFluidTank(capacity);
        ((InventoryListener) this.container).addListener(this::onChanged);
        //FabricParticipants.FLUID_ITEM.forItem(this, ((direction, key, count, itemKeyReplacingParticipant) -> container));
    }

    public void onChanged() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Object get(Access<?> access, Direction direction, ItemKey key, int count, Object container) {
        if (access == FabricParticipants.FLUID_ITEM && count == 1) {
            return new FluidCellHandler(this, (ReplacingParticipant<ItemKey>) container, key);
        }
        return null;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {
            ReplacingParticipant<ItemKey> participant = new PlayerInventoryParticipant(context.getPlayer().getInventory()).getCursorItemReplacingParticipant();
            Participant<Fluid> fluidCell = FabricParticipants.FLUID_ITEM.get().get(null, ItemKey.of(context.getStack()), context.getStack().getCount(), participant);
            Participant<Fluid> fluidParticipant = FabricParticipants.FLUID_WORLD.get().get(context.getSide(), context.getWorld(), context.getBlockPos());
            if(fluidParticipant != null) {
                fluidCell.extract(null, fluidParticipant);
            }
            //fluidCell.insert(Transaction.GLOBAL, Fluids.WATER, 100); // should make the fluid cell a 'water cell'
            //System.out.println(fluidCell.insert(Transaction.GLOBAL, Fluids.LAVA, 100)); // this should print zero, because u can't insert lava into a water cell
        }
        return ActionResult.CONSUME;
    }

    public int getCapacity() {
        return capacity;
    }
}
