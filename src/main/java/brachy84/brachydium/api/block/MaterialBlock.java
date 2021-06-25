package brachy84.brachydium.api.block;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.api.tag.TagDictionary;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

/**
 *  A block from a material
 *  Is not necessarily a Block like Iron Block, but also frames, casings etc..
 */
public class MaterialBlock extends Block {

    private Material material;
    private TagDictionary.Entry tag;

    private MaterialBlock(Settings settings) {
        super(settings);
    }

    public MaterialBlock(Material material, TagDictionary.Entry tag) {
        super(FabricBlockSettings.of(net.minecraft.block.Material.METAL));
        this.material = material;
        this.tag = tag;
    }

    public Identifier makeId() {
        return Brachydium.id(String.format("material/%s.%s", tag.getName(), material.getRegistryName()));
    }

    @Override
    public String getTranslationKey() {
        return "component." + tag.getName();
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
