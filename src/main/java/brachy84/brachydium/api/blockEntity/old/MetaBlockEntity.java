package brachy84.brachydium.api.blockEntity.old;

import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.blockEntity.TileTrait;
import brachy84.brachydium.api.cover.Cover;
import brachy84.brachydium.api.cover.ICoverable;
import brachy84.brachydium.api.handlers.*;
import brachy84.brachydium.api.render.OverlayRenderer;
import brachy84.brachydium.api.render.Renderer;
import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.render.Textures;
import brachy84.brachydium.api.util.Face;
import brachy84.brachydium.gui.widgets.RootWidget;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class MetaBlockEntity implements ICoverable, Tickable {

    protected final Identifier id;
    private Block block;
    private BlockItem item;
    private BlockEntityType<MetaBlockEntityHolder> entityType;

    private final List<TileTrait> traits = new ArrayList<>();
    private final List<TileTrait> nullTraits = new ArrayList<>();
    protected final List<OverlayRenderer> overlays = new ArrayList<>();
    private Direction frontFacing;

    protected MetaBlockEntityHolder holder;

    protected ArrayParticipant<ItemKey> importItems;
    protected ArrayParticipant<ItemKey> exportItems;
    protected ArrayParticipant<Fluid> importFluids;
    protected ArrayParticipant<Fluid> exportFluids;

    private final List<Runnable> initialiseListeners = new ArrayList<>();
    private final Map<Face, FaceType> faces = new HashMap<>();

    public enum FaceType {
        NONE, BLOCKED, INPUT, OUTPUT
    }

    private static final Direction[] DIRECTIONS = Direction.values();

    public MetaBlockEntity(Identifier id) {
        this.id = id;
        faces.put(Face.FRONT, FaceType.NONE);
        faces.put(Face.BACK, FaceType.OUTPUT);
        faces.put(Face.TOP, FaceType.NONE);
        faces.put(Face.BOTTOM, FaceType.NONE);
        faces.put(Face.LEFT, FaceType.NONE);
        faces.put(Face.RIGHT, FaceType.NONE);
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
        for(TileTrait trait : traits) {
            trait.addApis(getEntityType());
        }
        FabricParticipants.ITEM_WORLD.forBlockEntity(getEntityType(), (direction, state, world, pos, entity) -> getItemInventory());
        FabricParticipants.FLUID_WORLD.forBlockEntity(getEntityType(), (direction, state, world, pos, entity) -> getFluidInventory());
    }

    @Environment(EnvType.CLIENT)
    public void render(QuadEmitter emitter) {
        getRenderer().render(emitter, getFrontFacing());
        for(OverlayRenderer overlay : overlays) {
            overlay.render(emitter, getFrontFacing());
        }
        for(Map.Entry<Direction, Cover> cover : getCovers().entrySet()) {
            cover.getValue().render(emitter, cover.getKey());
        }
        for(Map.Entry<Face, FaceType> entry : faces.entrySet()) {
            if(entry.getValue() == FaceType.INPUT) {
                Renderer.renderSide(emitter, entry.getKey().getDirection(getFrontFacing()), Textures.INPUT_OVERLAY);
            } else if(entry.getValue() == FaceType.OUTPUT) {
                Renderer.renderSide(emitter, entry.getKey().getDirection(getFrontFacing()), Textures.OUTPUT_OVERLAY);
            }
        }
    }

    public void addTrait(TileTrait trait) {
        if(trait != null) {
            Brachydium.LOGGER.info("Adding trait " + trait.getName());
            traits.add(trait);
        } else {
            Brachydium.LOGGER.fatal("Can't add trait to MetaBlockEntity, because it's null");
        }
    }

    @Nullable
    public TileTrait findTrait(String name) {
        for(TileTrait trait : traits) {
            if(trait.getName().equals(name)) {
                return trait;
            }
        }
        return null;
    }

    @Nullable
    public TileTrait getTrait(Class<?> clazz) {
        for(TileTrait trait : traits) {
            if(clazz.isAssignableFrom(trait.getClass())) {
                return trait;
            }
        }
        return null;
    }

    public FaceType getFaceType(Face face) {
        return faces.get(face);
    }

    public String getRawLangKey() {
        return id.getNamespace() + ".metablockentity." + id.getPath().substring(4) + ".";
    }

    @Override
    public boolean canPutCover(Cover cover) {
        return true;
    }

    public void onAttach() {}

    public boolean shouldUpdate(TileTrait trait) {
        return true;
    }

    @Override
    public void tick() {
        for(TileTrait trait : traits) {
            if(shouldUpdate(trait)) {
                trait.tick();
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

    public RootWidget createUi(PlayerEntity player) {
        return createUITemplate(player, RootWidget.builder()).build();
    }

    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        Brachydium.LOGGER.info("Saving facing: " + getFrontFacing().getId());
        tag.putInt("front", getFrontFacing().getId());

        CompoundTag traitTag = new CompoundTag();
        for(TileTrait trait : traits) {
            traitTag.put(trait.getName(), trait.serializeTag());
        }
        tag.put("MBETraits", traitTag);

        serializeInventories(tag);
        return tag;
    }

    public void deserializeTag(CompoundTag tag) {
        Direction facing = DIRECTIONS[tag.getInt("front")];
        if(facing == null)
            Brachydium.LOGGER.info("Facing from NBT is null");
        else
            setFrontFacing(facing);
        CompoundTag traitTag = tag.getCompound("MBETraits");
        for(String key : traitTag.getKeys()) {
            TileTrait trait = findTrait(key);
            if(trait != null) {
                trait.deserializeTag(traitTag.getCompound(key));
            }
        }
        deserializeInventories(tag);
    }

    public void serializeInventories(CompoundTag tag) {
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
    }

    public void deserializeInventories(CompoundTag tag) {
        importItems = ItemInventory.fromTag(tag.getCompound("importItems"));
        exportItems = ItemInventory.fromTag(tag.getCompound("exportItems"));
        importFluids = FluidTankList.fromTag(tag.getCompound("importFluids"));
        exportFluids = FluidTankList.fromTag(tag.getCompound("exportFluids"));
    }

    @Nullable
    public RecipeTable<?> getRecipeTable() {
        for(TileTrait trait : traits) {
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

    public BlockPos getPos() {
        return holder.getPos();
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

    @Environment(EnvType.CLIENT)
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
        if(frontFacing == null) setFrontFacing(Direction.NORTH);//Brachydium.LOGGER.info("Front is null");
        return frontFacing;
    }

    public void setFrontFacing(Direction frontFacing) {
        if(frontFacing == Direction.DOWN || frontFacing == Direction.UP)
            throw new NullPointerException("Front can't be UP or DOWN");
        Brachydium.LOGGER.info("Set front to " + frontFacing);
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
