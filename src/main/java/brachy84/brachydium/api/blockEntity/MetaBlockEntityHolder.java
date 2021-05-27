package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.render.IOverlayRenderer;
import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.api.IUIHolder;
import brachy84.brachydium.gui.widgets.RootWidget;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import org.jetbrains.annotations.NotNull;

public class MetaBlockEntityHolder extends BlockEntity implements Tickable, IUIHolder {

    protected MetaBlockEntity metaTileEntity;

    private IOverlayRenderer overlayRenderer;

    public MetaBlockEntityHolder(MetaBlockEntity mbe) {
        super(mbe.getEntityType());
        this.metaTileEntity = mbe.recreate();
        this.metaTileEntity.holder = this;
    }

    public MetaBlockEntity setMetaBlockEntity(MetaBlockEntity mbe) {
        return this.metaTileEntity = mbe.recreate();
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

    @Override
    public boolean hasUI() {
        return metaTileEntity.hasUi();
    }

    @Override
    public @NotNull ModularGui createUi(PlayerEntity player) {
        if(hasUI()) {
            return new ModularGui(metaTileEntity.createUi(player), this, player);
        }
        return null;
    }
}
