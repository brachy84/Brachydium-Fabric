package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.handlers.CombinedFluidTankList;
import brachy84.brachydium.api.handlers.CombinedItemInventory;
import brachy84.brachydium.api.handlers.FluidTankList;
import brachy84.brachydium.api.handlers.ItemInventory;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;

public class InventoryHolder extends TileTrait {

    private ArrayParticipant<ItemKey> importItems;
    private ArrayParticipant<ItemKey> exportItems;
    private ArrayParticipant<Fluid> importFluids;
    private ArrayParticipant<Fluid> exportFluids;

    public InventoryHolder(TileEntity tile, ArrayParticipant<ItemKey> importItems, ArrayParticipant<ItemKey> exportItems, ArrayParticipant<Fluid> importFluids, ArrayParticipant<Fluid> exportFluids) {
        super(tile);
        this.importItems = importItems;
        this.exportItems = exportItems;
        this.importFluids = importFluids;
        this.exportFluids = exportFluids;
    }

    public InventoryHolder(TileEntity tile, int importItems, int exportItems, int importFluids, int exportFluids) {
        this(tile, ItemInventory.importInventory(importItems), ItemInventory.exportInventory(exportItems), FluidTankList.importTanks(importFluids), FluidTankList.exportTanks(exportFluids));
    }

    public InventoryHolder(TileEntity tile) {
        this(tile, 0, 0, 0, 0);
    }

    @Override
    public String getName() {
        return "inventory_holder";
    }

    @Override
    public void addApis(BlockEntityType<BlockEntityHolder> type) {
        FabricParticipants.ITEM_WORLD.forBlockEntity(type, (direction, state, world, pos, entity) -> {
            if(entity instanceof BlockEntityHolder) {
                TileEntity tile = ((BlockEntityHolder) entity).getActiveTileEntity();
                if(tile != null) {
                    return tile.getInventories().getItemInventory();
                }
            }
            return null;
        });
        FabricParticipants.FLUID_WORLD.forBlockEntity(type, (direction, state, world, pos, entity) -> {
            if(entity instanceof BlockEntityHolder) {
                TileEntity tile = ((BlockEntityHolder) entity).getActiveTileEntity();
                if(tile != null) {
                    return tile.getInventories().getFluidInventory();
                }
            }
            return null;
        });
    }

    public ArrayParticipant<ItemKey> getImportItems() {
        return importItems;
    }

    public ArrayParticipant<ItemKey> getExportItems() {
        return exportItems;
    }

    public ArrayParticipant<Fluid> getImportFluids() {
        return importFluids;
    }

    public ArrayParticipant<Fluid> getExportFluids() {
        return exportFluids;
    }

    public Participant<ItemKey> getItemInventory() {
        return new CombinedItemInventory(importItems, exportItems);
    }

    public Participant<Fluid> getFluidInventory() {
        return new CombinedFluidTankList(importFluids, exportFluids);
    }

    @Override
    public NbtCompound serializeTag() {
        NbtCompound tag = new NbtCompound();
        if(importItems instanceof ItemInventory) {
            tag.put("importItems", ((ItemInventory) importItems).toTag());
        }
        if(exportItems instanceof ItemInventory) {
            tag.put("exportItems", ((ItemInventory) exportItems).toTag());
        }
        if(importFluids instanceof FluidTankList) {
            tag.put("importFluids", ((FluidTankList) importFluids).toTag());
        }
        if(exportFluids instanceof FluidTankList) {
            tag.put("exportFluids", ((FluidTankList) exportFluids).toTag());
        }
        return tag;
    }

    @Override
    public void deserializeTag(NbtCompound tag) {
        importItems = ItemInventory.fromTag(tag.getCompound("importItems"));
        exportItems = ItemInventory.fromTag(tag.getCompound("exportItems"));
        importFluids = FluidTankList.fromTag(tag.getCompound("importFluids"));
        exportFluids = FluidTankList.fromTag(tag.getCompound("exportFluids"));
    }
}
