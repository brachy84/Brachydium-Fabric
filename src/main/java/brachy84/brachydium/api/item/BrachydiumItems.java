package brachy84.brachydium.api.item;

import brachy84.brachydium.api.item.tool.ToolItem;
import brachy84.brachydium.api.resource.CraftingRecipe;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.material.properties.PropertyKey;
import brachy84.brachydium.api.unification.ore.TagDictionary;

public class BrachydiumItems {

    public static ToolItem hammer;
    public static ToolItem file;
    public static ToolItem saw;
    public static ToolItem screwDriver;
    public static ToolItem wrench;
    public static ToolItem mortar;

    private static String getPreferredItemId(Material material, TagDictionary.Entry tag1) {
        TagDictionary.Entry tag = material.hasProperty(PropertyKey.INGOT) ? tag1 : TagDictionary.gem;
        return tag.createTagId(material).toString();
    }

    public static void init() {
        hammer = ToolItem.create("hammer")
                .setRecipeSymbol('H')
                .setCraftingRecipe(material -> CraftingRecipe.shaped("tool/hammer." + material.toString())
                        .pattern("III", "III")
                        .tag('I', getPreferredItemId(material, TagDictionary.ingot))
                        .result(hammer, hammer.getResultData(material)).end())
                .setTextures("handle", 1);
        file = ToolItem.create("file")
                .setRecipeSymbol('F').setCraftingRecipe(material -> CraftingRecipe.shaped("tool/file." + material.toString())
                        .pattern("P", "P")
                        .tag('P', getPreferredItemId(material, TagDictionary.plate))
                        .result(file, file.getResultData(material)).end())
                .setTextures("handle_file", 1);
        saw = ToolItem.create("saw")
                .setRecipeSymbol('S')
                .setTextures("handle_saw", 1);
        screwDriver = ToolItem.create("screwdriver")
                .setRecipeSymbol('D')
                .setTextures("handle_screwdriver", 1);
        wrench = ToolItem.create("wrench")
                .setRecipeSymbol('W')
                .setTextures(null, 0);
        mortar = ToolItem.create("mortar")
                .setRecipeSymbol('M')
                .setTextures("mortar_base", 1);
    }
}
