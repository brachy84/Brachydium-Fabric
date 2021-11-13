package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.unification.LoadableTag;
import com.google.common.collect.Lists;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RecipeItem implements Predicate<ItemStack>, Iterable<ItemStack> {

    private final Collection<ItemStack> values;
    private final Tag<Item> tag;
    private final List<ItemStack> allValid = new ArrayList<>();
    private final int amount;
    private final float chance;

    private boolean loadedLoadables;

    private RecipeItem(@Nullable Collection<ItemStack> values, @Nullable Tag<Item> tag, int amount, float chance) {
        this.values = values == null ? Collections.emptyList() : values;
        this.tag = tag;
        this.amount = amount;
        this.chance = chance;
        if (this.values.size() == 0 && tag == null) {
            throw new IllegalArgumentException("Ingredient can't be empty");
        }
        if (amount < 0)
            throw new IllegalArgumentException("Amount in ingredient can't be null");
        loadedLoadables = LoadableTag.isLoaded() && !(tag instanceof LoadableTag);
        buildValidList();
    }

    public RecipeItem(Tag<Item> tag, int amount, float chance) {
        this(new ArrayList<>(), tag, amount, chance);
    }

    public RecipeItem(Collection<ItemStack> values, int amount, float chance) {
        this(values, null, amount, chance);
    }

    public RecipeItem(int amount, float chance, ItemStack... values) {
        this(Lists.newArrayList(values), null, amount, chance);
    }

    public RecipeItem(ItemStack stack, float chance) {
        this(stack.getCount(), chance, stack);
    }

    public RecipeItem(ItemStack stack) {
        this(stack.getCount(), 1f, stack);
    }

    public static RecipeItem of(net.minecraft.recipe.Ingredient ingredient, int amount, float chance) {
        return new RecipeItem(amount, chance, ingredient.getMatchingStacks());
    }

    public static RecipeItem ofTagId(Identifier id, int amount, float chance) {
        return new RecipeItem(LoadableTag.getItemTag(id), amount, chance);
    }

    public static RecipeItem ofTagId(String id, int amount, float chance) {
        return ofTagId(new Identifier(id), amount, chance);
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        for (ItemStack t : getAllValid()) {
            if (areStacksEqual(stack, t))
                return true;
        }
        return false;
    }

    public int getAmount() {
        return amount;
    }

    public float getChance() {
        return chance;
    }

    private static boolean areStacksEqual(ItemStack stack, ItemStack stack1) {
        if (stack.getItem() != stack1.getItem())
            return false;
        NbtCompound nbt = stack.getNbt();
        NbtCompound nbt1 = stack1.getNbt();
        if (nbt == null && nbt1 == null)
            return true;
        if (nbt == null || nbt1 == null)
            return false;
        return nbt.equals(nbt1);
    }

    public List<ItemStack> getAllValid() {
        if (!loadedLoadables && LoadableTag.isLoaded()) {
            buildValidList();
            loadedLoadables = true;
        }
        return Collections.unmodifiableList(allValid);
    }

    private void buildValidList() {
        allValid.clear();
        allValid.addAll(values);
        if (tag != null)
            allValid.addAll(tag.values().stream().map(ItemStack::new).collect(Collectors.toList()));
    }

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return getAllValid().iterator();
    }

    public EntryIngredient toEntryStack() {
        EntryIngredient entries = EntryIngredients.ofItemStacks(getAllValid());
        for (EntryStack<?> entryStack : entries) {
            ((ItemStack) entryStack.getValue()).setCount(amount);
        }
        return entries;
    }
}
