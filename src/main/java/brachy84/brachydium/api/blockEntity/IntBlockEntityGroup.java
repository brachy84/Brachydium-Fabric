package brachy84.brachydium.api.blockEntity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Map;

public abstract class IntBlockEntityGroup extends BlockEntityGroup<Integer> {

    public IntBlockEntityGroup(Identifier id, Map<Integer, TileEntity> map) {
        super(id, map);
    }

    @Override
    public TileEntity getBlockEntity(NbtCompound tag) {
        if (tag == null) throw new IllegalStateException("Tag can't be null");//return (TileEntity) blockEntityMap.values().toArray()[0];
        if (!tag.contains(TILE_KEY)) {
            throw new IllegalStateException("Tag does not contain " + TILE_KEY);
        }
        return blockEntityMap.get(tag.getInt(TILE_KEY));
    }

    @Override
    public void writeNbt(NbtCompound tag, Integer integer) {
        tag.putInt(TILE_KEY, integer);
    }
}
