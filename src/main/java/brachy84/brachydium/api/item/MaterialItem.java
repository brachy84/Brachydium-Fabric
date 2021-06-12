package brachy84.brachydium.api.item;

import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.api.tag.TagDictionary;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MaterialItem extends Item {

    private TagDictionary.Entry component;
    private Material material;
    private String translationKey;

    private MaterialItem(Settings settings) {
        super(settings);
    }

    public MaterialItem(TagDictionary.Entry component, Material material) {
        super(new Settings().group(ItemGroups.MATERIALS));
        this.component = component;
        this.material = material;
    }

    public Identifier makeId() {
        return Brachydium.id(String.format("material.%s_%s", component.getName(), material.getRegistryName()));
    }

    @Override
    public Text getName(ItemStack stack) {
        return new TranslatableText(getTranslationKey(), material.getLocalizedName());
    }

    @Override
    public String getTranslationKey() {
        return "component." + component.getName();
    }

    public TagDictionary.Entry getComponent() {
        return component;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new LiteralText(material.chemicalFormula).formatted(Formatting.DARK_GRAY));
    }
}
