package brachy84.brachydium.api.worldgen;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.block.OreBlock;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.material.Materials;
import brachy84.brachydium.api.worldgen.feature.BrachydiumFeatures;
import brachy84.brachydium.api.worldgen.feature.OreVeinFeatureConfig;
import brachy84.brachydium.api.worldgen.populator.ConfiguredOreVeinPopulator;
import brachy84.brachydium.api.worldgen.populator.OreVeinPopulators;
import brachy84.brachydium.api.worldgen.populator.SurfaceBlockPopulatorConfig;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class OreVein {

    public static void init() {
        new Builder("copper")
                .addOre(Materials.Copper, 1)
                .addOre(Materials.Iron, 1)
                .surfaceBlock(Blocks.CAKE.getDefaultState())
                .minSize(12, 2, 12)
                .maxSize(24, 6, 24)
                .spawnY(30, 60)
                .build();
    }

    public static class Builder {
        public int radiusXmin = 4, radiusYmin = 4, radiusZmin = 4, radiusXmax = 4, radiusYmax = 4, radiusZmax = 4, minY = 10, maxY = 50, minForSurfaceStone = 200;
        public float surfaceStoneChance = 0.2f;
        public float density = 0.8f;
        public final Map<BlockState, Integer> ores = new HashMap<>();
        public BlockState surfaceBlock;
        public int surfaceBlockCountMin = 1, surfaceBlockCountMax = 4, surfaceBlockRadiusMin = 6, surfaceBlockRadiusMax = 12;
        public boolean generateSurfaceBlockUnderwater = true;
        public RuleTest test = OreFeatureConfig.Rules.BASE_STONE_OVERWORLD;
        public final String name;
        public Predicate<BiomeSelectionContext> biomeSelector = BiomeSelectors.foundInOverworld();

        public Builder(String name) {
            this.name = name;
        }

        public Builder surfaceBlock(BlockState state) {
            this.surfaceBlock = state;
            return this;
        }

        public Builder surfaceBlockCount(int min, int max) {
            this.surfaceBlockCountMax = max;
            this.surfaceBlockCountMin = min;
            return this;
        }

        public Builder surfaceBlockRadius(int min, int max) {
            this.surfaceBlockRadiusMax = max;
            this.surfaceBlockRadiusMin = min;
            return this;
        }

        public Builder surfaceSpawn(float chance, int requiredAmount) {
            this.surfaceStoneChance = chance;
            this.minForSurfaceStone = requiredAmount;
            return this;
        }

        public Builder generateSurfaceBlockUnderwater(boolean b) {
            this.generateSurfaceBlockUnderwater = b;
            return this;
        }

        public Builder addOre(Material material, int weight) {
            return addOre(OreBlock.getOre(material).getDefaultState(), weight);
        }

        public Builder addOre(BlockState state, int weight) {
            ores.put(state, weight);
            return this;
        }

        public Builder density(float density) {
            this.density = density;
            return this;
        }

        public Builder stateTest(RuleTest test) {
            this.test = test;
            return this;
        }

        public Builder spawnY(int min, int max) {
            this.minY = min;
            this.maxY = max;
            return this;
        }

        public Builder minSize(int x, int y, int z) {
            this.radiusXmin = x;
            this.radiusYmin = y;
            this.radiusZmin = z;
            return this;
        }

        public Builder maxSize(int x, int y, int z) {
            this.radiusXmax = x;
            this.radiusYmax = y;
            this.radiusZmax = z;
            return this;
        }

        public Builder biomeSelector(Predicate<BiomeSelectionContext> biomeSelector) {
            this.biomeSelector = biomeSelector;
            return this;
        }

        public ConfiguredFeature<?, ?> build() {
            ConfiguredOreVeinPopulator<?> populator = surfaceBlock == null ? null : OreVeinPopulators.SURFACE_BLOCK.configure(
                    new SurfaceBlockPopulatorConfig(
                            surfaceBlock,
                            UniformIntProvider.create(surfaceBlockCountMin, surfaceBlockCountMax),
                            generateSurfaceBlockUnderwater,
                            UniformIntProvider.create(surfaceBlockRadiusMin, surfaceBlockRadiusMax)));
            DataPool.Builder<OreVeinFeatureConfig.TargetGroup> builder = DataPool.builder();
            for (Map.Entry<BlockState, Integer> entry : ores.entrySet()) {
                builder.add(new OreVeinFeatureConfig.TargetGroup(List.of(new OreVeinFeatureConfig.Target(test, entry.getKey()))), entry.getValue());
            }

            ConfiguredFeature<?, ?> feature = BrachydiumFeatures.ORE_VEIN.configure(new OreVeinFeatureConfig(
                    UniformIntProvider.create(radiusXmin, radiusXmax),
                    UniformIntProvider.create(radiusXmin, radiusXmax),
                    UniformIntProvider.create(radiusXmin, radiusXmax),
                    List.of(new OreVeinFeatureConfig.GenerationLayer(builder.build(), 0f, 1f, density, 1f, 1f)),
                    populator,
                    minForSurfaceStone,
                    surfaceStoneChance
            )).decorate(new ConfiguredDecorator<>(Decorator.RANGE, new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.aboveBottom(minY), YOffset.fixed(maxY))
            )));

            RegistryKey<ConfiguredFeature<?, ?>> featureKey = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY,
                    Brachydium.id(name));
            Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, featureKey.getValue(), feature);
            BiomeModifications.addFeature(biomeSelector, GenerationStep.Feature.UNDERGROUND_ORES, featureKey);

            return feature;
        }
    }
}