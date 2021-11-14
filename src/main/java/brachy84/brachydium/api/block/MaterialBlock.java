package brachy84.brachydium.api.block;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.api.item.MaterialItem;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A block from a material
 * Is not necessarily a Block like Iron Block, but also frames, casings etc..
 */
public class MaterialBlock extends Block {

    public static MaterialBlock createAndRegister(@NotNull TagDictionary.Entry tag, @NotNull Material material) {
        return createAndRegister(tag, material, settings -> {});
    }

    public static MaterialBlock createAndRegister(@NotNull TagDictionary.Entry tag, @NotNull Material material, Consumer<FabricBlockSettings> blockSettingsConsumer) {
        Identifier id = MaterialItem.createItemId(material, tag);
        FabricBlockSettings blockSettings = FabricBlockSettings.of(net.minecraft.block.Material.METAL)
                .strength(5, 6);
        blockSettingsConsumer.accept(blockSettings);
        blockSettings.breakByTool(FabricToolTags.PICKAXES, material.getHarvestLevel()); // TODO strength from material
        Item.Settings settings = new FabricItemSettings().group(ItemGroups.MATERIALS)
                .maxCount(tag.maxStackSize);

        MaterialBlock materialBlock = Registry.register(Registry.BLOCK, id, new MaterialBlock(material, tag, blockSettings));
        MaterialBlockItem item = Registry.register(Registry.ITEM, id, new MaterialBlockItem(materialBlock, settings));

        RRPHelper.addSimpleMaterialItemTag(material, tag);
        RRPHelper.addBasicMaterialBlockState(material, tag);
        RRPHelper.addBasicMaterialBlockItemModel(material, tag);
        RRPHelper.addSimpleLootTable(id);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> material.getMaterialRGB(), materialBlock);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> material.getMaterialRGB(), item);
        return materialBlock;
    }

    private final Material material;
    private final TagDictionary.Entry tag;

    public MaterialBlock(Material material, TagDictionary.Entry tag, Settings settings) {
        super(settings);
        this.material = material;
        this.tag = tag;
    }

    @Override
    public String getTranslationKey() {
        return "component." + tag.name();
    }

    @Override
    public MutableText getName() {
        return new TranslatableText(getTranslationKey(), material.getLocalizedName());
    }

    public Material getMaterial() {
        return material;
    }

    public TagDictionary.Entry getTag() {
        return tag;
    }
}
