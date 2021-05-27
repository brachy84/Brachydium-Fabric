package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.cover.Cover;
import brachy84.brachydium.api.cover.ICoverable;
import brachy84.brachydium.api.handlers.*;
import brachy84.brachydium.api.render.OverlayRenderer;
import brachy84.brachydium.api.render.Renderer;
import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.gui.widgets.RootWidget;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class MetaBlockEntity implements ICoverable, Tickable {

    protected final Identifier id;
    private Block block;
    private BlockItem item;
    private BlockEntityType<MetaBlockEntityHolder> entityType;

    private final List<MBETrait> traits = new ArrayList<>();
    private final List<MBETrait> nullTraits = new ArrayList<>();
    private final List<OverlayRenderer> overlays = new ArrayList<>();
    private Direction frontFacing;

    protected MetaBlockEntityHolder holder;

    protected ArrayParticipant<ItemKey> importItems;
    protected ArrayParticipant<ItemKey> exportItems;
    protected ArrayParticipant<Fluid> importFluids;
    protected ArrayParticipant<Fluid> exportFluids;

    private final List<Runnable> initialiseListeners = new ArrayList<>();

    public MetaBlockEntity(Identifier id) {
        this.id = id;
        reinitializeInventories();
    }

    public static MetaBlockEntity getFromId(Identifier id) {
        return BrachydiumApi.META_BLOCK_ENTITY_REGISTRY.tryGetEntry(id);
    }

    public abstract MetaBlockEntity recreate();

    public void reinitializeInventories() {
        Brachydium.LOGGER.info("Reinitializing Inventories");
        importItems = createImportItemHandler();
        exportItems = createExportItemHandler();
        importFluids = createImportFluidHandler();
        exportFluids = createExportFluidHandler();
        initialiseListeners.forEach(Runnable::run);
    }

    public final void appendInitialiseListener(Runnable runnable) {
        initialiseListeners.add(runnable);
    }


    public void addApis() {
        for(MBETrait trait : traits) {
            trait.addApis(getEntityType());
        }
        FabricParticipants.ITEM_WORLD.forBlockEntity(getEntityType(), (direction, state, world, pos, entity) -> getItemInventory());
        FabricParticipants.FLUID_WORLD.forBlockEntity(getEntityType(), (direction, state, world, pos, entity) -> getFluidInventory());
    }

    public void render(QuadEmitter emitter) {
        getRenderer().render(emitter, getFrontFacing());
        for(OverlayRenderer overlay : overlays) {
            overlay.render(emitter, getFrontFacing());
        }
        for(Map.Entry<Direction, Cover> cover : getCovers().entrySet()) {
            cover.getValue().render(emitter, cover.getKey());
        }
    }

    public void addTrait(MBETrait trait) {
        if(trait != null) {
            Brachydium.LOGGER.info("Adding trait " + trait.getName());
            traits.add(trait);
        } else {
            Brachydium.LOGGER.fatal("Can't add trait to MetaBlockEntity, because it's null");
        }
    }

    public MBETrait findTrait(String name) {
        for(MBETrait trait : traits) {
            if(trait.getName().equals(name)) {
                return trait;
            }
        }
        return null;
    }

    public String getRawLangKey() {
        return id.getNamespace() + ".metablockentity." + id.getPath().substring(4) + ".";
    }

    @Override
    public boolean canPutCover(Cover cover) {
        return true;
    }

    public boolean shouldUpdate(MBETrait trait) {
        return true;
    }

    @Override
    public void tick() {
        for(MBETrait trait : traits) {
            if(shouldUpdate(trait)) {
                trait.update();
            }
        }
    }

    public boolean hasUi() {
        return false;
    }

    @NotNull
    public RootWidget.Builder createUITemplate(PlayerEntity player, RootWidget.Builder builder) {
        return builder;
    }

    /*public ModularGuiOld createUiOld(PlayerEntity player) {
        return createUITemplate(player, ModularGuiOld.defaultBuilder()).build(getHolder(), player);
    };*/

    public RootWidget createUi(PlayerEntity player) {
        return createUITemplate(player, RootWidget.builder()).build();
    }

    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        CompoundTag traitTag = new CompoundTag();

        for(MBETrait trait : traits) {
            traitTag.put(trait.getName(), trait.serializeTag());
        }

        tag.put("MBETraits", traitTag);

        return tag;
    }

    public void deserializeTag(CompoundTag tag) {
        CompoundTag traitTag = tag.getCompound("MBETraits");
        for(String key : traitTag.getKeys()) {
            MBETrait trait = findTrait(key);
            if(trait != null) {
                trait.deserializeTag(traitTag.getCompound(key));
            }
        }
    }

    @Nullable
    public RecipeTable<?> getRecipeTable() {
        for(MBETrait trait : traits) {
            if(trait instanceof AbstractRecipeLogic) {
                return ((AbstractRecipeLogic) trait).recipeTable;
            }
        }
        return null;
    }

    public ArrayParticipant<ItemKey> createImportItemHandler() {
        return ItemInventory.importInventory(0);
    }

    public ArrayParticipant<ItemKey> createExportItemHandler() {
        return ItemInventory.exportInventory(0);
    }

    public ArrayParticipant<Fluid> createImportFluidHandler() {
        return FluidTankList.importTanks(0);
    }

    public ArrayParticipant<Fluid> createExportFluidHandler() {
        return FluidTankList.exportTanks(0);
    }

    @Nullable
    public World getWorld() {
        return holder == null ? null : holder.getWorld();
    }

    public boolean isClient() {
        if(getWorld() == null)
            return true;
        else
            return getWorld().isClient();
    }

    public Block getBlock() {
        return block;
    }

    public BlockItem getItem() {
        return item;
    }

    public ItemStack getStackForm() {
        return new ItemStack(item);
    }

    public ItemStack getStackForm(int amount) {
        return new ItemStack(item, amount);
    }

    public BlockEntityType<MetaBlockEntityHolder> getEntityType() {
        return entityType;
    }

    public MetaBlockEntityHolder getHolder() {
        return holder;
    }

    abstract public Renderer getRenderer();

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

    public Direction getFrontFacing() {
        return frontFacing == null ? Direction.NORTH : frontFacing;
    }

    public void setFrontFacing(Direction frontFacing) {
        this.frontFacing = frontFacing;
    }

    public void setBlock(Block block) {
        if(this.block != null) return;
        this.block = block;
    }

    public void setBlockItem(BlockItem blockItem) {
        if(this.item != null) return;
        this.item = blockItem;
    }

    public void setBlockEntityType(BlockEntityType<MetaBlockEntityHolder> entityType) {
        if(this.entityType != null) return;
        this.entityType = entityType;
    }

    public Supplier<BlockEntityType<MetaBlockEntityHolder>> getEntityTypeSupplier() {
        return this::getEntityType;
    }

    public Identifier getId() {
        return id;
    }
}
