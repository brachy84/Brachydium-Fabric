package brachy84.brachydium.api.blockEntity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

public abstract class TileTrait {

    protected final TileEntity tile;

    public TileTrait(TileEntity tile) {
        this.tile = Objects.requireNonNull(tile);
        this.tile.addTrait(this);
    }

    /**
     * @return a unique name (for nbt)
     */
    public abstract String getName();

    /*
     * @return the block apis to register
     */
    //@Deprecated
    //public BlockApiHolder<?, ?>[] getApis() { return null; }

    public abstract void addApis(BlockEntityType<BlockEntityHolder> type);

    /**
     * Gets called every tick
     */
    public void tick() {}

    /**
     * Gets called on MetaBlockEntity nbt serialization
     * @return a serialized Tag
     */
    abstract public NbtCompound serializeTag();

    /**
     * Gets called on MetaBlockEntity nbt deserialization
     * @param tag to deserialize
     */
    abstract public void deserializeTag(NbtCompound tag);

    public TileEntity getTile() {
        return tile;
    }
}
