package brachy84.brachydium.api.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class TileEntity {

    private final Identifier id;
    private final List<TileTrait> traits = new ArrayList<>();
    private Direction frontFacing;

    protected TileEntity(@NotNull Identifier id) {
        this.id = id;
    }

    public void addTrait(TileTrait trait) {
        Objects.requireNonNull(trait);
        traits.add(trait);
    }

    @Nullable
    public TileTrait findTrait(String name) {
        for (TileTrait trait : traits) {
            if (trait.getName().equals(name)) {
                return trait;
            }
        }
        return null;
    }

    @Nullable
    public TileTrait getTrait(Class<?> clazz) {
        for (TileTrait trait : traits) {
            if (clazz.isAssignableFrom(trait.getClass())) {
                return trait;
            }
        }
        return null;
    }

    public void onAttach() {
    }

    public void onDetach() {
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    public abstract CompoundTag serializeTag();

    public abstract void deserializeTag(CompoundTag tag);

    @NotNull
    public abstract BlockEntityHolder getHolder();

    @Nullable
    public World getWorld() {
        return getHolder().getWorld();
    }

    public boolean isClient() {
        if (getWorld() == null)
            return true;
        else
            return getWorld().isClient();
    }

    public Direction getFrontFacing() {
        if (frontFacing == null) setFrontFacing(Direction.NORTH);
        return frontFacing;
    }

    public void setFrontFacing(Direction frontFacing) {
        this.frontFacing = frontFacing;
    }

    public Identifier getId() {
        return id;
    }
}
