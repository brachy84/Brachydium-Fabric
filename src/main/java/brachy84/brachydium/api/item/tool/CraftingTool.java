package brachy84.brachydium.api.item.tool;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.api.resource.RecipeItem;
import brachy84.brachydium.gui.api.math.Color;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
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
            if (tintIndex == 0) return -1;
            NbtCompound tag = stack1.getNbt();
            if (tag != null) {
                return tag.getInt("Color");
            }
            return -1;
        }), this);

    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (group != ItemGroups.GENERAL) return;
        ItemStack stack = new ItemStack(this);
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putInt("Color", Color.of(30, 60, 220).asInt());
        stack.setNbt(tag);
        stacks.add(stack);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
    }


    @Override
    public Text getName(ItemStack stack) {
        String material = stack.getNbt().getString("Material");
        if (material != null && !material.trim().equals("")) {
            return new TranslatableText(Brachydium.MOD_ID + ".tool." + name, I18n.translate("material." + material));
        }
        return new TranslatableText(Brachydium.MOD_ID + ".tool." + name);
    }

    @Override
    public Text getName() {
        return new TranslatableText(Brachydium.MOD_ID + ".tool." + name);
    }

    public RecipeItem getIngredient(String key, int index, int damage) {
        return new RecipeItem(key, id.toString(), tool -> tool
                .remainder(group -> group
                        .entry("item", id.toString())
                        .group("data", data -> data
                                .entry("Damage", "$ i" + index + ".Damage + " + damage + " * i" + index + ".DmgMod")
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
