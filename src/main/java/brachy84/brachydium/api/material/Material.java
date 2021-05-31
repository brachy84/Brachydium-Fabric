package brachy84.brachydium.api.material;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.fluid.MaterialFluid;
import brachy84.brachydium.api.fluid.MaterialFluidRenderer;
import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.api.item.MaterialItem;
import brachy84.brachydium.api.util.BrachydiumRegistry;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Material {

    public static final Logger LOGGER = LogManager.getLogger(Brachydium.MOD_ID + ":material_system");

    //private static List<Material> materials = new ArrayList<>();

    public static final BrachydiumRegistry<String, Material> REGISTRY = new BrachydiumRegistry<>();

    private String name;
    private int color;
    private FabricBlockSettings blockSettings;
    private List<MaterialFlag> flags = new ArrayList<>();
    private Map<String, MaterialItem> components = new HashMap<>();
    private MiningToolMaterial toolProperties;
    private MaterialFluid.Still fluid;

    public Material(String name, int color, MaterialFlag[] flags) {
        this.name = name;
        this.color = color;
        addFlags(flags);
        //addMaterial(this);
        register();
    }

    boolean hasFlag(MaterialFlag flag) {
        return flags.contains(flag);
    }

    public void addFlags(MaterialFlag... flags) {
        for(MaterialFlag flag : flags) {
            if(!this.flags.contains(flag)) {
                this.flags.add(flag);
                addFlags(flag.getRequiredFlags());
            }
        }
    }

    public static void register(Material[] materials) {
        System.out.println("Registering materials");
        for(Material material : materials) {
            System.out.println("Registering " + material.getName());
            //addMaterial(material);
            material.register();
        }
    }

    private void register() {
        for (MaterialFlag flag : flags) {
            System.out.println("Registering " + flag.getName());
            flag.register(this);
        }
        registerFluid();
        if(REGISTRY.hasKey(name)) {
            Brachydium.LOGGER.info("Material {} is already registered. Adding missing flags", name);
            Material dupe = REGISTRY.tryGetEntry(name);
            assert dupe != null;
            dupe.addFlags(flags.toArray(new MaterialFlag[0]));
        } else {
            REGISTRY.register(name, this);
        }
    }

    public void registerClient() {
        if(fluid != null) {
            MaterialFluidRenderer.setup(fluid, new Identifier("lava"), color);
        }
    }

    protected void registerComponent(String name, MaterialItem item) {
        components.put(name, item);
    }

    public FabricBlockSettings getBlockSettings() {
        return blockSettings;
    }

    public Material setBlockSettings(FabricBlockSettings blockSettings) {
        //requiresFlags(Flags.INGOT);
        this.blockSettings = blockSettings;
        return this;
    }

    public Material setBlockSettings(float hardness, float resistance, int miningLevel) {
        return setBlockSettings(FabricBlockSettings.of(net.minecraft.block.Material.METAL)
                .strength(hardness, resistance)
                .requiresTool()
                .breakByTool(FabricToolTags.PICKAXES, miningLevel));
    }

    public Material setCablePropertie(CableProperties cableProperties) {
        requiresFlags(Flags.PLATE);
        return this;
    }

    public Material setToolProperties(MiningToolMaterial toolProperties) {
        requiresFlags(Flags.CRAFTING_TOOLS);
        this.toolProperties = toolProperties;
        return this;
    }

    /**
     * Checks if the material has the required flags and adds them if not
     * @param required The required flags the material needs
     */
    public void requiresFlags(MaterialFlag... required) {
        for(MaterialFlag flag : required) {
            if(!flags.contains(flag)) {
                flags.add(flag);
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public List<MaterialFlag> getFlags() {
        return flags;
    }

    public Text translatedText() {
        return new TranslatableText("material." + name);
    }

    public class CableProperties {
        private int voltage;
    }

    public void registerFluid() {
        this.fluid = BrachydiumApi.registerFluid(Brachydium.MOD_ID, this);
    }

    @Nullable
    public MaterialFluid.Still getFluid() {
        return fluid;
    }

    public FluidStack getFluid(int amount) {
        if(fluid == null) return FluidStack.EMPTY;
        return new FluidStack(fluid, amount);
    }

    // Flags
    public CountableIngredient getCountableIngredient() {return null;}

    public ItemStack getStack(String componentName, int amount) {
        return new ItemStack(components.get(componentName), amount);
    }

    public ItemStack ingot(int amount) {
        return getStack("ingot", amount);
    }

    public ItemStack nugget(int amount) {
        return getStack("nugget", amount);
    }

    public ItemStack plate(int amount) {
        return getStack("plate", amount);
    }

    public ItemStack stick(int amount) {
        return getStack("stick", amount);
    }

    public ItemStack gear(int amount) {
        return getStack("gear", amount);
    }

    public ItemStack dust(int amount) {
        return getStack("dust", amount);
    }

    public ItemStack smallDust(int amount) {
        return getStack("small_dust", amount);
    }

    public ItemStack bolt(int amount) {
        return getStack("bolt", amount);
    }

    public ItemStack screw(int amount) {
        return getStack("screw", amount);
    }

    public ItemStack smallGear(int amount) {
        return getStack("small_gear", amount);
    }

    public MiningToolMaterial getToolProperties() {
        return toolProperties;
    }

    public static class MiningToolMaterial implements ToolMaterial {
        private int durability;
        private float miningSpeed;
        private int miningLevel;
        private float attackDamage;
        private int enchantability;

        public MiningToolMaterial(int durability, float miningSpeed, int miningLevel, float attackDamage, int enchantability) {
            this.durability = durability;
            this.miningSpeed = miningSpeed;
            this.miningLevel = miningLevel;
            this.attackDamage = attackDamage;
            this.enchantability = enchantability;
        }

        public MiningToolMaterial(int durability, float miningSpeed, int miningLevel, float attackDamage) {
            this.durability = durability;
            this.miningSpeed = miningSpeed;
            this.miningLevel = miningLevel;
            this.attackDamage = attackDamage;
            this.enchantability = 8;
        }

        public MiningToolMaterial(int durability, float miningSpeed, int miningLevel) {
            this.durability = durability;
            this.miningSpeed = miningSpeed;
            this.miningLevel = miningLevel;
            this.attackDamage = 2.0f;
            this.enchantability = 8;
        }

        public MiningToolMaterial(int durability) {
            this.durability = durability;
            this.miningSpeed = 5.0f;
            this.miningLevel = 2;
            this.attackDamage = 2.0f;
            this.enchantability = 8;
        }

        public MiningToolMaterial() {
            this.durability = 256;
            this.miningSpeed = 5.0f;
            this.miningLevel = 2;
            this.attackDamage = 2.0f;
            this.enchantability = 8;
        }

        @Override
        public int getDurability() {
            return durability;
        }

        @Override // wood has 2.0 / diamond has 8.0
        public float getMiningSpeedMultiplier() {
            return miningSpeed;
        }

        @Override
        public float getAttackDamage() {
            return attackDamage;
        }

        @Override // diamond has 3
        public int getMiningLevel() {
            return miningLevel;
        }

        @Override // gold has 22 / diamond has 10
        public int getEnchantability() {
            return enchantability;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return null;
        }
    }
}
