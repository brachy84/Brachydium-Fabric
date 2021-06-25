package brachy84.brachydium.api.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BlockEntityGroup<K> {

    public static final String TILE_KEY = "TileKey";

    protected final Map<K, TileEntity> blockEntityMap = new HashMap<>();
    public final Identifier id;
    private BlockEntityType<BlockEntityHolder> type;
    private BlockItem item;
    private Block block;

    protected BlockEntityGroup(Identifier id, Map<K, TileEntity> blockEntityMap) {
        this.blockEntityMap.putAll(blockEntityMap);
        if (!id.getPath().startsWith("tile/")) {
            id = new Identifier(id.getNamespace(), "tile/" + id.getPath());
        }
        this.id = id;
        for (TileEntity tile : blockEntityMap.values()) {
            for (Class<?> clazz : getRequiredTypes()) {
                if (!clazz.isAssignableFrom(tile.getClass())) {
                    throw new IllegalArgumentException("TileEntity needs to extend " + clazz.getCanonicalName());
                }
            }
            tile.setGroup(this);
        }
    }

    public Class<?>[] getRequiredTypes() {
        return new Class[0];
    }

    public abstract TileEntity getBlockEntity(NbtCompound tag);

    public abstract void writeNbt(NbtCompound tag, K k);

    @ApiStatus.Internal
    public void writeTileNbt(NbtCompound tag, TileEntity tile) {
        writeNbt(tag, getKeyOfTile(tile));
    }

    public abstract K getKeyOfTile(TileEntity tile);

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
        return blockEntityMap.values();
    }
}
