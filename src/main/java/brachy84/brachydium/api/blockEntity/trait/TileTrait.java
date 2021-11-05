package brachy84.brachydium.api.blockEntity.trait;

import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.handlers.ApiHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class TileTrait extends ApiHolder {

    public static String getTraitName(Class<? extends TileTrait> clazz) {
        return clazz.getSimpleName();
    }

    public static String getTraitName(TileTrait trait) {
        return trait.getClass().getSimpleName();
    }

    protected final TileEntity tile;
    private final String id;

    public TileTrait(TileEntity tile) {
        this.tile = Objects.requireNonNull(tile);
        this.id = getTraitName(this);
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

    /**
     * Gets called every tick
     */
    @ApiStatus.OverrideOnly
    public void tick() {
    }

    @ApiStatus.NonExtendable
    public final void syncCustomData(int id, Consumer<PacketByteBuf> consumer) {
        tile.syncTraitData(this, id, consumer);
    }

    @ApiStatus.OverrideOnly
    @Environment(EnvType.CLIENT)
    public void readCustomData(int id, PacketByteBuf buf) {
    }

    @ApiStatus.OverrideOnly
    public void writeInitialData(PacketByteBuf buf) {
    }

    @ApiStatus.OverrideOnly
    public void receiveInitialData(PacketByteBuf buf) {
    }

    /**
     * Gets called on MetaBlockEntity nbt serialization
     *
     * @return a serialized Tag
     */
    abstract public NbtCompound serializeNbt();

    /**
     * Gets called on MetaBlockEntity nbt deserialization
     *
     * @param tag to deserialize
     */
    abstract public void deserializeNbt(NbtCompound tag);

    public TileEntity getTile() {
        return tile;
    }
}
