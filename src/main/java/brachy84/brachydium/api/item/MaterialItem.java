package brachy84.brachydium.api.item;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MaterialItem extends BrachydiumItem implements IMaterialItem {

    public static MaterialItem createAndRegister(@NotNull TagDictionary.Entry tag, @NotNull Material material) {
        Identifier id = Brachydium.id(String.format("material/%s.%s", tag.lowerCaseName, material));
        Item.Settings settings = new FabricItemSettings().group(ItemGroups.MATERIALS)
                .maxCount(tag.maxStackSize);

        MaterialItem item = Registry.register(Registry.ITEM, id, new MaterialItem(tag, material, settings));
        RRPHelper.addSimpleMaterialItemTag(material, tag);
        RRPHelper.addBasicMaterialItemModel(material, tag, true);
        ColorProviderRegistry.ITEM.register((stack, layer) -> tag.materialIconType != null ? tag.materialIconType.defaultColorProvider.getColor(layer, material) : layer == 0 ? material.getMaterialRGB() : -1, item);
        return item;
    }

    public static Identifier createItemId(Material material, TagDictionary.Entry tag) {
        return Brachydium.id(String.format("material/%s.%s", tag.lowerCaseName, material.toString()));
    }

    public static String getTexturePath(Material material, TagDictionary.Entry tag) {
        return "brachydium:item/material_sets/" + material.getMaterialIconSet().name + "/" + tag.lowerCaseName;
    }

    private final TagDictionary.Entry tag;
    private final Material material;

    public MaterialItem(TagDictionary.Entry tag, Material material, Item.Settings settings) {
        super(createItemId(material, tag), settings);
        this.tag = tag;
        this.material = material;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (tag.heatDamage != 0 && selected) {
            DamageSource source;
            float dmg;
            if (tag.heatDamage > 0) {
                source = DamageSource.ON_FIRE;
                dmg = tag.heatDamage;
            } else {
                source = DamageSource.FREEZE;
                dmg = -tag.heatDamage;
            }
            entity.damage(source, dmg);
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        return new TranslatableText(getTranslationKey(), material.getLocalizedName());
    }

    @Override
    public String getTranslationKey() {
        return "component." + tag.lowerCaseName;
    }

    public TagDictionary.Entry getComponent() {
        return tag;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public Material getMaterial(ItemStack stack) {
        return material;
    }

    @Override
    public void writeMaterial(Material material, ItemStack stack) {
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new LiteralText(material.getChemicalFormula()).formatted(Formatting.YELLOW));
        String mod = material.getModName();
        if (mod != null && !mod.equals(Brachydium.NAME)) {
            tooltip.add(new LiteralText(mod).formatted(Formatting.GREEN));
        }
    }
}
