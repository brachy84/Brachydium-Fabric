package brachy84.brachydium.api.block;

import brachy84.brachydium.api.blockEntity.SurfaceStoneBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SurfaceStoneBlock extends Block implements BlockEntityProvider {

    public static final VoxelShape SHAPE = VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 3 / 16D, 0.75);

    public SurfaceStoneBlock() {
        super(FabricBlockSettings.of(Material.STONE).breakByHand(true).strength(2f, 2f));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SurfaceStoneBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        SurfaceStoneBlockEntity be = (SurfaceStoneBlockEntity) world.getBlockEntity(pos);
        if (be != null)
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), be.getDrop());
        super.onBreak(world, pos, state, player);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
