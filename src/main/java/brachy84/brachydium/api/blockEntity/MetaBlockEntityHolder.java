package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.render.IOverlayRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class MetaBlockEntityHolder extends BlockEntity implements Tickable {

    protected MetaBlockEntity metaTileEntity;

    private IOverlayRenderer overlayRenderer;

    private boolean hasOpenUi = false;

    /*public MetaBlockEntityHolder() {
        super(null);
    }*/

    public MetaBlockEntityHolder(BlockEntityType<?> type) {
        super(type);
    }

    public MetaBlockEntityHolder(MetaBlockEntity mbe) {
        super(mbe.getEntityType());
        this.metaTileEntity = mbe;
    }

    public MetaBlockEntity setMetaBlockEntity(MetaBlockEntity mbe) {
        return this.metaTileEntity = mbe;
    }

    public MetaBlockEntity getMetaBlockEntity() {
        return metaTileEntity;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putString("ID", metaTileEntity.getId().toString());
        tag.put("MetaBlockEntity", metaTileEntity.serializeTag());
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (metaTileEntity == null) {
            metaTileEntity = MetaBlockEntity.getFromId(new Identifier(tag.getString("ID")));
        }
        metaTileEntity.deserializeTag(tag.getCompound("MetaBlockEntity"));
    }

    @Override
    public void tick() {
        metaTileEntity.tick();
    }
}
