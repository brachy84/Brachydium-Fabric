package brachy84.brachydium.api.blockEntity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TieredBlockEntityGroup extends BlockEntityGroup<Integer> {

    public TieredBlockEntityGroup(Identifier id, Map<Integer, TileEntity> map) {
        super(id, map);
    }

    public static TieredBlockEntityGroup of(Identifier id, TileEntity... tileEntities) {
        Map<Integer, TileEntity> tileEntityMap = new HashMap<>();
        for(int i = 0; i < tileEntities.length; i++) {
            tileEntityMap.put(i, tileEntities[i]);
        }
        return new TieredBlockEntityGroup(id, tileEntityMap);
    }

    @Override
    public TileEntity getBlockEntity(NbtCompound tag) {
        if (!tag.contains("tier")) {
            throw new IllegalStateException("Tag does not contain 'tier'");
        }
        return blockEntityMap.get(tag.getInt("tier"));
    }
}
