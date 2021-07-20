package brachy84.brachydium.api.blockEntity;

import java.util.Objects;

public class TileEntityFactory<T extends TileEntity> {

    private final T tile;
    private final Factory<T> factory;

    public TileEntityFactory(T tile, Factory<T> factory) {
        this.tile = Objects.requireNonNull(tile);
        this.factory = Objects.requireNonNull(factory);
    }

    public TileEntity getOriginal() {
        return tile;
    }

    public T create() {
        T t = factory.create(tile);
        t.setFactory(this);
        t.setGroup(tile.getGroup());
        t.setUp();
        return t;
    }

    public interface Factory<T extends TileEntity> {
        T create(T original);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileEntityFactory<?> that = (TileEntityFactory<?>) o;
        return Objects.equals(tile, that.tile) && Objects.equals(factory, that.factory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tile, factory);
    }
}
