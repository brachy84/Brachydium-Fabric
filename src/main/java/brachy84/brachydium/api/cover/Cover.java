package brachy84.brachydium.api.cover;

import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.item.BrachydiumItem;
import brachy84.brachydium.api.render.Texture;
import brachy84.brachydium.api.render.TileRenderUtil;
import brachy84.brachydium.api.util.BrachydiumRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class Cover {

    public static final BrachydiumRegistry<Identifier, Cover> REGISTRY = new BrachydiumRegistry<>();

    public static Cover register(Cover cover) {
        REGISTRY.register(cover.id, cover);
        return cover;
    }

    private final Identifier id;
    private final BrachydiumItem itemForm;
    private Direction attachedSide;
    private ICoverable coverHolder;

    public Cover(BrachydiumItem item, Identifier id) {
        this.id = Objects.requireNonNull(id);
        this.itemForm = Objects.requireNonNull(item);
    }

    public Identifier getId() {
        return id;
    }

    public void onAttach(ICoverable coverHolder, Direction side) {
        this.coverHolder = coverHolder;
        this.attachedSide = side;
    }

    public void onDetach() {
        this.coverHolder = null;
        this.attachedSide = null;
    }

    public boolean isAttached() {
        return attachedSide != null && coverHolder != null;
    }

    public abstract boolean canPlaceOn(Direction direction, ICoverable coverable);

    public void render(QuadEmitter emitter, Direction side) {
        TileRenderUtil.renderSide(emitter, side, getTexture());
    }

    public abstract Texture getTexture();

    public float getPlateThickness() {
        return 2.0f;
    }

    public boolean isTicking() {
        return false;
    }

    public void tick() {
    }

    public final void syncCustomData(int id, Consumer<PacketByteBuf> consumer) {
        if(coverHolder instanceof TileEntity tile) {
            tile.syncCoverData(this, id, consumer);
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    @Environment(EnvType.CLIENT)
    public void readCustomData(int id, PacketByteBuf buf) {
    }

    /**
     * Is called when the holder is serialized
     * @param tag to save to
     */
    public abstract void serializeNbt(NbtCompound tag);

    /**
     * Load saved data. {@link #isAttached()} should always be true
     * @param tag to load from
     */
    public abstract void deserializeNbt(NbtCompound tag);

    public void serializeClientNbt(NbtCompound tag) {
    }

    public void deserializeClientNbt(NbtCompound tag) {
    }

    public Direction getAttachedSide() {
        return attachedSide;
    }

    public ICoverable getCoverHolder() {
        return coverHolder;
    }

    public ItemStack asStack(int amount) {
        return new ItemStack(itemForm, amount);
    }
}
