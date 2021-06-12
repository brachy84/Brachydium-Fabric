package brachy84.brachydium.api.blockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;

public class BlockEntityHolder extends BlockEntity {

    private BlockEntityGroup<?> group;
    public final Identifier id;
    private TileEntity currentTile;

    public BlockEntityHolder(BlockEntityGroup<?> group) {
        super(group.getType());
        this.id = group.id;
    }

    public BlockEntityHolder(Identifier id, BlockEntityType<?> type) {
        super(type);
        this.id = id;
    }

    public void setActiveTileEntity(TileEntity tile) {
        this.currentTile = tile;
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
