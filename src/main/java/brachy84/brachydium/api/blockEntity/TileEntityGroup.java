package brachy84.brachydium.api.blockEntity;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A BlockEntityGroup can hold multiple {@link TileEntity} which are serialized in nbt
 */
public class TileEntityGroup {

    public static final String TILE_KEY = "TileKey";

    protected final TileEntity[] tileEntities;
    public final Identifier id;
    public final Identifier registryId;
    private BlockEntityType<BlockEntityHolder> type;
    private BlockItem item;
    private Block block;

    public TileEntityGroup(Identifier id, TileEntity... tileEntities) {
        this.id = Objects.requireNonNull(id, "TileEntityGroup id can not be null");
        this.registryId = new Identifier(id.getNamespace(), "tile/" + id.getPath());
        if(tileEntities.length == 0)
            throw new IllegalArgumentException("There must be at least one TileEntity in a group");
        this.tileEntities = tileEntities;
        for (int i = 0; i < tileEntities.length; i++) {
            TileEntity tile = Objects.requireNonNull(this.tileEntities[i], "Null TileEntities are not allowed. GroupId " + id);
            tile.setGroup(this, i);
            tile.setUp();
        }
    }

    public TileEntityGroup(Identifier id, List<TileEntity> tileEntities) {
        this(id, tileEntities.toArray(new TileEntity[0]));
    }

    public TileEntity getBlockEntity(NbtCompound tag) {
        if (tag == null)
            throw new IllegalStateException("Tag can't be null");//return (TileEntity) blockEntityMap.values().toArray()[0];
        if (!tag.contains(TILE_KEY)) {
            throw new IllegalStateException("Tag does not contain " + TILE_KEY);
        }
        return tileEntities[readKey(tag)];
    }

    public Optional<TileEntity> tryGetBlockEntity(NbtCompound tag) {
        if (tag == null || !tag.contains(TILE_KEY))
            return Optional.empty();
        int key = readKey(tag);
        if(key >= tileEntities.length)
            return Optional.empty();
        return Optional.of(tileEntities[readKey(tag)]);
    }

    public TileEntity getTile(int key) {
        return tileEntities[key];
    }

    public void writeNbt(NbtCompound tag, Integer key) {
        tag.putInt(TILE_KEY, key);
    }

    public int readKey(NbtCompound tag) {
        return tag.getInt(TILE_KEY);
    }

    @ApiStatus.Internal
    public void writeTileNbt(NbtCompound tag, TileEntity tile) {
        writeNbt(tag, tile.getGroupKey());
    }

    public BlockEntityType<BlockEntityHolder> getType() {
        return type;
    }

    /**
     * Should only be called Internally from {@link brachy84.brachydium.api.BrachydiumApi#registerTileEntityGroup(TileEntityGroup)}
     */
    @ApiStatus.Internal
    public void register(Block block, BlockItem item, BlockEntityType<BlockEntityHolder> type) {
        if(this.type != null)
            throw new IllegalStateException("register should only be called once");
        this.block = block;
        this.item = item;
        this.type = type;
    }

    public BlockItem getItem() {
        return item;
    }

    public Block getBlock() {
        return block;
    }

    public Collection<TileEntity> getTileEntities() {
        return Lists.newArrayList(tileEntities);
    }

    public TileEntity getFallbackTile() {
        return tileEntities[0];
    }
}
