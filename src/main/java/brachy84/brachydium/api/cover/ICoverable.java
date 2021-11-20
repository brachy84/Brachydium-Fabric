package brachy84.brachydium.api.cover;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface ICoverable {

    @Nullable
    Cover getCover(Direction direction);

    boolean canPlaceCover(Cover cover, Direction face);

    /**
     * @param cover to attach
     * @param side  to attach on
     */
    void placeCover(Cover cover, Direction side);

    /**
     * @param side to detach off
     */
    void removeCover(Direction side);

    void syncCustomData(int id, Consumer<PacketByteBuf> consumer);

    @Environment(EnvType.CLIENT)
    default void readCustomData(int id, PacketByteBuf buf) {
    }

    default NbtCompound serializeCovers(boolean client) {
        NbtCompound tag = new NbtCompound();
        for (Direction direction : Direction.values()) {
            Cover cover = getCover(direction);
            if (cover == null) continue;
            NbtCompound coverTag = new NbtCompound();
            coverTag.putString("id", cover.getId().toString());
            if (client) {
                cover.serializeClientNbt(coverTag);
            } else {
                cover.serializeNbt(coverTag);
            }
            tag.put(direction.asString(), coverTag);
        }
        return tag;
    }

    default void deserializeCovers(NbtCompound tag, boolean client) {
        for (Direction dir : Direction.values()) {
            if (tag.contains(dir.asString())) {
                NbtCompound coverTag = tag.getCompound(dir.asString());
                Cover cover = Cover.REGISTRY.getEntry(new Identifier(coverTag.getString("id")));
                placeCover(cover, dir);
                if (client) {
                    cover.deserializeClientNbt(coverTag);
                } else {
                    cover.deserializeNbt(coverTag);
                }
            }
        }
    }
}
