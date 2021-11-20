package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.network.Channels;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SyncedBlockEntity extends BlockEntity {

    public SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void syncCustomData(int id, Consumer<PacketByteBuf> consumer, Collection<ServerPlayerEntity> players) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeVarInt(id);
        consumer.accept(buf);
        players.forEach(player -> ServerPlayNetworking.send(player, Channels.SYNC_TILE_CUSTOM, buf));
    }

    public void syncCustomData(int id, Consumer<PacketByteBuf> consumer, int range) {
        List<ServerPlayerEntity> players = getPlayersInRange(range).stream().map(player -> (ServerPlayerEntity) player).collect(Collectors.toList());
        syncCustomData(id, consumer, players);
    }

    public void syncCustomData(int id, Consumer<PacketByteBuf> consumer) {
        syncCustomData(id, consumer, 64);
    }

    public List<PlayerEntity> getPlayersInRange(double range) {
        return world == null ? List.of() : world.getPlayers().stream().filter(player -> Math.sqrt(player.getBlockPos().getSquaredDistance(pos)) <= range).collect(Collectors.toList());
    }

    @Environment(EnvType.CLIENT)
    public void readCustomData(int id, PacketByteBuf buf) {
    }
}
