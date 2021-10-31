package brachy84.brachydium.api.unification.ore;

import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.material.properties.PropertyKey;
import brachy84.brachydium.api.util.BrachydiumRegistry;
import com.google.common.base.Preconditions;
import com.google.common.collect.Comparators;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * For ore generation
 */
public class StoneType implements Comparable<StoneType> {

    public static final int AFFECTED_BY_GRAVITY = 1;
    public static final int UNBREAKABLE = 1 << 1;

    public final String name;
    public final String harvestTool;
    public final Identifier backgroundSideTexture;
    public final Identifier backgroundTopTexture;

    public final TagDictionary.Entry processingPrefix;
    public final Material stoneMaterial;
    public final Supplier<BlockState> stone;
    public net.minecraft.block.Material soundType;
    //we are using guava predicate because isReplaceableOreGen uses it
    @SuppressWarnings("Guava")
    private final com.google.common.base.Predicate<BlockState> predicate;
    public final boolean unbreakable;
    public final boolean affectedByGravity;

    public static final BrachydiumRegistry<String, StoneType> STONE_TYPE_REGISTRY = new BrachydiumRegistry<>();

    public StoneType(String name, Identifier backgroundSideTexture, Identifier backgroundTopTexture, /*SoundType soundType, */TagDictionary.Entry processingPrefix, Material stoneMaterial, String harvestTool, int flags, Supplier<BlockState> stone, Predicate<BlockState> predicate) {
        Preconditions.checkArgument(
                stoneMaterial.hasProperty(PropertyKey.DUST),
                "Stone type must be made with a Material with the Dust Property!"
        );
        this.name = name;
        this.backgroundSideTexture = backgroundSideTexture;
        this.backgroundTopTexture = backgroundTopTexture;
        //this.soundType = soundType;
        this.processingPrefix = processingPrefix;
        this.stoneMaterial = stoneMaterial;
        this.harvestTool = harvestTool;
        this.unbreakable = (flags & UNBREAKABLE) > 0;
        this.affectedByGravity = (flags & AFFECTED_BY_GRAVITY) > 0;
        this.stone = stone;
        this.predicate = predicate::test;
        STONE_TYPE_REGISTRY.register(name, this);
    }

    public StoneType(String name, Identifier backgroundTexture, /*SoundType soundType, */TagDictionary.Entry processingPrefix, Material stoneMaterial, String harvestTool, int flags, Supplier<BlockState> stone, Predicate<BlockState> predicate) {
        this(name, backgroundTexture, backgroundTexture, /*soundType, */processingPrefix, stoneMaterial, harvestTool, flags, stone, predicate);
    }

    @Override
    public int compareTo(@NotNull StoneType stoneType) {
        return Integer.compare(name.hashCode(), stoneType.name.hashCode());
        //return STONE_TYPE_REGISTRY.getIDForObject(this) - STONE_TYPE_REGISTRY.getIDForObject(stoneType);
    }

    public static void init() {
        //noinspection ResultOfMethodCallIgnored
        //StoneTypes.STONE.name.getBytes();
    }

    public static StoneType computeStoneType(BlockState blockState, BlockView world, BlockPos blockPos) {
        //TODO ADD CONFIG HOOK HERE FOR MATCHING BLOCKS WITH STONE TYPES
        /*for (StoneType stoneType : STONE_TYPE_REGISTRY) {
            if (blockState.getBlock().(blockState, world, blockPos, stoneType.predicate))
                return stoneType;
        }*/
        return null;
    }

}
