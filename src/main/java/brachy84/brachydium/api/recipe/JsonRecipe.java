package brachy84.brachydium.api.recipe;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.fluid.FluidStack;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class JsonRecipe {

    public static void loadFromJson(Identifier id, JsonObject json) {
        if(!json.has("table")) {
            Brachydium.LOGGER.error("JsonRecipe does not have required field 'table'");
        }
        String table = json.get("table").getAsString();
        JsonArray inputs = null;
        JsonArray outputs = null;
        int eut = 0;
        int dur = 0;
        if(json.has("inputs")) {
            inputs = json.get("inputs").getAsJsonArray();
        }
        if(json.has("outputs")) {
            outputs = json.get("outputs").getAsJsonArray();
        }
        if(json.has("eut")) {
            eut = json.get("eut").getAsInt();
        }
        if(json.has("duration")) {
            dur = json.get("duration").getAsInt();
        }
        RecipeTable<?> table1 = RecipeTable.getByName(table);
        if(table1 != null) {
            RecipeBuilder<?> builder = table1.recipeBuilder();
            if(inputs != null) processArray(builder, inputs, true);
            if(outputs != null) processArray(builder, outputs, false);
            builder.EUt(eut).duration(dur).buildAndRegister();
        }
    }

    public static void processArray(RecipeBuilder<?> builder, JsonArray array, boolean isInput) {

        for(int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            if(element.isJsonPrimitive()) {
                String[] parts = element.getAsString().split(";");
                if(parts.length < 2) continue;
                int amount;
                String item = parts[1];
                String type = parts[0];
                if(parts.length == 2)
                    amount = 1;
                else
                    amount = Integer.parseInt(parts[2]);
                addInputOutput(builder, type, item, amount, isInput);
            } else if(element.isJsonObject()) {
                JsonObject jsonInput = element.getAsJsonObject();
                int amount;
                String item;
                String type;
                if(jsonInput.has("item")) type = "item";
                else if(isInput && jsonInput.has("tag")) type = "tag";
                else if(jsonInput.has("fluid")) type = "fluid";
                else continue;
                item = jsonInput.get(type).getAsString();
                if(jsonInput.has("count")) {
                    amount = jsonInput.get("count").getAsInt();
                } else {
                    String[] parts = item.split(";");
                    if(parts.length == 1)
                        amount = 1;
                    else {
                        amount = Integer.parseInt(parts[1]);
                        item = parts[0];
                    }
                }
                addInputOutput(builder, type, item, amount, isInput);
            }
        }
    }

    public static void addInputOutput(RecipeBuilder<?> builder, String type, String item, int amount, boolean isInput) {
        Identifier id = new Identifier(item);
        if(type.equals("item")) {
            if(item.split(":").length == 1 && item.startsWith("material")) {
                id = Brachydium.id(item);
            }
            ItemStack stack = new ItemStack(Registry.ITEM.get(id), amount);
            if(isInput)
                builder.input(stack);
            else
                builder.outputs(stack);
        } else if(isInput && type.equals("tag")) {
            if(item.split(":").length == 1)
                id = new Identifier("c", item);
            builder.inputs(RecipeItem.ofTagId(id, amount, 1f));
        } else if(type.equals("fluid")) {
            if(item.split(":").length == 1) {
                //TODO: implement material fluid
            }
            FluidStack stack = new FluidStack(Registry.FLUID.get(id), amount);
            if(isInput)
                builder.fluidInputs(stack);
            else
                builder.fluidOutputs(stack);
        }
    }

    public static ItemStack getStack(String item, String amount) {
        return new ItemStack(Registry.ITEM.get(new Identifier(item)), Integer.parseInt(amount));
    }

    public static FluidStack getFluid(String fluid, String amount) {
        return new FluidStack(Registry.FLUID.get(new Identifier(fluid)), Integer.parseInt(amount));
    }
}
