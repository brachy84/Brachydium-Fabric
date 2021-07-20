package brachy84.brachydium.api.blockEntity.group;

import brachy84.brachydium.api.blockEntity.TileEntityFactory;
import brachy84.brachydium.api.blockEntity.TileEntityGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashMap;

/**
 * A BlockEntityGroup which can only hold one tile
 */
public class SingleTileEntity extends TileEntityGroup<Void> {

    private final TileEntityFactory<?> tileEntity;

    protected SingleTileEntity(Identifier id, TileEntityFactory<?> tileEntity) {
        super(id, new HashMap<>());
        this.tileEntity = tileEntity;
    }

    @Override
    public TileEntityFactory<?> getBlockEntity(NbtCompound tag) {
        return tileEntity;
    }

    @Override
    public final void writeNbt(NbtCompound tag, Void unused) {
    }

    @Override
    public Void readKey(NbtCompound tag) {
        return null;
    }

    @Override
    public final void writeTileNbt(NbtCompound tag, TileEntityFactory<?> tile) {
    }
}
