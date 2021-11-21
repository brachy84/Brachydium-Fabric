package brachy84.brachydium.api.block;

import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.blockEntity.TileEntityGroup;
import brachy84.brachydium.api.cover.Cover;
import brachy84.brachydium.api.item.BrachydiumItems;
import brachy84.brachydium.api.render.ICustomOutlineRender;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class BlockEntityHolderBlock extends Block implements BlockEntityProvider, ICustomOutlineRender {

    private static final Map<Direction, List<Edge>> EDGE_MAP = new EnumMap<>(Direction.class);

    static {
        // edges when the direction is up
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge().start(0.25, 1, 0).end(0.25, 1, 1));
        edges.add(new Edge().start(0.75, 1, 0).end(0.75, 1, 1));
        edges.add(new Edge().start(0, 1, 0.25).end(1, 1, 0.25));
        edges.add(new Edge().start(0, 1, 0.75).end(1, 1, 0.75));
        for (Direction direction : Direction.values()) {
            EDGE_MAP.put(direction, edges.stream().map(edge -> edge.copyAndTransform(Direction.UP, direction)).collect(Collectors.toList()));
        }
    }

    private final TileEntityGroup group;

    public BlockEntityHolderBlock(TileEntityGroup group) {
        super(FabricBlockSettings.of(Material.METAL).strength(3, 3));
        this.group = Objects.requireNonNull(group);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ((world1, pos, state1, blockEntity) -> {
            ((BlockEntityHolder) blockEntity).tick();
        });
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        for (TileEntity tile : this.group.getTileEntities()) {
            list.add(tile.asStack());
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityHolder(group, pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        if (world.isClient)
            return;
        if (stack.getItem() instanceof BlockMachineItem && stack.hasNbt()) {
            TileEntity tile = group.getBlockEntity(stack.getNbt());
            if (tile != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof BlockEntityHolder holder) {
                    holder.setActiveTileEntity(tile, placer.getHorizontalFacing().getOpposite());
                    holder.syncPlaceData();
                }
            }
        }
        // BlockView.raycast()
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
        TileEntity tile = null;
        if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            tile = TileEntity.getOf(world, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
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

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        TileEntity tile = TileEntity.getOf(world, pos);
        if (tile != null) {
            return tile.asStack();
        }
        return group.getFallbackTile().asStack();
    }

    @Override
    public boolean renderOutline(MatrixStack matrices, VertexConsumer vertexConsumer, double camX, double camY, double camZ, BlockState state, BlockPos pos) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if (stack.isEmpty() || stack.getItem() != BrachydiumItems.wrench)
            stack = player.getStackInHand(Hand.OFF_HAND);
        if (stack.isEmpty() || stack.getItem() != BrachydiumItems.wrench)
            return true;

        BlockHitResult hitResult = (BlockHitResult) MinecraftClient.getInstance().crosshairTarget;
        Direction side = hitResult.getSide();

        drawEdges(EDGE_MAP.get(side), matrices, vertexConsumer, camX, camY, camZ, pos);

        return true;
    }
}
