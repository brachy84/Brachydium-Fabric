package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.item.BrachydiumItem;
import brachy84.brachydium.api.item.tool.ToolItem;
import brachy84.brachydium.api.unification.material.Materials;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Helper class to register vanilla crafting recipes
 * use {@link #shaped(String)} or {@link #shapeless(String)}
 * <p>
 * some chars are reserved, but can be overwritten in each recipe
 * reserved chars:
 * </br> H - hammer tool
 * </br> F - file tool
 * </br> S - saw tool
 * </br> D - screwdriver tool
 * </br> W - wrench tool
 * </br> M - mortar tool
 */
public class CraftingRecipe {

    public static void init() {
        shaped("test")
                .pattern("# #", " # ", "# #")
                .item('#', Items.IRON_INGOT)
                .result(Items.GOLD_INGOT).end();
        shaped("test_nbt")
                .pattern("ab", "ba")
                .tag('a', TagDictionary.ingot.createTagId(Materials.Copper))
                .item('b', "minecraft:nether_star")
                .result(Items.DIAMOND_PICKAXE, data -> data.entry("Damage", 420)).end();
        shapeless("test_shapeless")
                .item("minecraft:stone")
                .tag("coals")
                .item("minecraft:stone", NbtItemBuilder.require(data -> data
                        .group("display", data1 -> data1
                                .group("Name", data2 -> data2
                                        .entry("text", "Neat Custom Name")
                                        .entry("nbtcrafting:stringify", true)))))
                .tag("c:aluminium_ingots")
                .result(Items.NETHER_STAR, 32).end();
    }

    public static Shaped shaped(String name) {
        return new Shaped(name);
    }

    public static Shapeless shapeless(String name) {
        return new Shapeless(name);
    }

    public static Map<Identifier, JsonObject> recipes = new HashMap<>();

    public static class Shaped {

        private final JsonObject recipe = new JsonObject();
        private final JsonObject keys = new JsonObject();
        private final Identifier id;
        private final Map<Character, Integer> keySet = new HashMap<>();
        private int flag = 0;

        private Shaped(String name) {
            this.id = Brachydium.id("recipes/" + name + ".json");
            recipe.addProperty("type", "minecraft:crafting_shaped");
        }

        public Shaped pattern(String row1) {
            return pattern(row1, null, null);
        }

        public Shaped pattern(String row1, @Nullable String row2) {
            return pattern(row1, row2, null);
        }

        public Shaped pattern(String row1, @Nullable String row2, @Nullable String row3) {
            if (!checkKeys(row1, 0))
                throw new IllegalArgumentException("A crafting recipe can have at most 3 ingredients");
            JsonArray pattern = new JsonArray();
            pattern.add(row1);
            if (checkKeys(row2, 1))
                pattern.add(row2);
            if (checkKeys(row3, 3))
                pattern.add(row3);
            recipe.add("pattern", pattern);
            flag |= 1;
            return this;
        }

        public Shaped item(char key, Object value) {
            return item(key, value, null, null, null);
        }

        public Shaped item(char key, Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder) {
            return item(key, value, nbtBuilder, null, null);
        }

        public Shaped item(char key, Object value, @Nullable Object remainder) {
            return item(key, value, null, remainder, null);
        }

        public Shaped item(char key, Object value, @Nullable Object remainder, @Nullable Consumer<NbtItemBuilder> remainderNbtBuilder) {
            return item(key, value, null, remainder, remainderNbtBuilder);
        }

        public Shaped item(char key, Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder, @Nullable Object remainder, @Nullable Consumer<NbtItemBuilder> remainderNbtBuilder) {
            JsonObject keyObject = ingredient("item", value, remainder, remainderNbtBuilder);
            if (nbtBuilder != null) {
                NbtItemBuilder builder = new NbtItemBuilder();
                nbtBuilder.accept(builder);
                keyObject.add("data", builder.getData());
            }
            keys.add(String.valueOf(key), keyObject);
            keySet.remove(key);
            return this;
        }

        public Shaped tag(char key, Object value) {
            return tag(key, value, null, null, null);
        }

        public Shaped tag(char key, Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder) {
            return tag(key, value, nbtBuilder, null, null);
        }

        public Shaped tag(char key, Object value, @Nullable Object remainder) {
            return tag(key, value, null, remainder, null);
        }

        public Shaped tag(char key, Object value, @Nullable Object remainder, @Nullable Consumer<NbtItemBuilder> remainderNbtBuilder) {
            return tag(key, value, null, remainder, remainderNbtBuilder);
        }

        public Shaped tag(char key, Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder, @Nullable Object remainder, @Nullable Consumer<NbtItemBuilder> remainderNbtBuilder) {
            JsonObject keyObject = ingredient("tag", value, remainder, remainderNbtBuilder);
            if (nbtBuilder != null) {
                NbtItemBuilder builder = new NbtItemBuilder();
                nbtBuilder.accept(builder);
                keyObject.add("data", builder.getData());
            }
            keys.add(String.valueOf(key), keyObject);
            keySet.remove(key);
            return this;
        }

        public Shaped result(Object value) {
            return result(value, null, 1);
        }

        public Shaped result(Object value, int count) {
            return result(value, null, count);
        }

        public Shaped result(Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder) {
            return result(value, nbtBuilder, 1);
        }

        public Shaped result(Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder, int count) {
            JsonObject result = new JsonObject();
            finalizeResult(result, value, nbtBuilder == null);
            result.addProperty("count", count);
            if (nbtBuilder != null) {
                NbtItemBuilder builder = new NbtItemBuilder();
                nbtBuilder.accept(builder);
                result.add("data", builder.getData());
            }
            recipe.add("result", result);
            flag |= 2;
            return this;
        }

        private boolean checkKeys(String keys, int row) {
            if (keys != null && keys.length() <= 3) {
                for (int i = 0, n = keys.length(); i < n; i++) {
                    int index = n * row + i;
                    keySet.putIfAbsent(keys.charAt(i), index);
                    //keySet.add(keys.charAt(i));
                }
                return true;
            }
            return false;
        }

        public void end() {
            if ((flag & 1) > 0 && (flag & 2) > 0) {
                keySet.remove(' ');
                for (Map.Entry<Character, Integer> c : keySet.entrySet()) {
                    ToolItem tool = ToolItem.getOfRecipeSymbol(c.getKey());
                    if (tool == null) {
                        Brachydium.LOGGER.error("Key {} was not set and could not find ToolItem for recipe {}", c, id);
                        return;
                    }
                    item(c.getKey(), tool, tool, tool.getRemainder(c.getValue()));
                }

                recipe.add("key", keys);
                recipes.put(id, recipe);
                return;
            }
            Brachydium.LOGGER.error("Could not load recipe {} ", id);
        }

        @Override
        public String toString() {
            return recipe.toString();
        }
    }

    public static class Shapeless {

        private final JsonObject recipe = new JsonObject();
        private final JsonArray ingredients = new JsonArray();
        private JsonObject lastIngredient;
        private final Identifier id;
        private int flag = 0;

        private Shapeless(String name) {
            this.id = Brachydium.id("recipes/" + name + ".json");
            recipe.addProperty("type", "minecraft:crafting_shapeless");
        }

        public Shapeless item(Object value) {
            return item(value, null, null, null);
        }

        public Shapeless item(Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder) {
            return item(value, nbtBuilder, null, null);
        }

        public Shapeless item(Object value, @Nullable Object remainder) {
            return item(value, null, remainder, null);
        }

        public Shapeless item(Object value, @Nullable Object remainder, @Nullable Consumer<NbtItemBuilder> remainderNbtBuilder) {
            return item(value, null, remainder, remainderNbtBuilder);
        }

        public Shapeless item(Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder, @Nullable Object remainder, @Nullable Consumer<NbtItemBuilder> remainderNbtBuilder) {
            JsonObject keyObject = ingredient("item", value, remainder, remainderNbtBuilder);
            if (nbtBuilder != null) {
                NbtItemBuilder builder = new NbtItemBuilder();
                nbtBuilder.accept(builder);
                keyObject.add("data", builder.getData());
            }
            ingredients.add(keyObject);
            lastIngredient = keyObject;
            flag |= 1;
            return this;
        }

        public Shapeless tag(Object value) {
            return tag(value, null, null, null);
        }

        public Shapeless tag(Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder) {
            return tag(value, nbtBuilder, null, null);
        }

        public Shapeless tag(Object value, @Nullable Object remainder) {
            return tag(value, null, remainder, null);
        }

        public Shapeless tag(Object value, @Nullable Object remainder, @Nullable Consumer<NbtItemBuilder> remainderNbtBuilder) {
            return tag(value, null, remainder, remainderNbtBuilder);
        }

        public Shapeless tag(Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder, @Nullable Object remainder, @Nullable Consumer<NbtItemBuilder> remainderNbtBuilder) {
            JsonObject keyObject = ingredient("tag", value, remainder, remainderNbtBuilder);
            if (nbtBuilder != null) {
                NbtItemBuilder builder = new NbtItemBuilder();
                nbtBuilder.accept(builder);
                keyObject.add("data", builder.getData());
            }
            ingredients.add(keyObject);
            lastIngredient = keyObject;
            flag |= 1;
            return this;
        }

        public Shapeless repeat(int count) {
            if (lastIngredient == null)
                return this;
            for (int i = 0; i < count; i++) {
                ingredients.add(lastIngredient);
            }
            return this;
        }

        public Shapeless tool(char recipeSymbol) {
            ToolItem tool = ToolItem.getOfRecipeSymbol(recipeSymbol);
            if (tool == null)
                Brachydium.LOGGER.error("Could not find ToolItem for symbol {}, for recipe {}", recipeSymbol, id);
            else
                item(tool, tool, tool.getRemainder(ingredients.size()));
            return this;
        }

        public Shapeless result(Object value) {
            return result(value, null, 1);
        }

        public Shapeless result(Object value, int count) {
            return result(value, null, count);
        }

        public Shapeless result(Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder) {
            return result(value, nbtBuilder, 1);
        }

        public Shapeless result(Object value, @Nullable Consumer<NbtItemBuilder> nbtBuilder, int count) {
            JsonObject result = new JsonObject();
            finalizeResult(result, value, nbtBuilder == null);
            result.addProperty("count", count);
            if (nbtBuilder != null) {
                NbtItemBuilder builder = new NbtItemBuilder();
                nbtBuilder.accept(builder);
                result.add("data", builder.getData());
            }
            recipe.add("result", result);
            flag |= 2;
            return this;
        }

        public void end() {
            if ((flag & 1) > 0 && (flag & 2) > 0) {
                recipe.add("ingredients", ingredients);
                recipes.put(id, recipe);
            } else {
                Brachydium.LOGGER.error("Could not load recipe {} ", id);
            }
        }

        @Override
        public String toString() {
            return recipe.toString();
        }
    }

    private static JsonObject ingredient(String type, Object value, @Nullable Object remainder, @Nullable Consumer<NbtItemBuilder> remainderNbtBuilder) {
        JsonObject keyObject = new JsonObject();
        if (value instanceof ItemStack stack) {
            keyObject.addProperty(type, Registry.ITEM.getId(stack.getItem()).toString());
            if (stack.hasNbt()) {
                JsonObject nbt = new JsonObject();
                writeNbtToJson(stack.getNbt(), nbt);
                keyObject.add("data", nbt);
            }
        } else {
            keyObject.addProperty(type, finalizeIngredient(value));
        }
        if (remainder != null) {
            JsonObject remainderObject = new JsonObject();
            if (value instanceof ItemStack stack) {
                remainderObject.addProperty("item", Registry.ITEM.getId(stack.getItem()).toString());
                if (stack.hasNbt() && remainderNbtBuilder == null) {
                    JsonObject nbt = new JsonObject();
                    writeNbtToJson(stack.getNbt(), nbt);
                    remainderObject.add("data", nbt);
                }
            } else {
                remainderObject.addProperty("item", finalizeIngredient(value));
            }
            if (remainderNbtBuilder != null) {
                NbtItemBuilder builder = new NbtItemBuilder();
                remainderNbtBuilder.accept(builder);
                remainderObject.add("data", builder.getData());
            }
            keyObject.add("remainder", remainderObject);
        }
        return keyObject;
    }

    private static String finalizeIngredient(Object o) {
        if (o instanceof String string)
            return string;
        if (o instanceof Identifier)
            return o.toString();
        if (o instanceof BrachydiumItem item)
            return item.getId().toString();
        if (o instanceof Item item)
            return Registry.ITEM.getId(item).toString();
        if (o instanceof Tag.Identified tag)
            return tag.getId().toString();
        throw new IllegalArgumentException("Object is not a valid recipe ingredient. " + o);
    }

    private static void finalizeResult(JsonObject result, Object o, boolean useStackNbt) {
        String id = null;
        if (o instanceof String string)
            id = string;
        else if (o instanceof Identifier)
            id = o.toString();
        else if (o instanceof BrachydiumItem item)
            id = item.getId().toString();
        else if (o instanceof Item item)
            id = Registry.ITEM.getId(item).toString();
        else if (o instanceof ItemStack stack) {
            id = Registry.ITEM.getId(stack.getItem()).toString();
            result.addProperty("item", id);
            if (stack.hasNbt() && useStackNbt) {
                JsonObject nbt = new JsonObject();
                writeNbtToJson(stack.getNbt(), nbt);
                result.add("data", nbt);
            }
            return;
        }
        if (id != null) {
            result.addProperty("item", id);
            return;
        }
        throw new IllegalArgumentException("Object is not a valid recipe result. " + o);
    }

    public static void writeNbtToJson(NbtCompound nbt, JsonObject json) {
        for (String key : nbt.getKeys()) {
            NbtElement element = nbt.get(key);
            if (element == null)
                continue;
            if (element instanceof AbstractNbtNumber number)
                json.addProperty(key, number.numberValue());
            else if (element instanceof NbtString string)
                json.addProperty(key, string.asString());
            else if (element instanceof NbtCompound compound) {
                JsonObject tag = new JsonObject();
                writeNbtToJson(compound, tag);
                json.add(key, tag);
            } else if (element instanceof AbstractNbtList list) {
                JsonArray array = new JsonArray();
                writeNbtListToJson(list, array);
                json.add(key, array);
            }
        }
    }

    public static void writeNbtListToJson(AbstractNbtList<?> list, JsonArray json) {
        if (list instanceof NbtByteArray array) {
            for (NbtByte nbtByte : array) {
                json.add(nbtByte.byteValue());
            }
        } else if (list instanceof NbtIntArray array) {
            for (NbtInt nbtInt : array) {
                json.add(nbtInt.intValue());
            }
        } else if (list instanceof NbtLongArray array) {
            for (NbtLong nbtInt : array) {
                json.add(nbtInt.longValue());
            }
        } else if (list instanceof NbtList array) {
            switch (array.getHeldType()) {
                case 1 -> {
                    for (NbtElement nbtElement : array) {
                        NbtByte nbt = (NbtByte) nbtElement;
                        json.add(nbt.byteValue());
                    }
                }
                case 2 -> {
                    for (NbtElement nbtElement : array) {
                        NbtShort nbt = (NbtShort) nbtElement;
                        json.add(nbt.shortValue());
                    }
                }
                case 3 -> {
                    for (NbtElement nbtElement : array) {
                        NbtInt nbt = (NbtInt) nbtElement;
                        json.add(nbt.intValue());
                    }
                }
                case 4 -> {
                    for (NbtElement nbtElement : array) {
                        NbtLong nbt = (NbtLong) nbtElement;
                        json.add(nbt.longValue());
                    }
                }
                case 5 -> {
                    for (NbtElement nbtElement : array) {
                        NbtFloat nbt = (NbtFloat) nbtElement;
                        json.add(nbt.floatValue());
                    }
                }
                case 6 -> {
                    for (NbtElement nbtElement : array) {
                        NbtDouble nbt = (NbtDouble) nbtElement;
                        json.add(nbt.doubleValue());
                    }
                }
                case 8 -> {
                    for (NbtElement nbtElement : array) {
                        NbtString nbt = (NbtString) nbtElement;
                        json.add(nbt.asString());
                    }
                }
                case 10 -> {
                    for (NbtElement nbtElement : array) {
                        NbtCompound nbt = (NbtCompound) nbtElement;
                        JsonObject jsonArray = new JsonObject();
                        writeNbtToJson(nbt, jsonArray);
                        json.add(jsonArray);
                    }
                }
                case 7, 9, 11, 12 -> {
                    for (NbtElement nbtElement : array) {
                        AbstractNbtList<?> nbt = (AbstractNbtList<?>) nbtElement;
                        JsonArray jsonArray = new JsonArray();
                        writeNbtListToJson(nbt, jsonArray);
                        json.add(jsonArray);
                    }
                }
            }
        }
    }
}
