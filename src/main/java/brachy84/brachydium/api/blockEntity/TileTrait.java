package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.blockEntity.old.MetaBlockEntity;
import brachy84.brachydium.api.blockEntity.old.MetaBlockEntityHolder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public abstract class TileTrait {

    protected MetaBlockEntity metaBlockEntity;

    public TileTrait(MetaBlockEntity mbe) {
        this.metaBlockEntity = mbe;
        metaBlockEntity.addTrait(this);
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

    public abstract void addApis(BlockEntityType<MetaBlockEntityHolder> type);

    /**
     * Gets called every tick
     */
    public void tick() {}

    /**
     * Gets called on MetaBlockEntity nbt serialization
     * @return a serialized Tag
     */
    abstract public CompoundTag serializeTag();

    /**
     * Gets called on MetaBlockEntity nbt deserialization
     * @param tag to deserialize
     */
    abstract public void deserializeTag(CompoundTag tag);
}
