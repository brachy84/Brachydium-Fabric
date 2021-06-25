package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.block.BlockMachineItem;
import brachy84.brachydium.gui.api.IUIHolder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class TileEntity implements IUIHolder {

    private BlockEntityGroup<?> group;
    private BlockEntityHolder holder;
    private final Map<String, TileTrait> traits = new HashMap<>();
    private Direction frontFacing;
    private InventoryHolder inventories;
    private final List<Runnable> onAttachListener = new ArrayList<>();
    private final List<RenderTrait> renderTraits = new ArrayList<>();

    protected TileEntity() {
        createBaseRenderer();
    }

    public static TileEntity ofStack(ItemStack stack) {
        if(!(stack.getItem() instanceof BlockMachineItem))
            throw new IllegalArgumentException("Item is not BlockMachineItem");
        if(!stack.hasTag())
            throw new IllegalArgumentException("Can't get TileEntity with a null tag");
        return ((BlockMachineItem) stack.getItem()).getTileGroup().getBlockEntity(stack.getTag());
    }

    public InventoryHolder createInventories() {
        return new InventoryHolder(this);
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
        if (trait instanceof RenderTrait) {
            renderTraits.add((RenderTrait) trait);
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

    public boolean hasTrait(String name) {
        return findTrait(name) != null;
    }

    public <T extends TileTrait> boolean hasTrait(Class<T> clazz) {
        return getTrait(clazz) != null;
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
        inventories = createInventories();
        for (Runnable runnable : onAttachListener) {
            runnable.run();
        }
    }

    public void onDetach() {
    }

    public abstract RenderTrait createBaseRenderer();

    public void render(QuadEmitter emitter) {
        for (RenderTrait renderTrait : renderTraits) {
            renderTrait.onRender(emitter);
        }
        /*
        for(Renderer renderer : renderers) {
            renderer.render(emitter, frontFacing);
        }*/
    }

    public void tick() {
        traits.forEach((key, trait) -> {
            if (shouldTick(trait))
                trait.tick();
        });
    }

    public boolean shouldTick(TileTrait trait) {
        return !(trait instanceof RenderTrait);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    public NbtCompound serializeTag() {
        NbtCompound tag = new NbtCompound();
        Brachydium.LOGGER.info("Saving facing: " + getFrontFacing().getId());
        tag.putInt("front", getFrontFacing().getId());

        NbtCompound traitTag = new NbtCompound();
        for (TileTrait trait : traits.values()) {
            NbtCompound tag1 = trait.serializeTag();
            if (tag1 != null)
                traitTag.put(trait.getName(), tag1);
        }
        tag.put("MBETraits", traitTag);

        return tag;
    }

    public void deserializeTag(NbtCompound tag) {
        Direction facing = Direction.byId(tag.getInt("front"));
        setFrontFacing(facing);
        NbtCompound traitTag = tag.getCompound("MBETraits");
        for (String key : traitTag.getKeys()) {
            TileTrait trait = findTrait(key);
            if (trait != null) {
                trait.deserializeTag(traitTag.getCompound(key));
            }
        }
    }

    public InventoryHolder getInventories() {
        return inventories;
    }

    @NotNull
    public BlockEntityHolder getHolder() {
        return holder;
    }

    @ApiStatus.Internal
    public void setHolder(BlockEntityHolder holder) {
        this.holder = holder;
    }

    public boolean isActive() {
        return false;
    }

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

    public Direction getFrontFacing() {
        if (frontFacing == null) setFrontFacing(Direction.NORTH);
        return frontFacing;
    }

    public void setFrontFacing(Direction frontFacing) {
        this.frontFacing = frontFacing;
    }

    public BlockEntityType<BlockEntityHolder> getEntityType() {
        return holder.getGroup().getType();
    }

    public BlockEntityGroup<?> getGroup() {
        return group;
    }

    @ApiStatus.Internal
    public final void setGroup(BlockEntityGroup<?> group) {
        if (this.group != null) {
            throw new ConcurrentModificationException("The group of TileEntity can only be set once!");
        }
        this.group = group;
    }

    public Item asItem() {
        return group.getItem();
    }

    public ItemStack asStack() {
        return asStack(1);
    }

    public ItemStack asStack(int amount) {
        ItemStack item = new ItemStack(asItem(), amount);
        getGroup().writeTileNbt(item.getOrCreateTag(), this);
        return item;
    }
}
