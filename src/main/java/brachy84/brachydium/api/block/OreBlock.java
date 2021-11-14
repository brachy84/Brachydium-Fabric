package brachy84.brachydium.api.block;

import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.ore.TagDictionary;

public class OreBlock extends MaterialBlock {

    public OreBlock(Material material, TagDictionary.Entry tag, Settings settings) {
        super(material, tag, settings);
    }
}
