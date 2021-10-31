package brachy84.brachydium.api.blockEntity.trait;

import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.handlers.ApiHolder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

public abstract class TileTrait extends ApiHolder {

    protected final TileEntity tile;
    private final String id;

    public TileTrait(TileEntity tile) {
        this.tile = Objects.requireNonNull(tile);
        this.id = this.getClass().getSimpleName();
        this.tile.addTrait(this);
    }

    /**
     * @return a unique name (for nbt)
     */
    public final String getName() {
        return id;
    }

    /*
     * @return the block apis to register
     */
    //@Deprecated
    //public BlockApiHolder<?, ?>[] getApis() { return null; }

    @ApiStatus.OverrideOnly
    public void init() {
    }

    @Override
    public void registerApis() {
    }

    public abstract void addApis(BlockEntityType<BlockEntityHolder> type);

    /**
     * Gets called every tick
     */
    @ApiStatus.OverrideOnly
    public void tick() {
    }

    /**
     * Gets called on MetaBlockEntity nbt serialization
     *
     * @return a serialized Tag
     */
    abstract public NbtCompound serializeTag();

    /**
     * Gets called on MetaBlockEntity nbt deserialization
     *
     * @param tag to deserialize
     */
    abstract public void deserializeTag(NbtCompound tag);

    public TileEntity getTile() {
        return tile;
    }
}
