package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.blockEntity.group.IntTileEntityGroup;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A BlockEntityGroup can hold multiple {@link TileEntity} which are serialized in nbt
 *
 * @param <K> The type of key to set and get a {@link TileEntity}
 */
public abstract class TileEntityGroup<K> {

    public static final String TILE_KEY = "TileKey";

    public static IntTileEntityGroup createOfArray(Identifier id, TileEntity... tileEntities) {
        Map<Integer, TileEntity> map = new HashMap<>();
        for (int i = 0; i < tileEntities.length; i++)
            map.put(i, tileEntities[i]);
        return new IntTileEntityGroup(id, map);
    }

    protected final BiMap<K, TileEntityFactory<?>> blockEntityMap;
    public final Identifier id;
    public final String tileName;
    private BlockEntityType<BlockEntityHolder> type;
    private BlockItem item;
    private Block block;

    protected TileEntityGroup(Identifier id, Map<K, TileEntity> blockEntityMap) {
        if (!Objects.requireNonNull(id).getPath().startsWith("tile/"))
            id = new Identifier(id.getNamespace(), "tile/" + id.getPath());
        this.id = id;
        this.tileName = id.getPath().split("/")[1];
        Objects.requireNonNull(blockEntityMap);
        this.blockEntityMap = HashBiMap.create(blockEntityMap.size());
        for (Map.Entry<K, TileEntity> entry : blockEntityMap.entrySet()) {
            TileEntity tile = Objects.requireNonNull(entry.getValue());
            Objects.requireNonNull(entry.getKey());
            if (!isValid(tile))
                throw new IllegalArgumentException("Tile of type " + tile.getClass().getSimpleName() + " is not valid for " + this.getClass().getSimpleName());
            this.blockEntityMap.put(entry.getKey(), tile.createAndSetFactory());
            tile.setGroup(this);
            tile.setUp();
        }
    }

    public boolean isValid(TileEntity tile) {
        return true;
    }

    public TileEntityFactory<?> getBlockEntity(NbtCompound tag) {
        if (tag == null)
            throw new IllegalStateException("Tag can't be null");//return (TileEntity) blockEntityMap.values().toArray()[0];
        if (!tag.contains(TILE_KEY)) {
            throw new IllegalStateException("Tag does not contain " + TILE_KEY);
        }
        return blockEntityMap.get(readKey(tag));
    }

    public abstract void writeNbt(NbtCompound tag, K k);

    public abstract K readKey(NbtCompound tag);

    @ApiStatus.Internal
    public void writeTileNbt(NbtCompound tag, TileEntityFactory<?> tile) {
        writeNbt(tag, blockEntityMap.inverse().get(tile));
    }

    public BlockEntityType<BlockEntityHolder> getType() {
        return type;
    }

    @ApiStatus.Internal
    public void setType(BlockEntityType<BlockEntityHolder> type) {
        this.type = type;
    }

    public BlockItem getItem() {
        return item;
    }

    @ApiStatus.Internal
    public void setItem(BlockItem item) {
        this.item = item;
    }

    public Block getBlock() {
        return block;
    }

    @ApiStatus.Internal
    public void setBlock(Block block) {
        this.block = block;
    }

    public Collection<TileEntityFactory<?>> getTileEntities() {
        return blockEntityMap.values();
    }
}
