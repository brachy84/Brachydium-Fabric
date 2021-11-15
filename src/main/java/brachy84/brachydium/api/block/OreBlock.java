package brachy84.brachydium.api.block;

import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.api.item.MaterialItem;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OreBlock extends MaterialBlock {

    private static final Map<Material, OreBlock> ORES = new HashMap<>();

    @Nullable
    public static OreBlock getOre(Material material) {
        return ORES.get(material);
    }

    public static OreBlock createAndRegister(@NotNull TagDictionary.Entry tag, @NotNull Material material) {
        return createAndRegister(tag, material, settings -> {
        });
    }

    public static OreBlock createAndRegister(@NotNull TagDictionary.Entry tag, @NotNull Material material, Consumer<FabricBlockSettings> blockSettingsConsumer) {
        Identifier id = MaterialItem.createItemId(material, tag);
        FabricBlockSettings blockSettings = FabricBlockSettings.of(net.minecraft.block.Material.METAL)
                .strength(5, 6);
        blockSettingsConsumer.accept(blockSettings);
        blockSettings.breakByTool(FabricToolTags.PICKAXES, material.getHarvestLevel()); // TODO strength from material
        Item.Settings settings = new FabricItemSettings().group(ItemGroups.MATERIALS)
                .maxCount(tag.maxStackSize);

        OreBlock materialBlock = Registry.register(Registry.BLOCK, id, new OreBlock(material, tag, blockSettings));
        MaterialBlockItem item = Registry.register(Registry.ITEM, id, new MaterialBlockItem(materialBlock, settings));

        RRPHelper.addSimpleMaterialItemTag(material, tag);
        RRPHelper.addOreBlockState(material);
        RRPHelper.addOreBlockItemModel(material);
        RRPHelper.addSimpleLootTable(id);
        BlockRenderLayerMap.INSTANCE.putBlock(materialBlock, RenderLayer.getCutout());
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> tintIndex == 1 ? material.getMaterialRGB() : -1, materialBlock);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? material.getMaterialRGB() : -1, item);
        return materialBlock;
    }

    @ApiStatus.Internal
    public static void initVariantState() {
        variant = OreVariant.createWithAll("variant");
    }

    private static OreVariant variant;

    public static OreVariant getVariantProperty() {
        return variant;
    }

    public OreBlock(Material material, TagDictionary.Entry tag, Settings settings) {
        super(material, tag, settings);
        ORES.put(material, this);
        setDefaultState(this.getStateManager().getDefaultState().with(variant, variant.getDefaultVariant()));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(variant);
    }

    public OreVariant getVariant() {
        return variant;
    }
}
