package brachy84.brachydium.api.material;

import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.tag.Tags;
import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.item.tool.Tools;
import brachy84.brachydium.api.resource.RecipeItem;
import net.devtech.arrp.json.recipe.*;

import static brachy84.brachydium.Brachydium.RESOURCE_PACK;

public class Flags {
    /**
     *  Registers Ingot, Nugget, Block for material
     */
    public static final MaterialFlag INGOT = new MaterialFlag("ingot") {

        @Override
        public void register(Material material) {
            addItem("ingot", material);
            addItem("nugget", material);
            addBlock("block", material);
            RESOURCE_PACK.addRecipe(Brachydium.id(material.getName() + "_block"), JShapedRecipe.shaped(JPattern.pattern(
                    "III",
                    "III",
                    "III"
                    ), JKeys.keys().key("I", JIngredient.ingredient().tag(Tags.materialItem(Tags.INGOT, material))),
                    JResult.result(Brachydium.id("material/" + material.getName() + ".block").toString())));

            RESOURCE_PACK.addRecipe(Brachydium.id(material.getName() + "_ingot_of_block"), JShapelessRecipe.shapeless(
                    JIngredients.ingredients().add(JIngredient.ingredient().tag("c:" + material.getName() + "_blocks")),
                    JResult.stackedResult(Brachydium.id("material/" + material.getName() + ".ingot").toString(), 9)
            ));
        }
    };

    /*public static final MaterialFlag Ingot = MaterialFlag.builder("ingot")
            .register(((flag, material) -> {
                flag.addItem("ingot", material);
                flag.addItem("nugget", material);
                flag.addBlock("block", material);
                RESOURCE_PACK.addRecipe(Brachydium.id(material.getName() + "_block"), JShapedRecipe.shaped(JPattern.pattern(
                        "III",
                        "III",
                        "III"
                        ), JKeys.keys().key("I", JIngredient.ingredient().tag("c:" + material.getName() + "_ingots")),
                        JResult.result(Brachydium.id("material/" + material.getName() + ".block").toString())));

                RESOURCE_PACK.addRecipe(Brachydium.id(material.getName() + "_ingot_of_block"), JShapelessRecipe.shapeless(
                        JIngredients.ingredients().add(JIngredient.ingredient().tag("c:" + material.getName() + "_blocks")),
                        JResult.stackedResult(Brachydium.id("material/" + material.getName() + ".ingot").toString(), 9)
                ));
            }))
            .build();*/

    public static final MaterialFlag DUST = new MaterialFlag("dust") {

        @Override
        public void register(Material material) {
            addItem("dust", material);
            addItem("small_dust", material);
        }
    };

    public static final MaterialFlag PLATE = new MaterialFlag("plate") {

        @Override
        public MaterialFlag[] getRequiredFlags() {
            return new MaterialFlag[]{INGOT};
        }

        @Override
        public void register(Material material) {
            addItem("plate", material);
            //addItem("double_ingot", material); ?
            RRPHelper.addNbtRecipe(material.getName() + "_plate", builder -> builder
                    .type("shaped")
                    .pattern("H", "I", "I")
                    .keys(
                            Tools.HAMMER.getIngredient("H", 0, 2),
                            new RecipeItem("I", "c:" + material.getName() + "_ingots", true)
                    )
                    .result(Brachydium.id("material/" + material.getName() + ".plate").toString())
            );

        }
    };

    public static final MaterialFlag STICK = new MaterialFlag("stick") {

        @Override
        public MaterialFlag[] getRequiredFlags() {
            return new MaterialFlag[]{PLATE};
        }

        @Override
        public void register(Material material) {
            addItem("stick", material);
            RRPHelper.addNbtRecipe(material.getName() + "_stick", builder -> builder
                    .type("shaped")
                    .pattern("FO", "OP")
                    .keys(
                            Tools.FILE.getIngredient("F", 0, 1),
                            new RecipeItem("P", "c:" + material.getName() + "_plates", true),
                            new RecipeItem("O", "minecraft:air")
                    )
                    .result(Brachydium.id("material/" + material.getName() + ".stick").toString())
            );
        }
    };

    public static final MaterialFlag BOLT_SCREW = new MaterialFlag("bolt_screw") {

        @Override
        public MaterialFlag[] getRequiredFlags() {
            return new MaterialFlag[]{STICK};
        }

        @Override
        public void register(Material material) {
            addItem("bolt", material);
            addItem("screw", material);
            RRPHelper.addNbtRecipe(material.getName() + "_bolt", builder -> builder
                    .type("shaped")
                    .pattern("SO", "OR")
                    .keys(
                            Tools.SAW.getIngredient("S", 0, 1),
                            new RecipeItem("R", "c:" + material.getName() + "_sticks", true),
                            new RecipeItem("O", "minecraft:air")
                    )
                    .result(Brachydium.id("material/" + material.getName() + ".bolt").toString())
            );
            RRPHelper.addNbtRecipe(material.getName() + "_screw", builder -> builder
                    .type("shaped")
                    .pattern("FB", "BO")
                    .keys(
                            Tools.FILE.getIngredient("F", 0, 2),
                            new RecipeItem("B", "c:" + material.getName() + "_bolts", true),
                            new RecipeItem("O", "minecraft:air")

                    )
                    .result(Brachydium.id("material/" + material.getName() + ".screw").toString())
            );
        }
    };

    public static final MaterialFlag GEAR = new MaterialFlag("gear") {

        @Override
        public MaterialFlag[] getRequiredFlags() {
            return new MaterialFlag[]{PLATE};
        }

        @Override
        public void register(Material material) {
            addItem("gear", material);
            RRPHelper.addNbtRecipe(material.getName() + "_gear", builder -> builder
                    .type("shaped")
                    .pattern("RPR", "PHP", "RPR")
                    .keys(
                            Tools.HAMMER.getIngredient("H", 4, 8),
                            new RecipeItem("R", "c:" + material.getName() + "_sticks", true),
                            new RecipeItem("P", "c:" + material.getName() + "_plates", true)
                    )
                    .result(Brachydium.id("material/" + material.getName() + ".gear").toString())
            );
        }
    };

    // fine wire
    public static final MaterialFlag WIRE = new MaterialFlag("wire") {

        @Override
        public MaterialFlag[] getRequiredFlags() {
            return new MaterialFlag[]{PLATE};
        }

        @Override
        public void register(Material material) {
            addItem("wire", material);
        }
    };

    public static final MaterialFlag SMALL_GEAR = new MaterialFlag("small_gear") {

        @Override
        public MaterialFlag[] getRequiredFlags() {
            return new MaterialFlag[]{PLATE};
        }

        @Override
        public void register(Material material) {
            addItem("small_gear", material);
        }
    };

    public static final MaterialFlag DENSE_PLATE = new MaterialFlag("dense_plate") {

        @Override
        public MaterialFlag[] getRequiredFlags() {
            return new MaterialFlag[]{PLATE};
        }

        @Override
        public void register(Material material) {
            addItem("dense_plate", material);
        }
    };

    public static final MaterialFlag FRAME = new MaterialFlag("frame") {

        @Override
        public MaterialFlag[] getRequiredFlags() {
            return new MaterialFlag[]{STICK};
        }

        @Override
        public void register(Material material) {
            addBlock("frame", material);
        }
    };

    public static final MaterialFlag CASING = new MaterialFlag("casing") {

        @Override
        public MaterialFlag[] getRequiredFlags() {
            return new MaterialFlag[]{FRAME};
        }

        @Override
        public void register(Material material) {
            addBlock("casing", material);
        }
    };

    public static final MaterialFlag CRAFTING_TOOLS = new MaterialFlag("crafting_tools") {

        @Override
        public MaterialFlag[] getRequiredFlags() {
            return new MaterialFlag[]{PLATE, STICK};
        }

        @Override
        public void register(Material material) {
            if(material.getToolProperties() == null) {
                material.setToolProperties(new Material.MiningToolMaterial());
            }


            RecipeItem plate = new RecipeItem("P", Brachydium.MOD_ID + ":material/" + material.getName() + ".plate");
            RecipeItem ingot = new RecipeItem("I", Brachydium.MOD_ID + ":material/" + material.getName() + ".ingot");
            RecipeItem rod = new RecipeItem("R", Brachydium.MOD_ID + ":material/" + material.getName() + ".stick");
            RecipeItem air = new RecipeItem("O", "minecraft:air");
            RecipeItem stick = new RecipeItem("S", "minecraft:stick");

            Tools.WRENCH.createRecipe(material, new RecipeItem[]{plate, air}, "POP", "PPP", "OPO");
            Tools.HAMMER.createRecipe(material, new RecipeItem[]{ingot, air, stick},"III", "III", "OSO");
            Tools.FILE.createRecipe(material, new RecipeItem[]{plate, stick}, "P", "P", "S");
            Tools.SCREW_DRIVER.createRecipe(material, new RecipeItem[]{rod, stick, air}, "OOR", "ORO", "SOO");
            Tools.SAW.createRecipe(material, new RecipeItem[]{plate, stick}, "SSS", "PPS");
        }

        public void addTool(Material material) {

        }
    };
}
