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
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockEntityHolderBlock extends Block implements BlockEntityProvider {

    private final BlockEntityGroup<?> group;

    public BlockEntityHolderBlock(BlockEntityGroup<?> group) {
        super(FabricBlockSettings.of(Material.METAL).strength(3, 3));
        this.group = Objects.requireNonNull(group);
    }

    @Override
    public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> list) {
        for (TileEntity tile : this.group.getTileEntities()) {
            list.add(tile.asStack());
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
        if (stack.getItem() instanceof BlockMachineItem && stack.hasTag()) {
            TileEntity tile = group.getBlockEntity(stack.getTag());
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

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        if (!world.isClient()) {
            if(blockEntity instanceof BlockEntityHolder) {
                 ((BlockEntityHolder) blockEntity).getActiveTileEntity().onDetach();
            }
        }
        super.afterBreak(world, player, pos, state, blockEntity, stack);
    }
}
