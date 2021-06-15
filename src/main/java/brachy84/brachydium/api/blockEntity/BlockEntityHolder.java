package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.api.IUIHolder;
import brachy84.brachydium.gui.widgets.RootWidget;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A BLockEntity which can hold a TileEntity of a BlockEntityGroup
 * see also: {@link TileEntity}, {@link BlockEntityGroup}
 */
public class BlockEntityHolder extends BlockEntity implements IUIHolder {

    private BlockEntityGroup<?> group;
    public final Identifier id;
    @Nullable
    private TileEntity currentTile;

    public BlockEntityHolder(BlockEntityGroup<?> group, BlockPos pos, BlockState state) {
        super(group.getType(), pos, state);
        this.id = group.id;
    }

    /*public BlockEntityHolder(Identifier id, BlockEntityType<?> type) {
        super(type);
        this.id = id;
    }*/


    public void tick() {
        if(currentTile != null)
            currentTile.tick();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putString("ID", group.id.toString());
        tag.put("Tile", currentTile.serializeTag());
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (currentTile == null) {
            currentTile = group.getBlockEntity(tag);
        }
        currentTile.deserializeTag(tag.getCompound("Tile"));
    }


    public void setActiveTileEntity(TileEntity tile) {
        if(currentTile != null) {
            currentTile.onDetach();
            currentTile.setHolder(null);
        }
        this.currentTile = tile;
        currentTile.setHolder(this);
        currentTile.onAttach();
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

    @Override
    public boolean hasUI() {
        if (currentTile != null) {
            return currentTile.hasUI();
        }
        return false;
    }

    @Override
    public @NotNull ModularGui createUi(PlayerEntity player) {
        if (currentTile != null) {
            return currentTile.createUi(player);
        }
        return new ModularGui(RootWidget.builder().build(), this, player);
    }
}
