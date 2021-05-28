package brachy84.brachydium.api.block;

import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import brachy84.brachydium.api.blockEntity.MetaBlockEntityHolder;
import brachy84.brachydium.api.blockEntity.MetaBlockEntityUIFactory;
import brachy84.brachydium.Brachydium;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockMachine extends Block implements BlockEntityProvider {

    private Identifier id;

    private MetaBlockEntity metaBlockEntity = null;

    public BlockMachine(Identifier id) {
        super(FabricBlockSettings.of(net.minecraft.block.Material.METAL).strength(6f, 5f).nonOpaque());
        this.id = id;
    }

    public void ensureBlockEntityNotNull(BlockView world, BlockPos pos) {
        if (metaBlockEntity != null) return;
        if (id != null) {
            MetaBlockEntity mbe = MetaBlockEntity.getFromId(id);
            if (mbe != null) {
                metaBlockEntity = mbe;
                return;
            }
        }
        if (world != null && pos != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MetaBlockEntityHolder) {
                MetaBlockEntity mbe = ((MetaBlockEntityHolder) blockEntity).getMetaBlockEntity();
                if (mbe != null) {
                    metaBlockEntity = mbe;
                    id = metaBlockEntity.getId();
                }
            }
        }
    }

    @Nullable
    public MetaBlockEntity getMetaBlockEntity(BlockView world, BlockPos pos) {
        ensureBlockEntityNotNull(world, pos);
        return metaBlockEntity;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        Brachydium.LOGGER.info("Creating blockEntity");
        ensureBlockEntityNotNull(world, null);
        if (metaBlockEntity == null) {
            throw new NullPointerException("A MetaBlockEntity with id " + id + " doesn't exist");
        }
        return new MetaBlockEntityHolder(metaBlockEntity);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        Brachydium.LOGGER.info("BlockEntity placed!");
        //TODO: test this on server. If crashes, insert !isClient and sync facing
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MetaBlockEntityHolder) {
            MetaBlockEntity mbe = ((MetaBlockEntityHolder) blockEntity).getMetaBlockEntity();
            if (mbe != null && placer != null) {
                mbe.setFrontFacing(placer.getHorizontalFacing().getOpposite());
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MetaBlockEntityHolder) {
                MetaBlockEntityUIFactory.INSTANCE.openUI((MetaBlockEntityHolder) blockEntity, (ServerPlayerEntity) player);
            }
        }
        return ActionResult.SUCCESS;
    }
}
