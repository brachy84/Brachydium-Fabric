package brachy84.brachydium.api.blockEntity;

import net.minecraft.util.Identifier;

import java.util.Map;

public class TieredBlockEntityGroup extends IntBlockEntityGroup {

    public TieredBlockEntityGroup(Identifier id, Map<Integer, TileEntity> map) {
        super(id, map);
    }

    @Override
    public Class<?>[] getRequiredTypes() {
        return new Class[]{
                ITiered.class
        };
    }

    @Override
    public Integer getKeyOfTile(TileEntity tile) {
        return ((ITiered) tile).getTier();
    }


}
