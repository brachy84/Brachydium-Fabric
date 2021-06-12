package brachy84.brachydium.api.material;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.tag.TagDictionary;
import brachy84.brachydium.api.util.BrachydiumRegistry;
import brachy84.brachydium.gui.math.Color;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class Material {

    public static final BrachydiumRegistry<String, Material> REGISTRY = new BrachydiumRegistry<>();

    public final String registryName;
    public final Color color;
    public final String chemicalFormula;
    private final MaterialType type;

    private final ImmutableList<MaterialStack> components;
    private final List<IMaterialFlag<?>> materialFlags = new ArrayList<>();
    private final Map<String, Item> items = new HashMap<>();

    private final Element element;

    // For blocks
    private int miningLevel = 1;
    private float hardness = 2;
    private float resistance = 2;

    private Material(String registryName, Color color, MaterialType type, ImmutableList<MaterialStack> components, Element element, IMaterialFlag<?>... flags) {
        this.registryName = registryName;
        this.color = color.withAlpha((byte) 255);
        this.type = type;
        this.components = components;
        this.element = element;
        this.chemicalFormula = calculateChemicalFormula();
        addFlags(flags);
        addFlags(type.getDefaultFlags());
    }

    public static Material create(String registryName, Color color, MaterialType type, ImmutableList<MaterialStack> components, Element element, IMaterialFlag<?>... flags) {
        if(REGISTRY.hasKey(registryName)) {
            Material material = REGISTRY.getEntry(registryName);
            material.addFlags(flags);
            return material;
        }
        Material material = new Material(registryName, color, type, components, element, flags);
        REGISTRY.register(registryName, material);
        return material;
    }

    public static Builder builder() {
        return new Builder();
    }

    @ApiStatus.Internal
    public static void registerItems() {
        REGISTRY.foreach(material -> {
            for(IMaterialFlag<?> flag : material.materialFlags) {
                if(flag instanceof TagDictionary.Entry) {
                    Item item = ((TagDictionary.Entry) flag).registerItem(material);
                    if(item != Items.AIR) {
                        material.items.put(((TagDictionary.Entry) flag).getName(), item);
                    }
                }
            }
        });
    }

    @ApiStatus.Internal
    public static void registerResources() {
        REGISTRY.foreach(material -> {
            for(IMaterialFlag<?> flag : material.materialFlags) {
                flag.runResourceProviders(material);
            }
        });
    }

    @ApiStatus.Internal
    public static void runProcessors() {
        REGISTRY.foreach(material -> {
            for(IMaterialFlag<?> flag : material.materialFlags) {
                flag.runProcessors(material);
            }
        });
    }

    public MaterialStack toMaterialStack(int amount) {
        return new MaterialStack(this, amount);
    }

    private String calculateChemicalFormula() {
        if (element != null) {
            return element.name();
        }
        if (!components.isEmpty()) {
            StringBuilder components = new StringBuilder();
            for (MaterialStack component : this.components)
                components.append(component.toString());
            return components.toString();
        }
        return "";
    }

    public boolean isType(MaterialType type) {
        return this.type == type;
    }

    public void addFlags(IMaterialFlag<?>... flags) {
        for(IMaterialFlag<?> flag : flags) {
            if(flag == null) continue;
            if(!materialFlags.contains(flag)) {
                materialFlags.add(flag);
                addFlags(flag.getRequiredFlags());
            }
        }
    }

    public boolean hasFlag(IMaterialFlag<?> flag) {
        return materialFlags.contains(flag);
    }

    /**
     * @param clazz the class to check for ! Must implement {@link IMaterialFlag}
     * @return if the material has a flag that is an instance of the class
     */
    public <T extends IMaterialFlag<?>> boolean hasFlag(Class<T> clazz) {
        for (IMaterialFlag<?> flag : materialFlags) {
            if(clazz.isAssignableFrom(flag.getClass())) {
                return true;
            }
        }
        return false;
    }

    public boolean isRadioactive() {
        if (element != null)
            return element.halfLifeSeconds >= 0;
        for (MaterialStack material : components)
            if (material.getMaterial().isRadioactive()) return true;
        return false;
    }

    public long getProtons() {
        if (element != null)
            return element.getProtons();
        if (components.isEmpty())
            return Element.Tc.getProtons();
        long totalProtons = 0;
        for (MaterialStack material : components) {
            totalProtons += material.amount * material.material.getProtons();
        }
        return totalProtons;
    }

    public long getNeutrons() {
        if (element != null)
            return element.getNeutrons();
        if (components.isEmpty())
            return Element.Tc.getNeutrons();
        long totalNeutrons = 0;
        for (MaterialStack material : components) {
            totalNeutrons += material.amount * material.material.getNeutrons();
        }
        return totalNeutrons;
    }

    public long getMass() {
        if (element != null)
            return element.getMass();
        if (components.isEmpty())
            return Element.Tc.getMass();
        long totalMass = 0;
        for (MaterialStack material : components) {
            totalMass += material.amount * material.material.getMass();
        }
        return totalMass;
    }

    public long getAverageProtons() {
        if (element != null)
            return element.getProtons();
        if (components.isEmpty())
            return Element.Tc.getProtons();
        long totalProtons = 0, totalAmount = 0;
        for (MaterialStack material : components) {
            totalAmount += material.amount;
            totalProtons += material.amount * material.material.getAverageProtons();
        }
        return totalProtons / totalAmount;
    }

    public long getAverageNeutrons() {
        if (element != null)
            return element.getNeutrons();
        if (components.isEmpty())
            return Element.Tc.getNeutrons();
        long totalNeutrons = 0, totalAmount = 0;
        for (MaterialStack material : components) {
            totalAmount += material.amount;
            totalNeutrons += material.amount * material.material.getAverageNeutrons();
        }
        return totalNeutrons / totalAmount;
    }

    public long getAverageMass() {
        if (element != null)
            return element.getMass();
        if (components.size() <= 0)
            return Element.Tc.getMass();
        long totalMass = 0, totalAmount = 0;
        for (MaterialStack material : components) {
            totalAmount += material.amount;
            totalMass += material.amount * material.material.getAverageMass();
        }
        return totalMass / totalAmount;
    }

    private void setBlockSettings(int miningLevel, float hardness, float resistance) {
        this.miningLevel = miningLevel;
        this.hardness = hardness;
        this.resistance = resistance;
    }

    public int getMiningLevel() {
        return miningLevel;
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistance() {
        return resistance;
    }

    /**
     * @param tag should be equal to {@link TagDictionary.Entry#getName()}
     * @return the item that belongs to the tag
     */
    public Item getItem(String tag) {
        return items.get(tag);
    }

    public Item getItem(TagDictionary.Entry tag) {
        return items.get(tag.getName());
    }

    public ItemStack getItemStack(TagDictionary.Entry tag, int amount) {
        return new ItemStack(getItem(tag), amount);
    }

    public String toCamelCaseString() {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, toString());
    }

    public String getRegistryName() {
        return registryName;
    }

    public String getUnlocalizedName() {
        return "material." + getRegistryName();
    }

    @Environment(EnvType.CLIENT)
    public String getLocalizedName() {
        return I18n.translate(getUnlocalizedName());
    }

    @Override
    public String toString() {
        return registryName;
    }

    public static class Builder {

        private String registryName;
        private Color color;
        private MaterialType type;

        private final List<MaterialStack> components = new ArrayList<>();
        private final List<IMaterialFlag<?>> materialFlags = new ArrayList<>();

        private Element element;

        private int miningLevel = 1;
        private float hardness = 2;
        private float resistance = 2;

        private Builder() {}

        public Builder registryName(String name) {
            if(name == null || name.trim().equals("")) {
                throw new IllegalArgumentException("Name can't be null or empty");
            }
            this.registryName = name;
            return this;
        }

        public Builder type(MaterialType type) {
            Objects.requireNonNull(type);
            this.type = type;
            return this;
        }

        public Builder color(Color color) {
            Objects.requireNonNull(color);
            this.color = color;
            return this;
        }

        public Builder color(int color) {
            return color(Color.of(color));
        }

        public Builder color(int r, int g, int b) {
            return color(Color.of(r, g, b));
        }

        public Builder color(float r, float g, float b) {
            return color(Color.of(r, g, b));
        }

        public Builder addMaterialComponents(MaterialStack... materialStacks) {
            for(MaterialStack stack : materialStacks) {
                Objects.requireNonNull(stack);
                components.add(stack);
            }
            return this;
        }

        public Builder element(Element element) {
            Objects.requireNonNull(element);
            this.element = element;
            return this;
        }

        public Builder addFlags(IMaterialFlag<?>... flags) {
            for(IMaterialFlag<?> flag : flags) {
                Objects.requireNonNull(flag);
                materialFlags.add(flag);
            }
            return this;
        }

        private Builder setBlockSettings(int miningLevel, float hardness, float resistance) {
            this.miningLevel = miningLevel;
            this.hardness = hardness;
            this.resistance = resistance;
            return this;
        }

        public Material build() {
            Objects.requireNonNull(registryName);
            Objects.requireNonNull(color);
            if(element == null && components.size() == 0) {
                throw new IllegalArgumentException("Element or material components must be defined");
            } else if(element != null && components.size() > 0) {
                Brachydium.LOGGER.warn("Element and material components are both defined for material {}. Material components will be ignored if element is not null", registryName);
            }
            if(type == null) type = MaterialType.UNDEFINED;
            Material material = Material.create(registryName, color, type, ImmutableList.copyOf(components), element, materialFlags.toArray(new IMaterialFlag<?>[0]));
            material.setBlockSettings(miningLevel, resistance, hardness);
            return material;
        }
    }
}
