package brachy84.brachydium.api.blockEntity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashMap;

/**
 * A BlockEntityGroup which can only hold one tile
 */
public class SingleBlockEntity extends BlockEntityGroup<Void> {

    private final TileEntity tileEntity;

    protected SingleBlockEntity(Identifier id, TileEntity tileEntity) {
        super(id, new HashMap<>());
        this.tileEntity = tileEntity;
    }

    @Override
    public TileEntity getBlockEntity(NbtCompound tag) {
        return tileEntity;
    }
}
