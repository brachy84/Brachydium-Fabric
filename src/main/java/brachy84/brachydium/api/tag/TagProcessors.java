package brachy84.brachydium.api.tag;

import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.api.resource.RRPHelper;
import net.devtech.arrp.json.recipe.JIngredient;
import net.devtech.arrp.json.recipe.JIngredients;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.recipe.JResult;
import net.minecraft.item.ItemStack;

import static brachy84.brachydium.Brachydium.*;
import static brachy84.brachydium.api.tag.TagDictionary.*;

public class TagProcessors {

    public static void init() {
        Ingot.addProcessor(TagProcessors::processIngot);
        Ingot.addResourceProvider(TagProcessors::addIngotResources);
        Block.addResourceProvider(TagProcessors::addBlockResources);
    }

    private static void processIngot(Material material, Entry ingotTag) {

    }

    private static void addIngotResources(Material material, Entry ingotTag) {
        ItemStack ingotStack = ingotTag.asStack(material, 9);
        RESOURCE_PACK.addRecipe(id(String.format("%s_ingot_of_block", material.getRegistryName())),
                JRecipe.shapeless(JIngredients.ingredients()
                                .add(JIngredient.ingredient().tag(Block.getStringTag(material)))
                        , JResult.itemStack(ingotStack.getItem(), ingotStack.getCount())
                )
        );
    }

    private static void addBlockResources(Material material, Entry tag) {
        RRPHelper.addBasicMaterialBlockState(material.getRegistryName(), tag.getName());
    }
}
