package brachy84.brachydium.api.blockEntity;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Objects;

/**
 * A BlockEntityGroup can hold multiple {@link TileEntity} which are serialized in nbt
 */
public class TileEntityGroup {

    public static final String TILE_KEY = "TileKey";

    protected final TileEntity[] tileEntities;
    public final Identifier id;
    public final String tileName;
    private BlockEntityType<BlockEntityHolder> type;
    private BlockItem item;
    private Block block;

    public TileEntityGroup(Identifier id, TileEntity... tileEntities) {
        if (!Objects.requireNonNull(id).getPath().startsWith("tile/"))
            id = new Identifier(id.getNamespace(), "tile/" + id.getPath());
        this.id = id;
        this.tileName = id.getPath().split("/")[1];
        if(tileEntities.length == 0)
            throw new IllegalArgumentException("There must be at least one TileEntity in a group");
        this.tileEntities = tileEntities;
        for (int i = 0; i < tileEntities.length; i++) {
            TileEntity tile = Objects.requireNonNull(this.tileEntities[i]);
            if (!isValid(tile))
                throw new IllegalArgumentException("Tile of type " + tile.getClass().getSimpleName() + " is not valid for " + this.getClass().getSimpleName());
            tile.setGroup(this, i);
            tile.setUp();
        }
    }

    public boolean isValid(TileEntity tile) {
        return true;
    }

    public TileEntity getBlockEntity(NbtCompound tag) {
        if (tag == null)
            throw new IllegalStateException("Tag can't be null");//return (TileEntity) blockEntityMap.values().toArray()[0];
        if (!tag.contains(TILE_KEY)) {
            throw new IllegalStateException("Tag does not contain " + TILE_KEY);
        }
        return tileEntities[readKey(tag)];
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

    public Collection<TileEntity> getTileEntities() {
        return Lists.newArrayList(tileEntities);
    }

    public TileEntity getFallbackTile() {
        return tileEntities[0];
    }
}
