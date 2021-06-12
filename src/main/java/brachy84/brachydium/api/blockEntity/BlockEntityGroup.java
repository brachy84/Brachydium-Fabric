package brachy84.brachydium.api.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public abstract class BlockEntityGroup<K> {

    protected final Map<K, TileEntity> blockEntityMap = new HashMap<>();
    public final Identifier id;
    private BlockEntityType<BlockEntityHolder> type;
    private BlockItem item;
    private Block block;

    protected BlockEntityGroup(Identifier id, Map<K, TileEntity> blockEntityMap) {
        this.blockEntityMap.putAll(blockEntityMap);
        this.id = id;
    }

    public abstract TileEntity getBlockEntity(CompoundTag tag);

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
}
