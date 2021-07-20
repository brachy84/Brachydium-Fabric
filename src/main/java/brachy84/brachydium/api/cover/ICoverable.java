package brachy84.brachydium.api.cover;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

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

    default NbtCompound serializeCovers() {
        NbtCompound tag = new NbtCompound();
        for (Direction direction : Direction.values()) {
            Cover cover = getCover(direction);
            if (cover == null) continue;
            NbtCompound coverTag = new NbtCompound();
            coverTag.putString("id", cover.getId().toString());
            cover.serializeNbt(coverTag);
            tag.put(direction.asString(), coverTag);
        }
        return tag;
    }

    default void deserializeCovers(NbtCompound tag) {
        for (Direction dir : Direction.values()) {
            if (tag.contains(dir.asString())) {
                NbtCompound coverTag = tag.getCompound(dir.asString());
                Cover cover = Cover.REGISTRY.getEntry(new Identifier(coverTag.getString("id")));
                placeCover(cover, dir);
                cover.deserializeNbt(coverTag);
            }
        }
    }
}
