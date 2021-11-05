package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.gui.internal.Gui;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
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
public class BlockEntityHolder extends SyncedBlockEntity implements BlockEntityClientSerializable {

    @Nullable
    public static BlockEntityHolder getOf(World world, BlockPos pos) {
        if (world == null || pos == null) return null;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BlockEntityHolder)
            return (BlockEntityHolder) blockEntity;
        return null;
    }

    private final TileEntityGroup group;
    public final Identifier id;
    @Nullable
    private TileEntity currentTile;

    public BlockEntityHolder(TileEntityGroup group, BlockPos pos, BlockState state) {
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
    public void readCustomData(int id, PacketByteBuf buf) {
        if(currentTile != null)
            currentTile.readCustomData(id, buf);
    }

    @Override
    public void writeInitialData(PacketByteBuf buf) {
        if(currentTile != null)
            currentTile.writeInitialData(buf);
    }

    @Override
    public void receiveInitialData(PacketByteBuf buf) {
        if(currentTile != null)
            currentTile.receiveInitialData(buf);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putString("ID", group.id.toString());
        if (currentTile != null) {
            tag.put("Tile", currentTile.serializeTag());
            group.writeNbt(tag, currentTile.getGroupKey());
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

    public void setActiveTileEntity(TileEntity tile, Direction front) {
        if (this.currentTile != null) {
            this.currentTile.onDetach();
            this.currentTile.setHolder(null);
        }
        this.currentTile = tile.createCopyInternal();
        this.currentTile.setHolder(this);
        this.currentTile.setFrontFace(front);
        this.currentTile.onAttach();
    }

    public TileEntity getActiveTileEntity() {
        return currentTile;
    }

    public TileEntityGroup getGroup() {
        return group;
    }

    public Identifier getId() {
        return id;
    }

    public void scheduleRenderUpdate() {
        if(world instanceof ClientWorld) {
            ((ClientWorld) world).scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public boolean hasUI() {
        if (currentTile != null) {
            return currentTile.hasUI();
        }
        return false;
    }

    public @NotNull Gui createUi(PlayerEntity player) {
        if (currentTile != null) {
            return currentTile.createUi(player);
        }
        return Gui.defaultBuilder(player).build();
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        if (currentTile == null) {
            setActiveTileEntity(group.getBlockEntity(tag), Direction.byId(tag.getInt("dir")));
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        if (currentTile == null) return tag;
        group.writeNbt(tag, currentTile.getGroupKey());
        tag.putInt("dir", currentTile.getFrontFace().getId());
        return tag;
    }
}
