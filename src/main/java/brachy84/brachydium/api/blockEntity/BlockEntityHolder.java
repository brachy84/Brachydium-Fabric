package brachy84.brachydium.api.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import org.jetbrains.annotations.Nullable;

public class BlockEntityHolder extends BlockEntity implements Tickable {

    private BlockEntityGroup<?> group;
    public final Identifier id;
    @Nullable
    private TileEntity currentTile;

    public BlockEntityHolder(BlockEntityGroup<?> group) {
        super(group.getType());
        this.id = group.id;
    }

    public BlockEntityHolder(Identifier id, BlockEntityType<?> type) {
        super(type);
        this.id = id;
    }

    @Override
    public void tick() {
        if(currentTile != null)
            currentTile.tick();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putString("ID", group.id.toString());
        tag.put("Tile", currentTile.serializeTag());
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (currentTile == null) {
            currentTile = group.getBlockEntity(tag);
        }
        currentTile.deserializeTag(tag.getCompound("Tile"));
    }


    public void setActiveTileEntity(TileEntity tile) {
        if(currentTile != null) currentTile.setHolder(null);
        this.currentTile = tile;
        currentTile.setHolder(this);
    }

    public TileEntity getActiveTileEntity() {
        return currentTile;
    }

    public BlockEntityGroup<?> getGroup() {
        return group;
    }

    public Identifier getId() {
        return id;
    }
}
