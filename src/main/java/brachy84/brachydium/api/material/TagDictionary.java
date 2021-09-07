package brachy84.brachydium.api.material;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.block.MaterialBlock;
import brachy84.brachydium.api.block.MaterialBlockItem;
import brachy84.brachydium.api.item.ColorProvider;
import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.api.item.MaterialItem;
import brachy84.brachydium.api.resource.RRPHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class TagDictionary {

    public static final long M = 81000L;

    private static final Map<String, Entry> entries = new HashMap<>();

    public static final Entry Ingot;
    public static final Entry Gem;
    public static final Entry Dust;
    public static final Entry SmallDust;
    public static final Entry Block;
    public static final Entry Nugget;
    public static final Entry Plate;
    public static final Entry MoltenFluid;

    public static Entry getEntry(String name) {
        return entries.get(name);
    }

    static {
        Ingot = Entry.of("ingot", M);
        Gem = Entry.of("gem", M);
        Dust = Entry.of("dust", M);
        SmallDust = Entry.of("small_dust", M / 9);
        Block = Entry.of("block", M * 9, Type.BLOCK);
        Nugget = Entry.of("nugget", M / 9);
        Plate = Entry.of("plate", M);
        MoltenFluid = Entry.of("molten", -1, Type.FLUID);
    }

    public enum Type {
        NONE, ITEM, BLOCK, FLUID
    }

    public static class Entry implements IMaterialFlag<Entry> {

        private final long value;
        private final String name;
        private final String tagName;
        private final Type type;
        private ColorProvider colorProvider;

        private final List<IFlagRegistrationHandler<Entry>> tagProcessors = new ArrayList<>();
        private final List<IFlagRegistrationHandler<Entry>> resourceProcessors = new ArrayList<>();

        public Entry(String name, String tagName, long value, Type type) {
            this.name = name;
            this.value = value;
            this.tagName = tagName;
            this.type = type;
            entries.put(name, this);
            register();
            this.colorProvider = ColorProvider.allLayers();
        }

        public static Entry of(String name, long amount, Type type) {
            return new Entry(name, name + "s", amount, type);
        }

        public static Entry of(String name, long amount) {
            return new Entry(name, name + "s", amount, Type.ITEM);
        }

        public CountableIngredient unify(Material material, int amount) {
            return CountableIngredient.of(getStringTag(material), amount);
        }

        public String getStringTag(Material material) {
            return String.format("c:%s_%s", material.getRegistryName(), tagName);
        }

        public ItemStack asStack(Material material, int amount) {
            //TODO get Item first
            return unify(material, amount).getIngredient().getMatchingStacks()[0];
        }

        public long getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public String getTagName() {
            return tagName;
        }

        public Type getType() {
            return type;
        }

        public void setColorProvider(ColorProvider colorProvider) {
            this.colorProvider = colorProvider;
        }

        @Override
        public String getIdentifier() {
            return "tag:" + name;
        }

        @Override
        public void addProcessor(IFlagRegistrationHandler<Entry> processor) {
            Objects.requireNonNull(processor);
            tagProcessors.add(processor);
        }

        @Override
        public void runProcessors(Material material) {
            for (IFlagRegistrationHandler<Entry> processor : tagProcessors) {
                processor.processMaterial(material, this);
            }
        }

        @Override
        public void addResourceProvider(IFlagRegistrationHandler<Entry> processor) {
            resourceProcessors.add(processor);
        }

        @Override
        public void runResourceProviders(Material material) {
            if (type == Type.ITEM) RRPHelper.addBasicMaterialItemModel(material.getRegistryName(), name);
            RRPHelper.addSimpleMaterialItemTag(material.registryName, this);
            for (IFlagRegistrationHandler<Entry> processor : resourceProcessors) {
                processor.processMaterial(material, this);
            }
        }

        @Override
        public IMaterialFlag<?>[] getRequiredFlags() {
            return new IMaterialFlag[0];
        }

        public Item registerItem(Material material) {
            if (type == Type.ITEM) {
                MaterialItem item = new MaterialItem(this, material);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> colorProvider.getColor(tintIndex, material), item);
                return Registry.register(Registry.ITEM, item.makeId(), item);
            }
            if (type == Type.BLOCK) {
                MaterialBlock block = new MaterialBlock(material, this);
                MaterialBlockItem item = new MaterialBlockItem(block);
                ColorProviderRegistry.BLOCK.register(((state, world, pos, tintIndex) -> material.color.asInt()));
                Registry.register(Registry.BLOCK, block.makeId(), block);
                return Registry.register(Registry.ITEM, block.makeId(), item);
            }
            if (type == Type.FLUID) {
                BrachydiumApi.registerFluid(Brachydium.MOD_ID, getName(), material);
            }
            return Items.AIR;
        }
    }
}
