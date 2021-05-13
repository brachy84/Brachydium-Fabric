package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.blockstate.JWhen;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static brachy84.brachydium.Brachydium.MOD_ID;
import static brachy84.brachydium.Brachydium.RESOURCE_PACK;
import static net.devtech.arrp.json.blockstate.JState.*;
import static net.devtech.arrp.json.loot.JLootTable.*;
import static net.devtech.arrp.json.models.JModel.model;
import static net.devtech.arrp.json.models.JModel.*;
import static net.devtech.arrp.json.tags.JTag.tag;

public class RRPHelper {
    private static Identifier mtId(String string) {
        return Brachydium.id(string);
    }
    // the key is the file name
    // client

    public static final Map<String, byte[]> otherResources = new HashMap<>();

    public static void initOtherResources() {
        for(Map.Entry<String, byte[]> entry : otherResources.entrySet()) {
            //System.out.println("Loading other resources: " + entry.getKey());
            RESOURCE_PACK.addResource(ResourceType.SERVER_DATA, Brachydium.id(entry.getKey()), entry.getValue());
        }
    }

    public static void addNbtRecipe(String id, Function<CraftingRecipeBuilder, CraftingRecipeBuilder> recipeBuilder) {
            String recipe = recipeBuilder.apply(new CraftingRecipeBuilder(id)).end();
            //System.out.println("Adding recipe: \n" + recipe);
            otherResources.put("recipes/" + id + ".json", recipe.getBytes());
    }

    public static void addBasicMaterialItemModel(String material, String component) {
        //itemModels.put("material/" + material + "." + component, model().parent("item/generated").textures(new JTextures().layer0("mechtech:item/component/" + component)));
        RESOURCE_PACK.addModel(model().parent("item/generated").textures(new JTextures().layer0("brachydium:item/component/" + component)), mtId("item/material/" + material + "." + component));
    }
    public static void addBasicMaterialBlockState(String material, String component) {
        //blockStates.put("material/" + material + "." + component, state(JState.variant(JState.model("mechtech:block/component/" + component))));
        RESOURCE_PACK.addBlockState(state(JState.variant(JState.model("brachydium:block/component/" + component))), mtId("material/" + material + "." + component));
    }

    public static void addBasicMaterialBlockItemModel(String material, String component) {
        //itemModels.put("item/material/" + material + "." + component, model().parent("mechtech:block/component/" + component));
        RESOURCE_PACK.addModel(model().parent("brachydium:block/component/" + component), mtId("item/material/" + material + "." + component));
    }

    public static void addSimpleMaterialItemTag(String material, String component) {
        //itemTags.put("c:items/" + material + "_" + component + "s", tag().add(new MTIdentifier("material/" + material + "." + component)));
        RESOURCE_PACK.addTag(new Identifier("c", "items/" + material + "_" + component + "s"), tag().add(mtId("material/" + material + "." + component)));
    }

    public static void addSimpleLootTable(String block) {
        String fullBlock = mtId(block).toString();
        RESOURCE_PACK.addLootTable(mtId("blocks/" + block), loot(fullBlock)
                .pool(pool()
                        .rolls(1)
                        .entry(entry().type("minecraft:item").name(fullBlock))
                ));
    }

    public static void addPipeBlockState(String material, int size) {

        JState state = state()
                .add(multipart(new JBlockModel(mtId("block/cable/cable_core_" + size))))
                .add(multipart(new JBlockModel(mtId("block/cable/cable_side_" + size)).uvlock()).when(new JWhen().add("north", "true")))
                .add(multipart(new JBlockModel(mtId("block/cable/cable_side_" + size)).uvlock().y(90)).when(new JWhen().add("east", "true")))
                .add(multipart(new JBlockModel(mtId("block/cable/cable_side_" + size)).uvlock().y(180)).when(new JWhen().add("south", "true")))
                .add(multipart(new JBlockModel(mtId("block/cable/cable_side_" + size)).uvlock().y(270)).when(new JWhen().add("west", "true")))
                .add(multipart(new JBlockModel(mtId("block/cable/cable_side_" + size)).uvlock().x(270)).when(new JWhen().add("up", "true")))
                .add(multipart(new JBlockModel(mtId("block/cable/cable_side_" + size)).uvlock().x(90)).when(new JWhen().add("down", "true")));
        RESOURCE_PACK.addBlockState(state, mtId("cable/" + material + "." + size));

        JModel itemModel = model().parent(mtId("block/cable/cable_core_" + size).toString());
        RESOURCE_PACK.addModel(itemModel, mtId("item/cable/" + material + "." + size));
    }

    public static void addPipeModel(int size) {
        JModel model = model().parent(mtId("block/cable/base_core_" + size).toString()).textures(textures().var("all", mtId("block/cable/wire").toString()));
        System.out.println(model.toString());
        RESOURCE_PACK.addModel(model, mtId("block/cable/cable_core_" + size));
        RESOURCE_PACK.addModel(model().parent(mtId("block/cable/base_side_" + size).toString()).textures(textures().var("all", mtId("block/cable/wire").toString())), mtId("block/cable/cable_side_" + size));
    }

    public static void addPipeModelTemplate(int size) {
        String texture = "all";
        float from = 8 - size / 2f;
        float to = 8 + size / 2f;
        JModel core = model().textures(
                textures().particle("#" + texture) // rrp doesn't add # to particle texture for some reason
        ).element(
                element().from(from, from, from).to(to, to, to)
                .faces(faces()
                        .up(face(texture).tintIndex(1).uv(from, from, to, to))
                        .down(face(texture).tintIndex(1).uv(from, from, to, to))
                        .north(face(texture).tintIndex(1).uv(from, from, to, to))
                        .south(face(texture).tintIndex(1).uv(from, from, to, to))
                        .east(face(texture).tintIndex(1).uv(from, from, to, to))
                        .west(face(texture).tintIndex(1).uv(from, from, to, to))
                ).shade()
        );
        JModel side = model().textures(textures().particle(texture)).element(
                element().from(from, from, 0).to(to, to, from)
                .faces(faces()
                        .up(face(texture).tintIndex(1).uv(from, 0, to, from))
                        .down(face(texture).tintIndex(1).uv(from, 0, to, from))
                        .north(face(texture).tintIndex(1).uv(from, from, to, to).cullface(Direction.SOUTH))
                        .south(face(texture).tintIndex(1).uv(from, from, to, to))
                        .east(face(texture).tintIndex(1).uv(from, 0, to, from).rot270())
                        .west(face(texture).tintIndex(1).uv(from, 0, to, from).rot90())
                ).shade()
        );
        RESOURCE_PACK.addModel(core, mtId("block/cable/base_core_" + size));
        RESOURCE_PACK.addModel(side, mtId("block/cable/base_side_" + size));
    }

    public static void addGenericMbeBlockState(Identifier id) {
        JState state = state().add(variant().put("", JState.model(MOD_ID + ":block/generic_mbe")));
        RESOURCE_PACK.addBlockState(state, new Identifier(id.getNamespace(), id.getPath()));
    }
}
