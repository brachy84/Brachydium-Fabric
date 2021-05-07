package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class CraftingRecipeBuilder {

    private String recipe = "{\n";

    Identifier id;

    public CraftingRecipeBuilder(String item) {
        this.id = Brachydium.id("recipes/" + item + ".json");
    }

    /**
     * @param type shaped or shapeless
     * @return this
     */
    public CraftingRecipeBuilder type(String type) {
        recipe += "  \"type\" : \"minecraft:crafting_" + type + "\",\n";
        return this;
    }

    public CraftingRecipeBuilder group(String group) {
        recipe += "  \"group\" : \"" + group + "\",\n";
        return this;
    }

    public CraftingRecipeBuilder pattern(String... pattern){
        recipe += "  \"pattern\": [\n";
        for(int i = 0; i < Math.min(pattern.length, 3); i++) {
            recipe += "    \"" + pattern[i] + "\"";
            if(i < pattern.length - 1) {
                recipe += ",";
            }
            recipe += "\n";
        }
        recipe +=  "  ],";
        return this;
    }

    public CraftingRecipeBuilder keys(RecipeItem... items) {
        appendComma();
        recipe += "  \"key\": {";
        for(RecipeItem item : items) {
            appendComma();
            recipe += "    \"" + item.key + "\": {";
            if(item.isTag) {
                recipe += "      \"tag\": \"" + item.item + "\"";
            } else {
                if(item.itemBuilder != null) {
                    recipe += "      " + item.itemBuilder.apply(new NbtItemBuilder(item.item)).end();
                } else {
                    recipe += "      \"item\": \"" + item.item + "\"";
                }
            }
            recipe += "    }";
        }
        recipe += "  }";
        return this;
    }

    /*
    public CraftingRecipeBuilder item(String key, String item, Function<NbtItemBuilder, NbtItemBuilder> builder){
        appendComma();
        recipe += "  \"key\": {\n";
        recipe += "    \"" + key + "\": {\n";
        recipe += "     " + builder.apply(new NbtItemBuilder(item)).end();
        recipe += "   }";
        return this;
    }

    public CraftingRecipeBuilder item(String key, String item){
        appendComma();
        recipe += "  \"key\": {\n";
        recipe += "    \"" + key + "\": {\n";
        recipe += "      \"item\": \"" + item + "\"\n";
        recipe += "   }";
        return this;
    }*/

    public CraftingRecipeBuilder result(String item, Function<NbtItemBuilder, NbtItemBuilder> builder){
        appendComma();
        recipe += "  \"result\": {";
        recipe += " " + builder.apply(new NbtItemBuilder(item)).end();
        recipe += "   }";
        return this;
    }

    public CraftingRecipeBuilder result(String item, int count){
        appendComma();
        recipe += "  \"result\": {\n";
        recipe += "     \"item\": \"" + item + "\",\n";
        recipe += "     \"count\": " + count + "\n";
        recipe += "   }";
        return this;
    }

    public CraftingRecipeBuilder result(String item){
        appendComma();
        recipe += "  \"result\": {\n";
        recipe += "     \"item\": \"" + item + "\"\n";
        recipe += "   }";
        return this;
    }

    private void appendComma() {
        String lastChar = Character.toString(recipe.charAt(recipe.length() - 1));
        if(",".equals(lastChar) || "{".equals(lastChar) || "[".equals(lastChar) || System.getProperty("line.separator").equals(lastChar)) {
            return;
        }
        recipe += ",";
    }

    protected String end() {
        return recipe += "\n}";
    }
}
