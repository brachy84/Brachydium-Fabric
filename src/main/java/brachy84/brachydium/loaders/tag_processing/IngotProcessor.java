package brachy84.brachydium.loaders.tag_processing;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.item.MaterialItem;
import brachy84.brachydium.api.resource.CraftingRecipe;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.material.properties.PropertyKey;
import brachy84.brachydium.api.unification.ore.TagDictionary;

public class IngotProcessor {

    public static void init() {
        TagDictionary.ingot.setDefaultProcessingHandler(IngotProcessor::processIngot);
        TagDictionary.plate.setDefaultProcessingHandler(IngotProcessor::processPlate);
    }

    private static void processIngot(TagDictionary.Entry tag, Material material) {
        CraftingRecipe.shapeless("ingot_of_nugget_" + material.toString())
                .tag(TagDictionary.nugget.createTagId(material))
                .repeat(8)
                .result(Brachydium.id(MaterialItem.createItemId(material, tag))).end();

        CraftingRecipe.shapeless("nugget_of_ingot_" + material)
                .tag(tag.createTagId(material))
                .result(Brachydium.id(MaterialItem.createItemId(material, TagDictionary.nugget)), 9).end();
    }

    private static void processPlate(TagDictionary.Entry tag, Material material) {
        if(material.hasProperty(PropertyKey.INGOT)) {
            CraftingRecipe.Shaped recipe = CraftingRecipe.shaped("plate_" + material)
                    .pattern("H", "I", "I")
                    .tag('I', TagDictionary.ingot.createTagId(material))
                    .result(Brachydium.id(MaterialItem.createItemId(material, tag)));
            recipe.end();
        }
    }
}
