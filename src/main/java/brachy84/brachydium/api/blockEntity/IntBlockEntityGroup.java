package brachy84.brachydium.api.blockEntity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Map;

public abstract class IntBlockEntityGroup extends BlockEntityGroup<Integer> {

    public IntBlockEntityGroup(Identifier id, Map<Integer, TileEntity> map) {
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
