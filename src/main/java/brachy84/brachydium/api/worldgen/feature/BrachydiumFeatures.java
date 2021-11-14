package brachy84.brachydium.api.worldgen.feature;

import brachy84.brachydium.Brachydium;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;

public class BrachydiumFeatures {

    public static final Feature<OreVeinFeatureConfig> ORE_VEIN;

    private static <T extends Feature<?>> T register(String name, T feature) {
        return Registry.register(Registry.FEATURE, Brachydium.id(name), feature);
    }

    public static void ensureInitialized() {
        register("ore_vein", ORE_VEIN);
    }

    static {
        ORE_VEIN = new OreVeinFeature(OreVeinFeatureConfig.CODEC);
    }

}
