package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.cover.Cover;
import brachy84.brachydium.api.cover.ICoverable;
import brachy84.brachydium.api.gui_v1.BrachydiumGui;
import brachy84.brachydium.api.gui_v1.IUiHolder;
import brachy84.brachydium.api.handlers.*;
import brachy84.brachydium.api.handlers.astrarre.ItemHandler;
import brachy84.brachydium.api.render.OverlayRenderer;
import brachy84.brachydium.api.render.Renderer;
import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.recipe.RecipeTable;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class MetaBlockEntity implements ICoverable, Tickable, IUiHolder, IInventoryHolder {

    private final Identifier id;
    private Block block;
    private BlockItem item;
    private BlockEntityType<MetaBlockEntityHolder> entityType;
    //private ScreenHandlerType<ModularScreenHandler> screenHandlerType;

    private List<MBETrait> traits = new ArrayList<>();
    private Map<Class<?>, MBETrait> traitMap = new HashMap<>();
    private List<OverlayRenderer> overlays = new ArrayList<>();
    //private ModularGui modularGui;
    private Direction frontFacing;

    protected MetaBlockEntityHolder holder;

    protected MetaBlockEntity metaTileEntity;

    protected Inventory importItems;
    protected Inventory exportItems;
    protected IFluidInventory importFluids;
    protected IFluidInventory exportFluids;

    public MetaBlockEntity(Identifier id) {
        this.id = id;
    }

    public static MetaBlockEntity getFromId(Identifier id) {
        return BrachydiumApi.META_BLOCK_ENTITY_REGISTRY.tryGetEntry(id);
    }

    /**
     * Gets called during Registry
     */
    /*public void init() {
        // register block apis
        for(MBETrait trait : traits) {
            for(BlockApiHolder<?, ?> apiHolder : trait.getApis()) {
                apiHolder.register(entityType);
            }
        }
        for(BlockApiHolder<?, ?> apiHolder : getApis()) {
            apiHolder.register(entityType);
        }
    }*/

    public void reinitializeInventories() {
        Brachydium.LOGGER.info("Reinitializing Inventories");
        importItems = createImportItemHandler();
        exportItems = createExportItemHandler();
        importFluids = createImportFluidHandler();
        exportFluids = createExportFluidHandler();
    }

    //public BlockApiHolder<?, ?>[] getApis() {
    //    return new BlockApiHolder[] {};
    //}

    public void addApis() {
        for(MBETrait trait : traits) {
            trait.addApis(getEntityType());
        }
        //FabricParticipants.ITEM_WORLD.forBlockEntity(getEntityType(), ((direction, state, world, pos, entity) -> new ItemHandler(importItems)));
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
        traits.add(trait);
        //traitMap.put(trait.getClass(), trait);
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

    @Override
    public boolean hasUi() {
        return false;
    }

    //@Nullable
    //public ModularGui createUi(PlayerInventory inventory) {
    //    return null;
    //};

    @Override
    public BrachydiumGui.Builder buildUi(BrachydiumGui.Builder builder) {
        return builder;
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

    @Override
    public Inventory createImportItemHandler() {
        return new ItemInventory(0);
    }

    @Override
    public Inventory createExportItemHandler() {
        return new ItemInventory(0);
    }

    @Override
    public IFluidInventory createImportFluidHandler() {
        return new FluidInventory(0);
    }

    @Override
    public IFluidInventory createExportFluidHandler() {
        return new FluidInventory(0);
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

    /*public ScreenHandlerType<ModularScreenHandler> getScreenHandlerType() {
        return screenHandlerType;
    }*/

    public MetaBlockEntityHolder getHolder() {
        return holder;
    }

    abstract public Renderer getRenderer();

    @Override
    public Inventory getImportItems() {
        return importItems;
    }

    @Override
    public Inventory getExportItems() {
        return exportItems;
    }

    @Override
    public IFluidInventory getImportFluids() {
        return importFluids;
    }

    @Override
    public IFluidInventory getExportFluids() {
        return exportFluids;
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

    /*public void setScreenHandlerType(ScreenHandlerType<ModularScreenHandler> screenHandlerType) {
        if(this.screenHandlerType != null) return;
        this.screenHandlerType = screenHandlerType;
    }*/

    public Supplier<BlockEntityType<MetaBlockEntityHolder>> getEntityTypeSupplier() {
        return this::getEntityType;
    }

    public Identifier getId() {
        return id;
    }
}
