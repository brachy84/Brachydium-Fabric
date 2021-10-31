package brachy84.brachydium.api.unification;

import brachy84.brachydium.api.unification.material.MarkerMaterial;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.material.properties.PropertyKey;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import brachy84.brachydium.api.unification.stack.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static brachy84.brachydium.api.ByValues.M;

public class OreDictUnifier {

    private OreDictUnifier() {
    }

    //simple version of material registry for marker materials
    private static final Map<String, MarkerMaterial> markerMaterialRegistry = new HashMap<>();
    private static final Map<Item, ItemMaterialInfo> materialUnificationInfo = new HashMap<>();
    private static final Map<Item, UnificationEntry> stackUnificationInfo = new HashMap<>();
    private static final Map<UnificationEntry, ArrayList<Item>> stackUnificationItems = new HashMap<>();
    private static final Map<Item, Set<String>> stackOreDictName = new HashMap<>();
    private static final Map<String, List<ItemStack>> oreDictNameStacks = new HashMap<>();

    @Nullable
    private static Comparator<Item> stackComparator;

    /*public static Comparator<ItemAndMetadata> getSimpleItemStackComparator() {
        if (stackComparator == null) {
            List<String> modPriorities = Arrays.asList(ConfigHolder.modPriorities);
            if (modPriorities.isEmpty()) {
                //noinspection ConstantConditions
                Function<ItemAndMetadata, String> modIdExtractor = stack -> stack.item.getRegistryName().getNamespace();
                stackComparator = Comparator.comparing(modIdExtractor);
            } else {
                stackComparator = Collections.reverseOrder(new CustomModPriorityComparator(modPriorities));
            }
        }
        return stackComparator;
    }

    public static Comparator<ItemStack> getItemStackComparator() {
        Comparator<ItemAndMetadata> comparator = getSimpleItemStackComparator();
        return (first, second) -> comparator.compare(new ItemAndMetadata(first), new ItemAndMetadata(second));
    }*/

    public static void registerMarkerMaterial(MarkerMaterial markerMaterial) {
        if (markerMaterialRegistry.containsKey(markerMaterial.toString())) {
            throw new IllegalArgumentException(("Marker material with id " + markerMaterial.toString() + " is already registered!"));
        }
        markerMaterialRegistry.put(markerMaterial.toString(), markerMaterial);
    }

    public static void registerOre(ItemStack itemStack, ItemMaterialInfo materialInfo) {
        if (itemStack.isEmpty()) return;
        materialUnificationInfo.put(itemStack.getItem(), materialInfo);
    }

    /*public static void registerOre(ItemStack itemStack, TagDictionary.Entry tag, @Nullable Material material) {
        registerOre(itemStack, tag.name(), material);
    }

    public static void registerOre(ItemStack itemStack, String customOrePrefix, @Nullable Material material) {
        if (itemStack.isEmpty()) return;
        OreDictionary.registerOre(customOrePrefix + (material == null ? "" : material.toCamelCaseString()), itemStack);
    }

    public static void registerOre(ItemStack itemStack, String oreDict) {
        if (itemStack.isEmpty()) return;
        OreDictionary.registerOre(oreDict, itemStack);
    }

    public static void init() {
        for (String registeredOreName : OreDictionary.getOreNames()) {
            NonNullList<ItemStack> theseOres = OreDictionary.getOres(registeredOreName);
            for (ItemStack itemStack : theseOres) {
                onItemRegistration(new OreRegisterEvent(registeredOreName, itemStack));
            }
        }
        MinecraftForge.EVENT_BUS.register(OreDictUnifier.class);
    }*/

    /*@SubscribeEvent
    public static void onItemRegistration(OreRegisterEvent event) {
        ItemAndMetadata simpleItemStack = new ItemAndMetadata(event.getOre());
        String oreName = event.getName();
        //cache this registration by name
        stackOreDictName.computeIfAbsent(simpleItemStack, k -> new HashSet<>()).add(oreName);
        List<ItemStack> itemStackListForOreDictName = oreDictNameStacks.computeIfAbsent(oreName, k -> new ArrayList<>());
        addAndSort(itemStackListForOreDictName, event.getOre().copy(), getItemStackComparator());

        //and try to transform registration name into OrePrefix + Material pair
        OrePrefix orePrefix = OrePrefix.getPrefix(oreName);
        Material material = null;
        if (orePrefix == null) {
            //split ore dict name to parts
            //oreBasalticMineralSand -> ore, Basaltic, Mineral, Sand
            ArrayList<String> splits = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            for (char character : oreName.toCharArray()) {
                if (Character.isUpperCase(character)) {
                    if (builder.length() > 0) {
                        splits.add(builder.toString());
                        builder = new StringBuilder().append(character);
                    } else splits.add(Character.toString(character));
                } else builder.append(character);
            }
            if (builder.length() > 0) {
                splits.add(builder.toString());
            }
            //try to combine in different manners
            //oreBasaltic MineralSand , ore BasalticMineralSand
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < splits.size(); i++) {
                buffer.append(splits.get(i));
                OrePrefix maybePrefix = OrePrefix.getPrefix(buffer.toString()); //ore -> OrePrefix.ore
                String possibleMaterialName = Joiner.on("").join(splits.subList(i + 1, splits.size())); //BasalticMineralSand
                String underscoreName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, possibleMaterialName); //basaltic_mineral_sand
                Material possibleMaterial = MaterialRegistry.MATERIAL_REGISTRY.getEntry(underscoreName); //Materials.BasalticSand
                if (possibleMaterial == null) {
                    //if we didn't found real material, try using marker material registry
                    possibleMaterial = markerMaterialRegistry.get(underscoreName);
                }
                if (maybePrefix != null && possibleMaterial != null) {
                    orePrefix = maybePrefix;
                    material = possibleMaterial;
                    break;
                }
            }
        }

        //finally register item
        if (orePrefix != null && (material != null || orePrefix.isSelfReferencing)) {
            UnificationEntry unificationEntry = new UnificationEntry(orePrefix, material);
            ArrayList<ItemAndMetadata> itemListForUnifiedEntry = stackUnificationItems.computeIfAbsent(unificationEntry, p -> new ArrayList<>());
            addAndSort(itemListForUnifiedEntry, simpleItemStack, getSimpleItemStackComparator());

            if (!unificationEntry.orePrefix.isMarkerPrefix()) {
                stackUnificationInfo.put(simpleItemStack, unificationEntry);
            }
            orePrefix.processOreRegistration(material);
        }
    }*/

    public static Set<String> getOreDictionaryNames(ItemStack itemStack) {
        if (itemStack.isEmpty()) return Collections.emptySet();
        Item simpleItemStack = itemStack.getItem();
        if (stackOreDictName.containsKey(simpleItemStack))
            return Collections.unmodifiableSet(stackOreDictName.get(simpleItemStack));
        return Collections.emptySet();
    }

    public static List<ItemStack> getAllWithOreDictionaryName(String oreDictionaryName) {
        return oreDictNameStacks.get(oreDictionaryName).stream()
                .map(ItemStack::copy)
                .collect(Collectors.toList());
    }

    @Nullable
    public static MaterialStack getMaterial(ItemStack itemStack) {
        if (itemStack.isEmpty()) return null;
        Item simpleItemStack = itemStack.getItem();
        UnificationEntry entry = stackUnificationInfo.get(simpleItemStack);
        if (entry != null) {
            Material entryMaterial = entry.material;
            if (entryMaterial == null) {
                entryMaterial = entry.tagDictionary.materialType;
            }
            if (entryMaterial != null) {
                return new MaterialStack(entryMaterial, entry.tagDictionary.materialAmount);
            }
        }
        ItemMaterialInfo info = materialUnificationInfo.get(simpleItemStack);
        return info == null ? null : info.material.copy();
    }

    @Nullable
    public static TagDictionary.Entry getPrefix(ItemStack itemStack) {
        if (itemStack.isEmpty()) return null;
        Item simpleItemStack = itemStack.getItem();
        UnificationEntry entry = stackUnificationInfo.get(simpleItemStack);
        if (entry != null) return entry.tagDictionary;
        return null;
    }

    public static TagDictionary.Entry getPrefix(Block block) {
        return getPrefix(new ItemStack(block));
    }

    @Nullable
    public static UnificationEntry getUnificationEntry(ItemStack itemStack) {
        if (itemStack.isEmpty()) return null;
        return stackUnificationInfo.get(itemStack.getItem());
    }

    public static ItemStack getUnificated(ItemStack itemStack) {
        if (itemStack.isEmpty()) return ItemStack.EMPTY;
        UnificationEntry unificationEntry = getUnificationEntry(itemStack);
        if (unificationEntry == null || !stackUnificationItems.containsKey(unificationEntry) || !unificationEntry.tagDictionary.isUnificationEnabled)
            return itemStack;
        ArrayList<Item> keys = stackUnificationItems.get(unificationEntry);
        return keys.size() > 0 ? new ItemStack(keys.get(0), itemStack.getCount()) : itemStack;
    }

    public static ItemStack get(UnificationEntry unificationEntry) {
        return get(unificationEntry.tagDictionary, unificationEntry.material);
    }

    public static ItemStack get(TagDictionary.Entry tagDictionary, Material material) {
        return get(tagDictionary, material, 1);
    }

    public static ItemStack get(TagDictionary.Entry tagDictionary, Material material, int stackSize) {
        //return tagDictionary.createItemStack(material, stackSize);

        UnificationEntry unificationEntry = new UnificationEntry(tagDictionary, material);
        if (!stackUnificationItems.containsKey(unificationEntry))
            return ItemStack.EMPTY;
        ArrayList<Item> keys = stackUnificationItems.get(unificationEntry);
        return keys.size() > 0 ? new ItemStack(keys.get(0), stackSize) : ItemStack.EMPTY;
    }

    public static ItemStack get(String oreDictName) {
        List<ItemStack> itemStacks = oreDictNameStacks.get(oreDictName);
        return itemStacks.size() > 0 ? itemStacks.get(0).copy() : ItemStack.EMPTY;
    }

    public static List<Entry<ItemStack, ItemMaterialInfo>> getAllItemInfos() {
        return materialUnificationInfo.entrySet().stream()
                .map(entry -> new SimpleEntry<>(new ItemStack(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }

    public static List<ItemStack> getAll(UnificationEntry unificationEntry) {
        if (!stackUnificationItems.containsKey(unificationEntry))
            return Collections.emptyList();
        ArrayList<Item> keys = stackUnificationItems.get(unificationEntry);
        return keys.stream().map(ItemStack::new).collect(Collectors.toList());
    }

    public static ItemStack getDust(Material material, long materialAmount) {
        if (!material.hasProperty(PropertyKey.DUST) || materialAmount <= 0)
            return ItemStack.EMPTY;
        if (materialAmount % M == 0 || materialAmount >= M * 16)
            return get(TagDictionary.dust, material, (int) (materialAmount / M));
        else if ((materialAmount * 4) % M == 0 || materialAmount >= M * 8)
            return get(TagDictionary.dustSmall, material, (int) ((materialAmount * 4) / M));
        else if ((materialAmount * 9) >= M)
            return get(TagDictionary.dustTiny, material, (int) ((materialAmount * 9) / M));
        return ItemStack.EMPTY;
    }


    public static ItemStack getDust(MaterialStack materialStack) {
        return getDust(materialStack.material, materialStack.amount);
    }

    public static ItemStack getIngot(Material material, long materialAmount) {
        if (!material.hasProperty(PropertyKey.INGOT) || materialAmount <= 0)
            return ItemStack.EMPTY;
        if (materialAmount % M == 0 || materialAmount >= M * 16)
            return get(TagDictionary.ingot, material, (int) (materialAmount / M));
        else if ((materialAmount * 9) >= M)
            return get(TagDictionary.nugget, material, (int) ((materialAmount * 9) / M));
        return ItemStack.EMPTY;
    }

    synchronized private static <T> void addAndSort(List<T> list, T itemToAdd, Comparator<T> comparator) {
        list.add(itemToAdd);

        if (list.size() > 1)
            list.sort(comparator);
    }
}
