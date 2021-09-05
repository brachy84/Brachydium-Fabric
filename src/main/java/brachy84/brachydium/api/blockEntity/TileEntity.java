package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.block.BlockMachineItem;
import brachy84.brachydium.api.blockEntity.trait.TileEntityRenderer;
import brachy84.brachydium.api.blockEntity.trait.TileTrait;
import brachy84.brachydium.api.cover.Cover;
import brachy84.brachydium.api.cover.CoverableApi;
import brachy84.brachydium.api.cover.ICoverable;
import brachy84.brachydium.api.gui.TileEntityUiFactory;
import brachy84.brachydium.api.handlers.ApiHolder;
import brachy84.brachydium.api.handlers.storage.*;
import brachy84.brachydium.gui.api.UIHolder;
import brachy84.brachydium.gui.internal.Gui;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class TileEntity extends ApiHolder implements UIHolder, IOrientable, ICoverable {

    @Nullable
    public static TileEntity getOf(BlockEntity blockEntity) {
        if (blockEntity instanceof BlockEntityHolder)
            return ((BlockEntityHolder) blockEntity).getActiveTileEntity();
        return null;
    }

    @Nullable
    public static TileEntity getOf(World world, BlockPos pos) {
        BlockEntityHolder holder = BlockEntityHolder.getOf(world, pos);
        if (holder == null) return null;
        return holder.getActiveTileEntity();
    }

    private TileEntityFactory<?> factory;
    private TileEntityGroup<?> group;
    private BlockEntityHolder holder;
    private final Map<String, TileTrait> traits = new HashMap<>();
    private Direction frontFacing;
    private final List<Runnable> onAttachListener = new ArrayList<>();
    private final List<TileEntityRenderer> tileEntityRenderers = new ArrayList<>();
    private final EnumMap<Direction, Cover> coverMap = new EnumMap<>(Direction.class);
    private Inventory importItems;
    private Inventory exportItems;
    private IFluidHandler importFluids;
    private IFluidHandler exportFluids;

    private Storage<ItemVariant> importItemStorage;
    private Storage<ItemVariant> exportItemStorage;
    private Storage<FluidVariant> importFluidStorage;
    private Storage<FluidVariant> exportFluidStorage;

    private Storage<ItemVariant> itemStorage;
    private Storage<FluidVariant> fluidStorage;

    protected TileEntity() {
        this.frontFacing = Direction.NORTH;
        for (Direction direction : Direction.values()) {
            coverMap.put(direction, null);
        }
    }

    @ApiStatus.Internal
    protected final TileEntityFactory<?> createAndSetFactory() {
        setFactory(createFactory());
        return this.factory;
    }

    @ApiStatus.Internal
    protected final void setFactory(TileEntityFactory<?> factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    /**
     * This creates a new instance of this tile entity when it's placed
     * <b>IMPORTANT:</b> override it in <b>EVERY</b> non abstract class to avoid issues
     * <p> example
     * {@code return new TileFactory<>(this, tile -> new TileEntity());}
     *
     * @return a tile factory
     */
    public abstract @NotNull TileEntityFactory<?> createFactory();

    public TileEntityFactory<?> getFactory() {
        return factory;
    }

    public static TileEntity ofStack(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockMachineItem))
            throw new IllegalArgumentException("Item is not BlockMachineItem");
        if (!stack.hasNbt())
            throw new IllegalArgumentException("Can't get TileEntity with a null tag");
        return ((BlockMachineItem) stack.getItem()).getTileGroup().getBlockEntity(stack.getNbt()).getOriginal();
    }

    /**
     * Gets called when a new TileEntity is created
     * It's basically a late constructor
     */
    @ApiStatus.Internal
    public void setUp() {
        Objects.requireNonNull(createBaseRenderer());
    }

    public void initializeInventories() {
        this.importItems = createInputItemHandler();
        this.exportItems = createOutputItemHandler();
        this.importFluids = createInputFluidHandler();
        this.exportFluids = createOutputFluidHandler();

        this.importItemStorage = InventoryStorage.of(importItems, null);
        this.exportItemStorage = InventoryStorage.of(exportItems, null);
        this.importFluidStorage = FluidInventoryStorage.of(importFluids);
        this.exportFluidStorage = FluidInventoryStorage.of(exportFluids);

        this.itemStorage = new CombinedStorage<>(Lists.newArrayList(importItemStorage, exportItemStorage));
        this.fluidStorage = new CombinedStorage<>(Lists.newArrayList(importFluidStorage, exportFluidStorage));
    }

    /**
     * This get's called in the TileTrait constructor
     * see also: {@link TileTrait#TileTrait(TileEntity)}
     *
     * @param trait to add
     */
    @ApiStatus.Internal
    public void addTrait(TileTrait trait) {
        Objects.requireNonNull(trait);
        traits.put(trait.getName(), trait);
        if (trait instanceof TileEntityRenderer) {
            tileEntityRenderers.add((TileEntityRenderer) trait);
        }
    }

    /**
     * @param name of the trait
     * @return the trait associated with the name
     * see also: {@link TileTrait#getName()}
     */
    @Nullable
    public TileTrait findTrait(String name) {
        return traits.get(name);
    }

    /**
     * @param clazz of the trait
     * @param <T>   type of the trait
     * @return the first trait that matches the class
     */
    @Nullable
    public <T extends TileTrait> T getTrait(Class<T> clazz) {
        for (TileTrait trait : traits.values()) {
            if (clazz.isAssignableFrom(trait.getClass())) {
                return (T) trait;
            }
        }
        return null;
    }

    /**
     * @param name of the trait
     * @return if the tile has a trait with name
     */
    public boolean hasTrait(String name) {
        return findTrait(name) != null;
    }

    /**
     * @param clazz class of the trait
     * @param <T>   type of the trait
     * @return if the tile has a trait that is an instance of clazz
     */
    public <T extends TileTrait> boolean hasTrait(Class<T> clazz) {
        return getTrait(clazz) != null;
    }

    @Override
    public Set<BlockApiLookup<Object, Object>> getLookups() {
        Set<BlockApiLookup<Object, Object>> lookups = new HashSet<>(super.getLookups());
        for (TileTrait trait : traits.values()) {
            lookups.addAll(trait.getLookups());
        }
        return lookups;
    }

    @Override
    public <A, C> A getApiProvider(BlockApiLookup<A, C> apiLookup, World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, C context) {
        A api = super.getApiProvider(apiLookup, world, pos, state, blockEntity, context);
        if (api != null) return api;
        for (TileTrait trait : traits.values()) {
            api = trait.getApiProvider(apiLookup, world, pos, state, blockEntity, context);
            if (api != null) return api;
        }
        return null;
    }

    @Override
    public void registerApis() {
        registerApi(CoverableApi.LOOKUP, this);
        registerApi(FluidStorage.SIDED, fluidStorage);
        registerApi(ItemStorage.SIDED, itemStorage);
    }

    public void addApis() {
        for (TileTrait trait : traits.values()) {
            trait.addApis(getEntityType());
        }
    }

    public void addOnAttachListener(Runnable runnable) {
        onAttachListener.add(runnable);
    }

    public void onAttach() {
        initializeInventories();
        for (Runnable runnable : onAttachListener) {
            runnable.run();
        }
    }

    public void onDetach() {
    }

    public abstract TileEntityRenderer createBaseRenderer();

    public void render(QuadEmitter emitter) {
        for (TileEntityRenderer tileEntityRenderer : tileEntityRenderers) {
            tileEntityRenderer.onRender(emitter);
        }
    }

    public boolean isTicking() {
        return true;
    }

    public void tick() {
        traits.forEach((key, trait) -> {
            if (shouldTick(trait))
                trait.tick();
        });
        for (Direction direction : Direction.values()) {
            Cover cover = getCover(direction);
            if (cover != null && cover.isTicking())
                cover.tick();
        }
    }

    public boolean shouldTick(TileTrait trait) {
        return !(trait instanceof TileEntityRenderer);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(TileEntityUiFactory.INSTANCE.openUI(this, player)) {
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public boolean hasUI() {
        return true;
    }

    @NotNull
    public Identifier getUiId() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @NotNull
    public Gui createUi(PlayerEntity player) {
        return Gui.defaultBuilder(player).build();
    }

    public Inventory createInputItemHandler() {
        return new ItemInventory(0);
    }

    public Inventory createOutputItemHandler() {
        return new ItemInventory(0);
    }

    public IFluidHandler createInputFluidHandler() {
        return IFluidHandler.EMPTY;
    }

    public IFluidHandler createOutputFluidHandler() {
        return IFluidHandler.EMPTY;
    }

    public IFluidHandler getExportFluidHandler() {
        return exportFluids;
    }

    public IFluidHandler getImportFluidHandler() {
        return importFluids;
    }

    public Inventory getExportInventory() {
        return exportItems;
    }

    public Inventory getImportInventory() {
        return importItems;
    }

    public Storage<FluidVariant> getExportFluidStorage() {
        return exportFluidStorage;
    }

    public Storage<FluidVariant> getImportFluidStorage() {
        return importFluidStorage;
    }

    public Storage<ItemVariant> getExportItemStorage() {
        return exportItemStorage;
    }

    public Storage<ItemVariant> getImportItemStorage() {
        return importItemStorage;
    }

    public Storage<ItemVariant> getItemStorage() {
        return itemStorage;
    }

    public Storage<FluidVariant> getFluidStorage() {
        return fluidStorage;
    }

    public void scheduleRenderUpdate() {
        holder.scheduleRenderUpdate();
    }

    public NbtCompound serializeTag() {
        NbtCompound tag = new NbtCompound();
        Brachydium.LOGGER.info("Saving facing: " + getFrontFace().getId());
        tag.putInt("front", getFrontFace().getId());

        NbtCompound traitTag = new NbtCompound();
        for (TileTrait trait : traits.values()) {
            NbtCompound tag1 = trait.serializeTag();
            if (tag1 != null)
                traitTag.put(trait.getName(), tag1);
        }
        tag.put("MBETraits", traitTag);
        tag.put("Covers", serializeCovers());

        return tag;
    }

    public void deserializeTag(NbtCompound tag) {
        Direction facing = Direction.byId(tag.getInt("front"));
        setFrontFace(facing);
        NbtCompound traitTag = tag.getCompound("MBETraits");
        for (String key : traitTag.getKeys()) {
            TileTrait trait = findTrait(key);
            if (trait != null) {
                trait.deserializeTag(traitTag.getCompound(key));
            }
        }
        deserializeCovers(tag.getCompound("Covers"));
    }

    @Override
    public @Nullable Cover getCover(Direction direction) {
        return coverMap.get(direction);
    }

    @Override
    public boolean canPlaceCover(Cover cover, Direction face) {
        return face != getFrontFace();
    }

    @Override
    public void placeCover(Cover cover, Direction side) {
        coverMap.put(side, Objects.requireNonNull(cover));
    }

    @Override
    public void removeCover(Direction side) {
        coverMap.remove(side);
    }

    @NotNull
    public BlockEntityHolder getHolder() {
        return holder;
    }

    @ApiStatus.Internal
    public void setHolder(BlockEntityHolder holder) {
        this.holder = holder;
    }

    public abstract boolean isActive();

    @Nullable
    public World getWorld() {
        return getHolder().getWorld();
    }

    public BlockPos getPos() {
        return getHolder().getPos();
    }

    public boolean isClient() {
        if (getWorld() == null)
            return true;
        else
            return getWorld().isClient();
    }

    public Direction getFrontFace() {
        if (frontFacing == null) setFrontFace(Direction.NORTH);
        return frontFacing;
    }

    public void setFrontFace(Direction frontFacing) {
        this.frontFacing = frontFacing;
    }

    public BlockEntityType<BlockEntityHolder> getEntityType() {
        return holder.getGroup().getType();
    }

    public TileEntityGroup<?> getGroup() {
        return group;
    }

    @ApiStatus.Internal
    protected final void setGroup(TileEntityGroup<?> group) {
        if (this.group != null) {
            throw new ConcurrentModificationException("The group of TileEntity can only be set once!");
        }
        this.group = group;
    }

    public BlockItem asItem() {
        return group.getItem();
    }

    public ItemStack asStack() {
        return asStack(1);
    }

    /**
     * @param amount of the stack
     * @return the Tile as item with identifying nbt data
     */
    public ItemStack asStack(int amount) {
        ItemStack item = new ItemStack(asItem(), amount);
        if (factory == null) return item;
        getGroup().writeTileNbt(item.getOrCreateNbt(), factory);
        return item;
    }
}
