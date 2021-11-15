package brachy84.brachydium.api.block;

import com.google.common.base.Preconditions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OreVariant extends Property<OreVariant.Variant> {

    @ApiStatus.Internal
    public static void init() {
        registerVariant("stone", "minecraft:block/stone", Blocks.STONE.getDefaultState());
        registerVariant("granite", "minecraft:block/granite", Blocks.GRANITE.getDefaultState());
        registerVariant("diorite", "minecraft:block/diorite", Blocks.DIORITE.getDefaultState());
        registerVariant("andesite", "minecraft:block/andesite", Blocks.ANDESITE.getDefaultState());
    }

    private static final Map<BlockState, Variant> VARIANTS = new HashMap<>();

    public static Collection<Variant> getAll() {
        return Collections.unmodifiableCollection(VARIANTS.values());
    }

    @Nullable
    public static Variant getVariantFor(BlockState state) {
        return VARIANTS.get(state);
    }

    /**
     * Sets the ore state to the
     *
     * @param propertyInstance the property instance of that specific block
     * @param ore              the ore block
     * @param stateToReplace   the block that got replaces
     * @return if the state successfully set
     */
    public static boolean setStateFor(OreVariant propertyInstance, BlockState ore, BlockState stateToReplace) {
        Variant variant = getVariantFor(stateToReplace);
        if (variant == null)
            return false;
        ore.with(propertyInstance, variant);
        return true;
    }

    /**
     * Registers a state vor ore blocks. Note that for each variant, every ore block gains another state.
     *
     * @param uniqueName  the name of the variant
     * @param texturePath the path of the blocks texture
     * @param block       the block of the texture
     * @see #init() for examples
     */
    public static void registerVariant(String uniqueName, String texturePath, BlockState block) {
        Preconditions.checkNotNull(uniqueName, "name for OreVariant can not be null");
        Preconditions.checkNotNull(texturePath, "texturePath for OreVariant can not be null");
        Preconditions.checkNotNull(block, "blockSate for OreVariant can not be null");
        uniqueName = uniqueName.toLowerCase();
        for (Variant variant : VARIANTS.values()) {
            if (uniqueName.equals(variant.name))
                throw new IllegalArgumentException("A OreVariant with the name " + uniqueName + " already exists");
        }
        VARIANTS.put(block, new Variant(uniqueName, texturePath, block));
    }

    private final Map<String, Variant> nameVariants = new HashMap<>();

    public static OreVariant createWithAll(String name) {
        return new OreVariant(name, VARIANTS.values().toArray(new Variant[0]));
    }

    public OreVariant(String name, Variant... variants) {
        super(name, Variant.class);
        for (Variant variant : variants) {
            if (variant == null)
                throw new NullPointerException("Variant can not be null");
            nameVariants.put(variant.name, variant);
        }
    }

    public Variant getDefaultVariant() {
        return nameVariants.values().iterator().next();
    }

    @Override
    public Collection<Variant> getValues() {
        return nameVariants.values();
    }

    @Override
    public String name(Variant value) {
        return value.name;
    }

    @Override
    public Optional<Variant> parse(String name) {
        return Optional.ofNullable(nameVariants.get(name));
    }

    public static final class Variant implements Comparable<Variant> {

        private final String name;
        private final String texturePath;
        private final BlockState block;

        private Variant(String name, String texturePath, BlockState block) {
            this.name = name;
            this.texturePath = texturePath;
            this.block = block;
        }

        public String getName() {
            return name;
        }

        public BlockState getBlock() {
            return block;
        }

        public String getTexturePath() {
            return texturePath;
        }

        @Override
        public int compareTo(@NotNull OreVariant.Variant o) {
            return name.compareTo(o.name);
        }
    }
}
