package brachy84.brachydium.api.item.tool;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.api.item.BrachydiumItem;
import brachy84.brachydium.api.item.IMaterialItem;
import brachy84.brachydium.api.resource.NbtItemBuilder;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.material.MaterialRegistry;
import brachy84.brachydium.api.unification.material.Materials;
import brachy84.brachydium.api.unification.material.properties.PropertyKey;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import com.google.common.collect.Sets;
import net.devtech.arrp.json.models.JTextures;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static brachy84.brachydium.Brachydium.RESOURCE_PACK;
import static net.devtech.arrp.json.models.JModel.model;

public class ToolItem extends BrachydiumItem implements IToolItem, IMaterialItem {

    private static final Map<String, ToolItem> TOOLS = new HashMap<>();

    private static final Map<Character, ToolItem> CRAFTING_TOOLS = new HashMap<>();

    @Nullable
    public static ToolItem getOfRecipeSymbol(char c) {
        return CRAFTING_TOOLS.get(c);
    }

    @Nullable
    public static ToolItem get(TagDictionary.Entry tag) {
        return TOOLS.get(tag.lowerCaseName);
    }

    @Nullable
    public static ToolItem get(String tag) {
        return TOOLS.get(tag);
    }

    @ApiStatus.Internal
    public static void createAndRegister() {
        int i = 0;
        for (Material material : MaterialRegistry.MATERIAL_REGISTRY) {
            if (material.hasProperty(PropertyKey.TOOL)) {
                i++;
                for (ToolItem tool : TOOLS.values()) {
                    tool.materials.add(material);
                    if (tool.craftingRecipe != null)
                        tool.craftingRecipe.accept(material);
                }
            }
        }
        Brachydium.LOGGER.info("Loaded tools for {} materials", i);
    }

    private static Settings createSettings() {
        return new FabricItemSettings().group(ItemGroups.MATERIALS).maxCount(1);
    }

    private final List<Material> materials = new ArrayList<>();

    public static Identifier createId(String name) {
        return Brachydium.id("tool/" + name);
    }

    public static ToolItem create(String name) {
        return new ToolItem(createId(name), name);
    }

    public static ToolItem createOf(TagDictionary.Entry tag) {
        return create(tag.lowerCaseName);
    }

    private int dmgPerCraft = 1;
    private char recipeSymbol;
    private Consumer<Material> craftingRecipe;
    private final String name;

    protected ToolItem(Identifier id, String tag) {
        super(id, createSettings());
        RRPHelper.addItemTag(tag, id);
        TOOLS.put(tag, this);
        this.name = tag;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (group != ItemGroups.MATERIALS)
            return;
        Material material = Materials.Neutronium;
        //for (Material material : materials) {
        stacks.add(append(material));
        //}
    }

    private ItemStack append(Material material) {
        ItemStack stack = new ItemStack(this, 1);
        writeMaterial(material, stack);
        NbtCompound nbt = stack.getNbt();
        nbt.putInt("MaxDmg", material.getProperty(PropertyKey.TOOL).getToolDurability());
        nbt.putInt("Dmg", 0);
        return stack;
    }

    public Consumer<NbtItemBuilder> getRemainder(int index) {
        return data -> data
                .entry("Material", "$ i" + index + ".Material")
                .entry("MaxDmg", "$ i" + index + ".MaxDmg")
                .entry("Dmg", "$ i" + index + ".Dmg + " + dmgPerCraft);

    }

    public Consumer<NbtItemBuilder> getResultData(Material material) {
        return data -> data
                .entry("Material", material.toString())
                .entry("MaxDmg", material.getProperty(PropertyKey.TOOL).getToolDurability())
                .entry("Dmg", 0);
    }

    public ToolItem setRecipeSymbol(char c) {
        this.recipeSymbol = c;
        CRAFTING_TOOLS.put(c, this);
        return this;
    }

    public ToolItem setDmgPerCraft(int i) {
        this.dmgPerCraft = i;
        return this;
    }

    public ToolItem setCraftingRecipe(Consumer<Material> craftingRecipe) {
        this.craftingRecipe = craftingRecipe;
        return this;
    }

    public ToolItem setTextures(@Nullable String handle, Integer... colorLayers) {
        return setTextures(handle, name, colorLayers);
    }

    public ToolItem setTextures(@Nullable String handle, String head, Integer... colorLayers) {
        String mod = getId().getNamespace();
        Identifier path = new Identifier(mod, "item/" + getId().getPath());
        String base = mod + ":item/tools/";
        JTextures textures = new JTextures();
        if (handle != null) {
            textures.layer0(base + handle)
                    .layer1(base + head);
        } else {
            textures.layer0(base + head);
        }

        RESOURCE_PACK.addModel(model().parent("item/handheld").textures(textures), path);
        Set<Integer> layers = Sets.newHashSet(colorLayers);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (layers.contains(tintIndex)) {
                Material material = getMaterial(stack);
                if (material != null)
                    return material.getMaterialRGB();
            }
            return -1;
        }, this);
        return this;
    }

    public char getRecipeSymbol() {
        return recipeSymbol;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getItemDamage(stack) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round(13.0F - (float) getItemDamage(stack) * 13.0F / (float) getMaxItemDamage(stack));
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        int maxDmg = getMaxItemDamage(stack);
        float f = Math.max(0.0F, ((float) maxDmg - (float) getItemDamage(stack)) / (float) maxDmg);
        return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isDamageable() {
        return super.isDamageable();
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        stack.getOrCreateNbt().putInt("Dmg", damage);
    }

    @Override
    public int getItemDamage(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("Dmg"))
            return 0;
        return nbt.getInt("Dmg");
    }

    @Override
    public int getMaxItemDamage(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("MaxDmg"))
            return 0;
        return nbt.getInt("MaxDmg");
    }

    @Override
    public Text getName(ItemStack stack) {
        Material material = getMaterial(stack);
        if (material == null)
            return new TranslatableText(getTranslationKey(), "Error");
        return new TranslatableText(getTranslationKey(), material.getLocalizedName());
    }

    @Override
    public String getTranslationKey() {
        return "brachydium.item.tool." + name;
    }
}
