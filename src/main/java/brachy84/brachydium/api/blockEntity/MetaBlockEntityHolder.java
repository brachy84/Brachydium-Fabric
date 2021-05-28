package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.api.IUIHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public class MetaBlockEntityHolder extends BlockEntity implements Tickable, IUIHolder {

    protected MetaBlockEntity metaTileEntity;

    public MetaBlockEntityHolder(MetaBlockEntity mbe) {
        this(mbe, mbe.getFrontFacing());
    }

    public MetaBlockEntityHolder(MetaBlockEntity mbe, Direction facing) {
        super(mbe.getEntityType());
        setMetaBlockEntity(mbe, facing);
    }

    public void setMetaBlockEntity(MetaBlockEntity mbe, Direction facing) {
        this.metaTileEntity = mbe.recreate();
        this.metaTileEntity.holder = this;
        this.metaTileEntity.setFrontFacing(facing);
        this.metaTileEntity.setBlockEntityType(mbe.getEntityType());
        this.metaTileEntity.setBlock(mbe.getBlock());
        this.metaTileEntity.setBlockItem(mbe.getItem());
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
        if(metaTileEntity != null) {
            metaTileEntity.tick();
        }
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
