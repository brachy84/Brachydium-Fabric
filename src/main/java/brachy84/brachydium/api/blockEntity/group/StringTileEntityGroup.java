package brachy84.brachydium.api.blockEntity.group;

import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.blockEntity.TileEntityGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringTileEntityGroup extends TileEntityGroup<String> {

    public static StringTileEntityGroup create(Identifier id, Function<TileEntity, String> mapper, TileEntity... tileEntities) {
        return new StringTileEntityGroup(id, Arrays.stream(tileEntities).collect(Collectors.toMap(mapper, tileEntity -> tileEntity)));
    }

    public StringTileEntityGroup(Identifier id, Map<String, TileEntity> map) {
        super(id, map);
    }

    @Override
    public void writeNbt(NbtCompound tag, String s) {
        tag.putString(TILE_KEY, s);
    }

    @Override
    public String readKey(NbtCompound tag) {
        return tag.getString(TILE_KEY);
    }
}
