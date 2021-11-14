package brachy84.brachydium.api.block;

import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class MaterialBlockItem extends BlockItem {

    private final Material material;
    private final TagDictionary.Entry tag;

    public MaterialBlockItem(MaterialBlock block, Settings settings) {
        super(block, settings);
        this.material = block.getMaterial();
        this.tag = block.getTag();
    }

    @Override
    public Text getName(ItemStack stack) {
        return new TranslatableText(getTranslationKey(), material.getLocalizedName());
    }

    @Override
    public String getTranslationKey() {
        return "component." + tag.lowerCaseName;
    }
}
