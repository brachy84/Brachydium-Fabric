package brachy84.brachydium.api.item.tool;

import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.resource.RecipeItem;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CraftingTool extends Item {

    // wrench, screwdriver, file
    public static final int MAX_DURABILITY = 32768;

    private final String name;
    private final Identifier id;

    public CraftingTool(String name, int maxDamage) {
        super(new FabricItemSettings().maxDamage(maxDamage).group(ItemGroups.GENERAL));
        this.name = name;
        this.id = Brachydium.id("tool." + name);

        ColorProviderRegistry.ITEM.register(((stack1, tintIndex) -> {
            CompoundTag tag = stack1.getTag();
            if(tag != null) {
                int color = tag.getInt("Color");
                if(color > 0) {
                    return color;
                    //return Integer.parseInt(Integer.toHexString(color));
                }
            }
            return -1;
        }), this);

    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
    }


    @Override
    public Text getName(ItemStack stack) {
        String material = stack.getTag().getString("Material");
        if(material != null && !material.trim().equals("")) {
            return new TranslatableText(Brachydium.MOD_ID+ ".tool." + name, I18n.translate("material." + material));
        }
        return new TranslatableText(Brachydium.MOD_ID+ ".tool." + name);
    }

    @Override
    public Text getName() {
        return new TranslatableText(Brachydium.MOD_ID+ ".tool." + name);
    }

    public void createRecipe(Material material, RecipeItem[] items, String... pattern) {
        float dmgMod = MAX_DURABILITY / (float) material.getToolProperties().getDurability();
        RRPHelper.addNbtRecipe(id.getPath() + "_" + material.getName(), builder -> builder
                .type("shaped")
                //.group("test_group")
                .pattern(pattern)
                .keys(
                        items
                )
                .result(id.toString(), nbtItemBuilder -> nbtItemBuilder
                        .data(group -> group
                                .entry("Material", material.getName())
                                .entry("Color", material.getColor())
                                .entry("DmgMod", dmgMod)
                        )
                )
        );
    }

    public RecipeItem getIngredient(String key, int index, int damage) {
        return new RecipeItem(key, id.toString(), tool -> tool
            .remainder(group -> group
                .entry("item", id.toString())
                .group("data", data -> data
                    .entry("Damage", "$ i" + index + ".Damage + " + damage + " * i"+index+".DmgMod")
                    .entry("Material", "$ i" + index + ".Material")
                    .entry("Color", "$ i" + index + ".Color")
                    .entry("DmgMod", "$ i" + index + ".DmgMod"), "{}"
                )
            )
        );
    }

    public Identifier getId() {
        return id;
    }
}
