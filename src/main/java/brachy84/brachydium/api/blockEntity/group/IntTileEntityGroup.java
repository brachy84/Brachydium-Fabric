package brachy84.brachydium.api.blockEntity.group;

import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.blockEntity.TileEntityGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IntTileEntityGroup extends TileEntityGroup<Integer> {

    public static IntTileEntityGroup create(Identifier id, Function<TileEntity, Integer> mapper, TileEntity... factories) {
        return new IntTileEntityGroup(id, Arrays.stream(factories).collect(Collectors.toMap(mapper, factory -> factory)));
    }

    public IntTileEntityGroup(Identifier id, Map<Integer, TileEntity> map) {
        super(id, map);
    }

    @Override
    public Integer readKey(NbtCompound tag) {
        return tag.getInt(TILE_KEY);
    }

    @Override
    public void writeNbt(NbtCompound tag, Integer integer) {
        tag.putInt(TILE_KEY, integer);
    }
}
