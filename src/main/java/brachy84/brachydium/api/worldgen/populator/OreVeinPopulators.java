package brachy84.brachydium.api.worldgen.populator;

import brachy84.brachydium.Brachydium;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OreVeinPopulators {

    //public static final OreVeinPopulator<FluidSpringPopulatorConfig> FLUID_SPRING;
    public static final OreVeinPopulator<SurfaceBlockPopulatorConfig> SURFACE_BLOCK;

    private static <T extends OreVeinPopulator<?>> T register(String name, T populator) {
        return Registry.register(OreVeinPopulator.REGISTRY, Brachydium.id(name), populator);
    }

    public static void ensureInitialized() {
    }

    static {
        //FLUID_SPRING = register("fluid_spring", new FluidSpringPopulator(FluidSpringPopulatorConfig.CODEC));
        SURFACE_BLOCK = register("surface_block", new SurfaceBlockPopulator(SurfaceBlockPopulatorConfig.CODEC));
    }
}
