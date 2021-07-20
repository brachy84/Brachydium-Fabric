package brachy84.brachydium.api.material;

import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.tag.Tags;
import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.item.tool.Tools;
import brachy84.brachydium.api.resource.RecipeItem;
import net.devtech.arrp.json.recipe.*;

import static brachy84.brachydium.Brachydium.RESOURCE_PACK;

public class Flags {


    /*
     *  Registers Ingot, Nugget, Block for material

    public static final MaterialFlagOld INGOT = new MaterialFlagOld("ingot") {

        @Override
        public void register(MaterialOld materialOld) {
            addItem("ingot", materialOld);
            addItem("nugget", materialOld);
            addBlock("block", materialOld);
            RESOURCE_PACK.addRecipe(Brachydium.id(materialOld.getName() + "_block"), JShapedRecipe.shaped(JPattern.pattern(
                    "III",
                    "III",
                    "III"
                    ), JKeys.keys().key("I", JIngredient.ingredient().tag(Tags.materialItem(Tags.INGOT, materialOld))),
                    JResult.result(Brachydium.id("material/" + materialOld.getName() + ".block").toString())));

            RESOURCE_PACK.addRecipe(Brachydium.id(materialOld.getName() + "_ingot_of_block"), JShapelessRecipe.shapeless(
                    JIngredients.ingredients().add(JIngredient.ingredient().tag("c:" + materialOld.getName() + "_blocks")),
                    JResult.stackedResult(Brachydium.id("material/" + materialOld.getName() + ".ingot").toString(), 9)
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
            .build();

    public static final MaterialFlagOld DUST = new MaterialFlagOld("dust") {

        @Override
        public void register(MaterialOld materialOld) {
            addItem("dust", materialOld);
            addItem("small_dust", materialOld);
        }
    };

    public static final MaterialFlagOld PLATE = new MaterialFlagOld("plate") {

        @Override
        public MaterialFlagOld[] getRequiredFlags() {
            return new MaterialFlagOld[]{INGOT};
        }

        @Override
        public void register(MaterialOld materialOld) {
            addItem("plate", materialOld);
            //addItem("double_ingot", material); ?
            RRPHelper.addNbtRecipe(materialOld.getName() + "_plate", builder -> builder
                    .type("shaped")
                    .pattern("H", "I", "I")
                    .keys(
                            Tools.HAMMER.getIngredient("H", 0, 2),
                            new RecipeItem("I", "c:" + materialOld.getName() + "_ingots", true)
                    )
                    .result(Brachydium.id("material/" + materialOld.getName() + ".plate").toString())
            );

        }
    };

    public static final MaterialFlagOld STICK = new MaterialFlagOld("stick") {

        @Override
        public MaterialFlagOld[] getRequiredFlags() {
            return new MaterialFlagOld[]{PLATE};
        }

        @Override
        public void register(MaterialOld materialOld) {
            addItem("stick", materialOld);
            RRPHelper.addNbtRecipe(materialOld.getName() + "_stick", builder -> builder
                    .type("shaped")
                    .pattern("FO", "OP")
                    .keys(
                            Tools.FILE.getIngredient("F", 0, 1),
                            new RecipeItem("P", "c:" + materialOld.getName() + "_plates", true),
                            new RecipeItem("O", "minecraft:air")
                    )
                    .result(Brachydium.id("material/" + materialOld.getName() + ".stick").toString())
            );
        }
    };

    public static final MaterialFlagOld BOLT_SCREW = new MaterialFlagOld("bolt_screw") {

        @Override
        public MaterialFlagOld[] getRequiredFlags() {
            return new MaterialFlagOld[]{STICK};
        }

        @Override
        public void register(MaterialOld materialOld) {
            addItem("bolt", materialOld);
            addItem("screw", materialOld);
            RRPHelper.addNbtRecipe(materialOld.getName() + "_bolt", builder -> builder
                    .type("shaped")
                    .pattern("SO", "OR")
                    .keys(
                            Tools.SAW.getIngredient("S", 0, 1),
                            new RecipeItem("R", "c:" + materialOld.getName() + "_sticks", true),
                            new RecipeItem("O", "minecraft:air")
                    )
                    .result(Brachydium.id("material/" + materialOld.getName() + ".bolt").toString())
            );
            RRPHelper.addNbtRecipe(materialOld.getName() + "_screw", builder -> builder
                    .type("shaped")
                    .pattern("FB", "BO")
                    .keys(
                            Tools.FILE.getIngredient("F", 0, 2),
                            new RecipeItem("B", "c:" + materialOld.getName() + "_bolts", true),
                            new RecipeItem("O", "minecraft:air")

                    )
                    .result(Brachydium.id("material/" + materialOld.getName() + ".screw").toString())
            );
        }
    };

    public static final MaterialFlagOld GEAR = new MaterialFlagOld("gear") {

        @Override
        public MaterialFlagOld[] getRequiredFlags() {
            return new MaterialFlagOld[]{PLATE};
        }

        @Override
        public void register(MaterialOld materialOld) {
            addItem("gear", materialOld);
            RRPHelper.addNbtRecipe(materialOld.getName() + "_gear", builder -> builder
                    .type("shaped")
                    .pattern("RPR", "PHP", "RPR")
                    .keys(
                            Tools.HAMMER.getIngredient("H", 4, 8),
                            new RecipeItem("R", "c:" + materialOld.getName() + "_sticks", true),
                            new RecipeItem("P", "c:" + materialOld.getName() + "_plates", true)
                    )
                    .result(Brachydium.id("material/" + materialOld.getName() + ".gear").toString())
            );
        }
    };

    // fine wire
    public static final MaterialFlagOld WIRE = new MaterialFlagOld("wire") {

        @Override
        public MaterialFlagOld[] getRequiredFlags() {
            return new MaterialFlagOld[]{PLATE};
        }

        @Override
        public void register(MaterialOld materialOld) {
            addItem("wire", materialOld);
        }
    };

    public static final MaterialFlagOld SMALL_GEAR = new MaterialFlagOld("small_gear") {

        @Override
        public MaterialFlagOld[] getRequiredFlags() {
            return new MaterialFlagOld[]{PLATE};
        }

        @Override
        public void register(MaterialOld materialOld) {
            addItem("small_gear", materialOld);
        }
    };

    public static final MaterialFlagOld DENSE_PLATE = new MaterialFlagOld("dense_plate") {

        @Override
        public MaterialFlagOld[] getRequiredFlags() {
            return new MaterialFlagOld[]{PLATE};
        }

        @Override
        public void register(MaterialOld materialOld) {
            addItem("dense_plate", materialOld);
        }
    };

    public static final MaterialFlagOld FRAME = new MaterialFlagOld("frame") {

        @Override
        public MaterialFlagOld[] getRequiredFlags() {
            return new MaterialFlagOld[]{STICK};
        }

        @Override
        public void register(MaterialOld materialOld) {
            addBlock("frame", materialOld);
        }
    };

    public static final MaterialFlagOld CASING = new MaterialFlagOld("casing") {

        @Override
        public MaterialFlagOld[] getRequiredFlags() {
            return new MaterialFlagOld[]{FRAME};
        }

        @Override
        public void register(MaterialOld materialOld) {
            addBlock("casing", materialOld);
        }
    };

    public static final MaterialFlagOld CRAFTING_TOOLS = new MaterialFlagOld("crafting_tools") {

        @Override
        public MaterialFlagOld[] getRequiredFlags() {
            return new MaterialFlagOld[]{PLATE, STICK};
        }

        @Override
        public void register(MaterialOld materialOld) {
            if(materialOld.getToolProperties() == null) {
                materialOld.setToolProperties(new MaterialOld.MiningToolMaterial());
            }


            RecipeItem plate = new RecipeItem("P", Brachydium.MOD_ID + ":material/" + materialOld.getName() + ".plate");
            RecipeItem ingot = new RecipeItem("I", Brachydium.MOD_ID + ":material/" + materialOld.getName() + ".ingot");
            RecipeItem rod = new RecipeItem("R", Brachydium.MOD_ID + ":material/" + materialOld.getName() + ".stick");
            RecipeItem air = new RecipeItem("O", "minecraft:air");
            RecipeItem stick = new RecipeItem("S", "minecraft:stick");

            Tools.WRENCH.createRecipe(materialOld, new RecipeItem[]{plate, air}, "POP", "PPP", "OPO");
            Tools.HAMMER.createRecipe(materialOld, new RecipeItem[]{ingot, air, stick},"III", "III", "OSO");
            Tools.FILE.createRecipe(materialOld, new RecipeItem[]{plate, stick}, "P", "P", "S");
            Tools.SCREW_DRIVER.createRecipe(materialOld, new RecipeItem[]{rod, stick, air}, "OOR", "ORO", "SOO");
            Tools.SAW.createRecipe(materialOld, new RecipeItem[]{plate, stick}, "SSS", "PPS");
        }

        public void addTool(MaterialOld materialOld) {

        }
    };*/
}
