package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.network.Channels;
import brachy84.brachydium.gui.api.Gui;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
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
    public static BlockEntityHolder getOf(BlockView world, BlockPos pos) {
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
        if (currentTile != null && currentTile.isTicking())
            currentTile.tick();
    }

    @Override
    public void readCustomData(int id, PacketByteBuf buf) {
        if (currentTile != null)
            currentTile.readCustomData(id, buf);
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
        if (currentTile != null) {
            super.setWorld(world);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Brachydium.LOGGER.info("Write NBT");
        if (currentTile != null) {
            tag.put("Tile", currentTile.serializeNbt());
            group.writeNbt(tag, currentTile.getGroupKey());
            tag.putInt("dir", currentTile.getFrontFace().getId());
        }
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        Brachydium.LOGGER.info("Read NBT");
        if (currentTile == null) {
            if (tag.contains(TileEntityGroup.TILE_KEY)) {
                setActiveTileEntity(group.getBlockEntity(tag), Direction.byId(tag.getInt("dir")));
                currentTile.deserializeNbt(tag.getCompound("Tile"));
            }
        }
    }

    public void setActiveTileEntity(TileEntity tile, Direction front) {
        if (this.currentTile != null) {
            this.currentTile.onDetach();
            this.currentTile.setHolder(null);
        }
        Brachydium.LOGGER.info("Setting tile {}. World {}", tile.getGroupKey(), world == null ? "null" : world.isClient);
        this.currentTile = tile.createCopyInternal();
        this.currentTile.setHolder(this);
        this.currentTile.setFrontFace(front);
        this.currentTile.onAttach();
        if(world != null && world.isClient) {
            scheduleRenderUpdate();
        }
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
        if (world instanceof ClientWorld clientWorld) {
            clientWorld.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public boolean hasUI() {
        return currentTile != null && currentTile.hasUI();
    }

    public @NotNull Gui createUi(PlayerEntity player) {
        if (currentTile != null) {
            return currentTile.createUi(player);
        }
        return Gui.defaultBuilder(player).build();
    }

    public void syncPlaceData() {
        if (world != null && !world.isClient) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);
            buf.writeVarInt(currentTile.getGroupKey());
            buf.writeVarInt(currentTile.getFrontFace().getId());
            getPlayersInRange(64).forEach(player -> {
                ServerPlayNetworking.send((ServerPlayerEntity) player, Channels.SYNC_TILE_INIT, buf);
            });
        }
    }

    public void readPlaceData(PacketByteBuf buf) {
        int tileId = buf.readVarInt();
        int front = buf.readVarInt();
        if (currentTile == null) {
            setActiveTileEntity(group.getTile(tileId), Direction.byId(front));
        }
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        if (currentTile == null) {
            setActiveTileEntity(group.getBlockEntity(tag), Direction.byId(tag.getInt("dir")));
        }
        currentTile.deserializeClientNbt(tag.getCompound("Tile"));
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        if (currentTile == null) return tag;
        group.writeNbt(tag, currentTile.getGroupKey());
        tag.putInt("dir", currentTile.getFrontFace().getId());
        tag.put("Tile", currentTile.serializeClientNbt());
        return tag;
    }
}
