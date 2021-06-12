package brachy84.brachydium.api.blockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import java.util.HashMap;

/**
 * A BlockEntityGroup which can only hold one tile
 */
public class SingleBlockEntity extends BlockEntityGroup<Void> {

    private TileEntity tileEntity;

    protected SingleBlockEntity(Identifier id, TileEntity tileEntity) {
        super(id, new HashMap<>());
        this.tileEntity = tileEntity;
    }

    @Override
    public TileEntity getBlockEntity(CompoundTag tag) {
        return tileEntity;
    }
}
