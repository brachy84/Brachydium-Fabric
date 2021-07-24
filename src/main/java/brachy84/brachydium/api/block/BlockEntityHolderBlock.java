package brachy84.brachydium.api.block;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.blockEntity.TileEntityFactory;
import brachy84.brachydium.api.blockEntity.TileEntityGroup;
import brachy84.brachydium.api.cover.Cover;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockEntityHolderBlock extends Block implements BlockEntityProvider {

    private final TileEntityGroup<?> group;

    public BlockEntityHolderBlock(TileEntityGroup<?> group) {
        super(FabricBlockSettings.of(Material.METAL).strength(3, 3));
        this.group = Objects.requireNonNull(group);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ((world1, pos, state1, blockEntity) -> {
            TileEntity tile = TileEntity.getOf(blockEntity);
            if (tile != null && tile.isTicking())
                tile.tick();
        });
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        for (TileEntityFactory<?> tile : this.group.getTileEntities()) {
            list.add(tile.getOriginal().asStack());
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        Brachydium.LOGGER.info("Creating blockEntity");
        return new BlockEntityHolder(group, pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        Brachydium.LOGGER.info("BlockEntity placed!");
        //TODO: test this on server. If crashes, insert !isClient and sync facing
        if (stack.getItem() instanceof BlockMachineItem && stack.hasNbt()) {
            TileEntityFactory<?> tile = group.getBlockEntity(stack.getNbt());
            if (tile != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof BlockEntityHolder) {
                    ((BlockEntityHolder) blockEntity).setActiveTileEntity(tile, placer.getHorizontalFacing().getOpposite());
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        TileEntity tile = TileEntity.getOf(world, pos);
        if (tile != null) {
            return tile.onUse(state, world, pos, player, hand, hit);
        }
        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            TileEntity tile = TileEntity.getOf(world, pos);
            if (tile != null) {
                tile.onDetach();
                for (Direction direction : Direction.values()) {
                    Cover cover = tile.getCover(direction);
                    if (cover != null) {
                        tile.removeCover(direction);
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), cover.asStack(1));
                    }
                }
            }
        }
    }
}
