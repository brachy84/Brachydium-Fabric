package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.block.BlockMachineItem;
import brachy84.brachydium.api.blockEntity.trait.TileTrait;
import brachy84.brachydium.api.cover.Cover;
import brachy84.brachydium.api.cover.CoverableApi;
import brachy84.brachydium.api.cover.ICoverable;
import brachy84.brachydium.api.gui.TileEntityUiFactory;
import brachy84.brachydium.api.handlers.ApiHolder;
import brachy84.brachydium.api.handlers.storage.*;
import brachy84.brachydium.api.render.SpriteMap;
import brachy84.brachydium.api.render.TileRenderUtil;
import brachy84.brachydium.api.util.TransferUtil;
import brachy84.brachydium.api.util.XSTR;
import brachy84.brachydium.gui.api.Gui;
import brachy84.brachydium.gui.api.UIHolder;
import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public abstract class TileEntity extends ApiHolder implements UIHolder, IOrientable, ICoverable {

    public static final XSTR random = new XSTR();
    private final Map<String, TileTrait> traits = new HashMap<>();
    private final List<Runnable> onAttachListener = new ArrayList<>();
    private final EnumMap<Direction, Cover> coverMap = new EnumMap<>(Direction.class);
    // Create an offset [0,20) to distribute ticks more evenly
    private final int offset = random.nextInt(20);
    protected List<IItemHandler> notifiedItemOutputList = new ArrayList<>();
    protected List<IItemHandler> notifiedItemInputList = new ArrayList<>();
    protected List<IFluidHandler> notifiedFluidInputList = new ArrayList<>();
    protected List<IFluidHandler> notifiedFluidOutputList = new ArrayList<>();
    private Identifier tileId = Brachydium.id("not_yet_initialised");
    private TileEntityGroup group;
    private int key;
    private BlockEntityHolder holder;
    private Direction frontFacing;
    private IItemHandler importItems;
    private IItemHandler exportItems;
    private IFluidHandler importFluids;
    private IFluidHandler exportFluids;
    private Storage<ItemVariant> importItemStorage;
    private Storage<ItemVariant> exportItemStorage;
    private Storage<FluidVariant> importFluidStorage;
    private Storage<FluidVariant> exportFluidStorage;
    private Storage<ItemVariant> itemStorage;
    private Storage<FluidVariant> fluidStorage;
    private long timer = 0L;
    protected TileEntity() {
        this.frontFacing = Direction.NORTH;
        for (Direction direction : Direction.values()) {
            coverMap.put(direction, null);
        }
    }

    @Nullable
    public static TileEntity getOf(BlockEntity blockEntity) {
        if (blockEntity instanceof BlockEntityHolder holder)
            return holder.getActiveTileEntity();
        return null;
    }

    @Nullable
    public static TileEntity getOf(BlockView world, BlockPos pos) {
        BlockEntityHolder holder = BlockEntityHolder.getOf(world, pos);
        if (holder == null) return null;
        return holder.getActiveTileEntity();
    }

    public static TileEntity ofStack(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockMachineItem))
            throw new IllegalArgumentException("Item is not BlockMachineItem");
        if (!stack.hasNbt())
            throw new IllegalArgumentException("Can't get TileEntity with a null tag");
        return ((BlockMachineItem) stack.getItem()).getTileGroup().getBlockEntity(stack.getNbt());
    }

    public static Optional<TileEntity> tryOfStack(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockMachineItem) || !stack.hasNbt())
            return Optional.empty();
        return ((BlockMachineItem) stack.getItem()).getTileGroup().tryGetBlockEntity(stack.getNbt());
    }

    @ApiStatus.Internal
    protected final TileEntity createCopyInternal() {
        TileEntity tile = createNewTileEntity();
        tile.setGroup(group, key);
        tile.setUp();
        return tile;
    }

    @ApiStatus.OverrideOnly
    public abstract @NotNull TileEntity createNewTileEntity();

    /**
     * Gets called when a new TileEntity is created
     * It's basically a late constructor
     */
    @ApiStatus.OverrideOnly
    public void setUp() {
    }

    public void initializeInventories() {
        this.importItems = createInputItemHandler();
        this.exportItems = createOutputItemHandler();
        this.importFluids = createInputFluidHandler();
        this.exportFluids = createOutputFluidHandler();

        importItems.setNotifiableMetaTileEntity(this);
        exportItems.setNotifiableMetaTileEntity(this);
        importFluids.setNotifiableMetaTileEntity(this);
        exportFluids.setNotifiableMetaTileEntity(this);

        importItems.addListener(() -> {
            if (getWorld() != null && !getWorld().isClient()) {
                syncCustomData(Const.SYNC_INPUT_ITEMS, buf -> TransferUtil.pack(importItems, buf));
            }
        });
        exportItems.addListener(() -> {
            if (getWorld() != null && !getWorld().isClient()) {
                syncCustomData(Const.SYNC_OUTPUT_ITEMS, buf -> TransferUtil.pack(exportItems, buf));
            }
        });

        importFluids.addListener(() -> {
            if (getWorld() != null && !getWorld().isClient()) {
                syncCustomData(Const.SYNC_INPUT_FLUIDS, buf -> TransferUtil.pack(importFluids, buf));
            }
        });
        exportFluids.addListener(() -> {
            if (getWorld() != null && !getWorld().isClient()) {
                syncCustomData(Const.SYNC_OUTPUT_FLUIDS, buf -> TransferUtil.pack(exportFluids, buf));
            }
        });

        this.importItemStorage = new InventoryStorage(importItems);
        this.exportItemStorage = new InventoryStorage(exportItems);
        this.importFluidStorage = new FluidInventoryStorage(importFluids);
        this.exportFluidStorage = new FluidInventoryStorage(exportFluids);

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
    }

    /**
     * @param name of the trait
     * @return the trait associated with the name
     * see also: {@link TileTrait#getName()}
     */
    @Nullable
    public TileTrait getTrait(String name) {
        return traits.get(name);
    }

    /**
     * @param clazz of the trait
     * @param <T>   type of the trait
     * @return the first trait that matches the class
     */
    @Nullable
    public <T extends TileTrait> T getTrait(Class<T> clazz) {
        return (T) traits.get(TileTrait.getTraitName(clazz));
    }

    /**
     * @param name of the trait
     * @return if the tile has a trait with name
     */
    public boolean hasTrait(String name) {
        return getTrait(name) != null;
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
        for (TileTrait trait : traits.values()) {
            trait.registerApis();
        }
    }

    public <T> void addNotifiedInput(T input) {
        if (input instanceof IItemHandler) {
            if (!notifiedItemInputList.contains(input)) {
                this.notifiedItemInputList.add((IItemHandler) input);
            }
        } else if (input instanceof IFluidHandler) {
            if (!notifiedFluidInputList.contains(input)) {
                this.notifiedFluidInputList.add((IFluidHandler) input);
            }
        }
    }

    public <T> void addNotifiedOutput(T output) {
        if (output instanceof IItemHandler) {
            if (!notifiedItemOutputList.contains(output)) {
                this.notifiedItemOutputList.add((IItemHandler) output);
            }
        } else if (output instanceof IFluidHandler) {
            if (!notifiedFluidOutputList.contains(output)) {
                this.notifiedFluidOutputList.add((IFluidHandler) output);
            }
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
        traits.values().forEach(TileTrait::init);
    }

    public void onDetach() {
    }

    @Environment(EnvType.CLIENT)
    public void render(QuadEmitter emitter) {
        TileRenderUtil.renderCube(emitter, SpriteMap.getMissingSprite());
    }

    public boolean isTicking() {
        return true;
    }

    public void tick() {
        if (timer++ == 0) {
            onFirstTick();
        }
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

    public boolean isFirstTick() {
        return timer == 0;
    }

    /**
     * Replacement for former getTimer().
     *
     * @return Timer value with a random offset of [0,20].
     */
    public long getOffsetTimer() {
        return timer + offset;
    }

    protected void onFirstTick() {
    }

    public boolean shouldTick(TileTrait trait) {
        return true;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (TileEntityUiFactory.INSTANCE.openUI(this, player)) {
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public boolean hasUI() {
        return false;
    }

    @NotNull
    public Identifier getUiId() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @NotNull
    public Gui createUi(PlayerEntity player) {
        return createUiTemplate(player).build();
    }

    public Gui.Builder createUiTemplate(PlayerEntity player) {
        return Gui.defaultBuilder(player);
    }

    @NotNull
    public IItemHandler createInputItemHandler() {
        return new ItemInventory(0);
    }

    @NotNull
    public IItemHandler createOutputItemHandler() {
        return new ItemInventory(0);
    }

    @NotNull
    public IFluidHandler createInputFluidHandler() {
        return IFluidHandler.EMPTY;
    }

    @NotNull
    public IFluidHandler createOutputFluidHandler() {
        return IFluidHandler.EMPTY;
    }

    public IFluidHandler getExportFluidHandler() {
        return exportFluids;
    }

    public IFluidHandler getImportFluidHandler() {
        return importFluids;
    }

    public IItemHandler getExportInventory() {
        return exportItems;
    }

    public IItemHandler getImportInventory() {
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

    public List<IFluidHandler> getNotifiedFluidInputList() {
        return notifiedFluidInputList;
    }

    public List<IFluidHandler> getNotifiedFluidOutputList() {
        return notifiedFluidOutputList;
    }

    public List<IItemHandler> getNotifiedItemInputList() {
        return notifiedItemInputList;
    }

    public List<IItemHandler> getNotifiedItemOutputList() {
        return notifiedItemOutputList;
    }

    @Environment(EnvType.CLIENT)
    public void scheduleRenderUpdate() {
        holder.scheduleRenderUpdate();
    }

    public void markDirty() {
        holder.markDirty();
    }

    public void syncTraitData(TileTrait trait, int internalId, Consumer<PacketByteBuf> dataWriter) {
        syncCustomData(Const.SYNC_TRAIT_DATA, buffer -> {
            buffer.writeString(trait.getName());
            buffer.writeVarInt(internalId);
            dataWriter.accept(buffer);
        });
    }

    public void syncCoverData(Cover cover, int internalId, Consumer<PacketByteBuf> dataWriter) {
        syncCustomData(Const.SYNC_COVER_DATA, buffer -> {
            buffer.writeByte(cover.getAttachedSide().getId());
            buffer.writeVarInt(internalId);
            dataWriter.accept(buffer);
        });
    }

    public void syncCustomData(int id, Consumer<PacketByteBuf> consumer) {
        holder.syncCustomData(id, consumer);
    }

    @Environment(EnvType.CLIENT)
    public void readCustomData(int id, PacketByteBuf buf) {
        switch (id) {
            case Const.SYNC_TRAIT_DATA -> {
                TileTrait trait = getTrait(buf.readString());
                trait.readCustomData(buf.readVarInt(), buf);
            }
            case Const.SYNC_COVER_DATA -> {
                Cover cover = getCover(Direction.byId(buf.readByte()));
                cover.readCustomData(buf.readVarInt(), buf);
            }
            case Const.SYNC_INPUT_ITEMS -> TransferUtil.unpack(importItems, buf);
            case Const.SYNC_OUTPUT_ITEMS -> TransferUtil.unpack(exportItems, buf);
            case Const.SYNC_INPUT_FLUIDS -> TransferUtil.unpack(importFluids, buf);
            case Const.SYNC_OUTPUT_FLUIDS -> TransferUtil.unpack(exportFluids, buf);
        }
    }

    public NbtCompound serializeNbt() {
        NbtCompound tag = new NbtCompound();
        NbtCompound traitTag = new NbtCompound();
        for (TileTrait trait : traits.values()) {
            NbtCompound tag1 = trait.serializeNbt();
            if (tag1 != null)
                traitTag.put(trait.getName(), tag1);
        }
        tag.put("MBETraits", traitTag);
        tag.put("Covers", serializeCovers(false));

        tag.put("InputItems", TransferUtil.inventoryToNbt(getImportInventory()));
        tag.put("OutputItems", TransferUtil.inventoryToNbt(getExportInventory()));
        tag.put("InputFluids", TransferUtil.fluidHandlerToNbt(getImportFluidHandler()));
        tag.put("OutputFluids", TransferUtil.fluidHandlerToNbt(getExportFluidHandler()));

        return tag;
    }

    public void deserializeNbt(NbtCompound tag) {
        NbtCompound traitTag = tag.getCompound("MBETraits");
        for (String key : traitTag.getKeys()) {
            TileTrait trait = getTrait(key);
            if (trait != null) {
                trait.deserializeNbt(traitTag.getCompound(key));
            }
        }
        deserializeCovers(tag.getCompound("Covers"), false);

        TransferUtil.inventoryFromNbt(tag.getList("InputItems", NbtElement.COMPOUND_TYPE), getImportInventory()::setStackSilently);
        TransferUtil.inventoryFromNbt(tag.getList("OutputItems", NbtElement.COMPOUND_TYPE), getExportInventory()::setStackSilently);
        TransferUtil.fluidHandlerFromNbt(tag.getList("InputFluids", NbtElement.COMPOUND_TYPE), getImportFluidHandler()::setFluidSilently);
        TransferUtil.fluidHandlerFromNbt(tag.getList("OutputFluids", NbtElement.COMPOUND_TYPE), getExportFluidHandler()::setFluidSilently);
    }

    public NbtCompound serializeClientNbt() {
        NbtCompound tag = new NbtCompound();
        NbtCompound traitTag = new NbtCompound();
        for (TileTrait trait : traits.values()) {
            NbtCompound tag1 = trait.serializeClientNbt();
            if (tag1 != null)
                traitTag.put(trait.getName(), tag1);
        }
        tag.put("MBETraits", traitTag);
        tag.put("Covers", serializeCovers(true));

        tag.put("InputItems", TransferUtil.inventoryToNbt(getImportInventory()));
        tag.put("OutputItems", TransferUtil.inventoryToNbt(getExportInventory()));
        tag.put("InputFluids", TransferUtil.fluidHandlerToNbt(getImportFluidHandler()));
        tag.put("OutputFluids", TransferUtil.fluidHandlerToNbt(getExportFluidHandler()));

        return tag;
    }

    public void deserializeClientNbt(NbtCompound tag) {
        NbtCompound traitTag = tag.getCompound("MBETraits");
        for (String key : traitTag.getKeys()) {
            TileTrait trait = getTrait(key);
            if (trait != null) {
                trait.deserializeClientNbt(traitTag.getCompound(key));
            }
        }
        deserializeCovers(tag.getCompound("Covers"), true);

        TransferUtil.inventoryFromNbt(tag.getList("InputItems", NbtElement.COMPOUND_TYPE), getImportInventory()::setStackSilently);
        TransferUtil.inventoryFromNbt(tag.getList("OutputItems", NbtElement.COMPOUND_TYPE), getExportInventory()::setStackSilently);
        TransferUtil.fluidHandlerFromNbt(tag.getList("InputFluids", NbtElement.COMPOUND_TYPE), getImportFluidHandler()::setFluidSilently);
        TransferUtil.fluidHandlerFromNbt(tag.getList("OutputFluids", NbtElement.COMPOUND_TYPE), getExportFluidHandler()::setFluidSilently);
    }

    public boolean isValid() {
        return getHolder() != null;
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
        if (!isValidFrontFacing(frontFacing))
            Brachydium.LOGGER.fatal("Tried to set {} as front of {}, but is invalid", frontFacing, tileId);
        this.frontFacing = frontFacing;
    }

    public boolean isValidFrontFacing(Direction direction) {
        return direction.getAxis() != Direction.Axis.Y;
    }

    public void addTooltip(ItemStack stack, @Nullable World player, List<Text> tooltip, boolean advanced) {
    }

    public String getTranslationKeyBase() {
        return String.format("tile.%s.%s", tileId.getNamespace(), tileId.getPath());
    }

    public String getNameKey() {
        return getTranslationKeyBase() + ".name";
    }

    public BlockEntityType<BlockEntityHolder> getEntityType() {
        return holder.getGroup().getType();
    }

    public TileEntityGroup getGroup() {
        return group;
    }

    @ApiStatus.Internal
    protected final void setGroup(TileEntityGroup group, int key) {
        if (this.group != null) {
            throw new ConcurrentModificationException("The group of TileEntity can only be set once!");
        }
        this.group = group;
        this.key = key;
        this.tileId = new Identifier(group.id.getNamespace(), group.id.getPath() + "." + getTileIdPostFix());
    }

    public int getGroupKey() {
        return key;
    }

    public String getTileIdPostFix() {
        return String.valueOf(key);
    }

    public Identifier getTileId() {
        return tileId;
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
        if (group == null) return item;
        getGroup().writeNbt(item.getOrCreateNbt(), key);
        return item;
    }

    public static class Const {
        public static final int SYNC_INPUT_ITEMS = -100;
        public static final int SYNC_OUTPUT_ITEMS = -101;
        public static final int SYNC_INPUT_FLUIDS = -102;
        public static final int SYNC_OUTPUT_FLUIDS = -103;
        public static final int SYNC_TRAIT_DATA = -104;
        public static final int SYNC_COVER_DATA = -105;
    }
}
