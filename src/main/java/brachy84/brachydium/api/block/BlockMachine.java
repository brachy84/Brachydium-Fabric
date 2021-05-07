package brachy84.brachydium.api.block;

import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import brachy84.brachydium.api.blockEntity.MetaBlockEntityHolder;
import brachy84.brachydium.api.gui_v1.BrachydiumGui;
import brachy84.brachydium.Brachydium;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockMachine extends Block implements BlockEntityProvider {

    private Identifier id;

    private MetaBlockEntity metaBlockEntity = null;

    public BlockMachine(Identifier id) {
        super(FabricBlockSettings.of(net.minecraft.block.Material.METAL).strength(6f, 5f));
        this.id = id;
    }

    public void ensureBlockEntityNotNull(BlockView world, BlockPos pos) {
        if(metaBlockEntity != null) return;
        if(id != null) {
            MetaBlockEntity mbe = MetaBlockEntity.getFromId(id);
            if(mbe != null) {
                metaBlockEntity = mbe;
                return;
            }
        }
        if(world != null && pos != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof MetaBlockEntityHolder) {
                MetaBlockEntity mbe = ((MetaBlockEntityHolder) blockEntity).getMetaBlockEntity();
                if(mbe != null) {
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
        Brachydium.LOGGER.info("Creating blockEntity for id " + id);
        ensureBlockEntityNotNull(world, null);
        if(metaBlockEntity == null) {
            throw new NullPointerException("A MetaBlockEntity with id " + id + " doesn't exist");
        }

        metaBlockEntity.reinitializeInventories();
        return new MetaBlockEntityHolder(metaBlockEntity);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        Brachydium.LOGGER.info("BlockEntity placed!");
        ensureBlockEntityNotNull(world, pos);
        if(metaBlockEntity != null && placer != null) {
            Brachydium.LOGGER.info("-- setting front");
            metaBlockEntity.setFrontFacing(placer.getMovementDirection().getOpposite());
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient()) {
            Brachydium.LOGGER.info("Right click BlockEntity");
            if(getMetaBlockEntity(world, pos) != null && metaBlockEntity.hasUi()) {
                metaBlockEntity.open((ServerPlayerEntity) player, metaBlockEntity);
            }
            /*NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if(screenHandlerFactory != null) {
                Brachydium.LOGGER.info("-- opening HandledScreen");
                 player.openHandledScreen(screenHandlerFactory);
            } else {
                System.out.println("ScreenHandler Factory is null");
            }*/
        }
        return ActionResult.SUCCESS;
    }

    /*@Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        Brachydium.LOGGER.info("Creating screenHandlerFactory");
        ensureBlockEntityNotNull(world, pos);
        if(metaBlockEntity != null && metaBlockEntity.getHolder() != null) return metaBlockEntity.getHolder();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory) blockEntity : null;
    }*/

}
