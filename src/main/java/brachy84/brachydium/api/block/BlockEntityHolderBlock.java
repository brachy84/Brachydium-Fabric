package brachy84.brachydium.api.block;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.blockEntity.BlockEntityGroup;
import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockEntityHolderBlock extends Block implements BlockEntityProvider {

    private final BlockEntityGroup<?> group;

    public BlockEntityHolderBlock(BlockEntityGroup<?> group) {
        super(FabricBlockSettings.of(Material.METAL).strength(3, 3));
        this.group = group;
    }

    @Nullable
    public TileEntity getTileEntity(CompoundTag tag) {
        TileEntity tile = null;
        BlockEntityGroup<?> group = BrachydiumApi.BLOCK_ENTITY_GROUP_REGISTRY.tryGetEntry(new Identifier(tag.getString("ID")));
        if (group != null) {
            tile = group.getBlockEntity(tag);
        }
        return tile;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        Brachydium.LOGGER.info("Creating blockEntity");
        return new BlockEntityHolder(group);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        Brachydium.LOGGER.info("BlockEntity placed!");
        //TODO: test this on server. If crashes, insert !isClient and sync facing
        if (stack.getItem() instanceof BlockMachineItem && stack.hasTag()) {
            TileEntity tile = getTileEntity(stack.getTag());
            if (tile != null) {
                if (placer != null) tile.setFrontFacing(placer.getHorizontalFacing().getOpposite());
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if(blockEntity instanceof BlockEntityHolder) {
                    ((BlockEntityHolder) blockEntity).setActiveTileEntity(tile);
                }
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof BlockEntityHolder) {
                return ((BlockEntityHolder) blockEntity).getActiveTileEntity().onUse(state, world, pos, player, hand, hit);
            }
        }
        return ActionResult.PASS;
    }
}
