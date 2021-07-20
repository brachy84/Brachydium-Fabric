package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.api.IUIHolder;
import brachy84.brachydium.gui.widgets.RootWidget;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A BLockEntity which can hold a TileEntity of a BlockEntityGroup
 * see also: {@link TileEntity}, {@link TileEntityGroup}
 */
public class BlockEntityHolder extends BlockEntity implements BlockEntityClientSerializable, IUIHolder {

    @Nullable
    public static BlockEntityHolder getOf(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof BlockEntityHolder)
            return (BlockEntityHolder) blockEntity;
        return null;
    }

    private final TileEntityGroup<?> group;
    public final Identifier id;
    @Nullable
    private TileEntity currentTile;
    private TileEntityFactory<?> currentFactory;

    public BlockEntityHolder(TileEntityGroup<?> group, BlockPos pos, BlockState state) {
        super(group.getType(), pos, state);
        this.group = group;
        this.id = group.id;
    }

    public BlockEntityHolder(TileEntity tile, BlockPos pos, BlockState state) {
        this(Objects.requireNonNull(tile.getGroup(), "TileEntity does not belong to a group!"), pos, state);
    }

    public void tick() {
        if (currentTile != null)
            currentTile.tick();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putString("ID", group.id.toString());
        if (currentTile != null) {
            tag.put("Tile", currentTile.serializeTag());
            group.writeTileNbt(tag, currentFactory);
            tag.putInt("dir", currentTile.getFrontFace().getId());
        }
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (currentTile == null) {
            setActiveTileEntity(group.getBlockEntity(tag), Direction.byId(tag.getInt("dir")));
        }
        currentTile.deserializeTag(tag.getCompound("Tile"));
    }

    public void setActiveTileEntity(TileEntityFactory<?> factory, Direction front) {
        if (this.currentTile != null) {
            this.currentTile.onDetach();
            this.currentTile.setHolder(null);
        }
        this.currentFactory = factory;
        this.currentTile = factory.create();
        this.currentTile.setHolder(this);
        this.currentTile.setFrontFace(front);
        this.currentTile.onAttach();
    }

    public TileEntity getActiveTileEntity() {
        return currentTile;
    }

    public TileEntityGroup<?> getGroup() {
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

    @Override
    public void fromClientTag(NbtCompound tag) {
        if (currentTile == null) {
            setActiveTileEntity(group.getBlockEntity(tag), Direction.byId(tag.getInt("dir")));
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        if(currentTile == null) return tag;
        group.writeTileNbt(tag, currentFactory);
        tag.putInt("dir", currentTile.getFrontFace().getId());
        return tag;
    }
}
