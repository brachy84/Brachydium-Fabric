package brachy84.brachydium.api.item;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.tag.LoadableTag;
import brachy84.brachydium.api.util.MatchingType;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;

public class CountableIngredient implements Predicate<ItemStack> {

    private LoadableTag tag;

    private final Ingredient ingredient;
    private int amount;

    public static final CountableIngredient EMPTY = new CountableIngredient(ItemStack.EMPTY);

    public CountableIngredient(Ingredient ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public CountableIngredient(int amount, Item... item) {
        this.ingredient = Ingredient.ofItems(item);
        this.amount = amount;
    }

    public CountableIngredient(ItemStack itemStack) {
        this.ingredient = Ingredient.ofStacks(itemStack);
        this.amount = itemStack.getCount();
    }

    public static CountableIngredient of(Identifier tagId, int amount) {
        /*if(!Brachydium.areTagsLoaded()) {
            CountableIngredient ci = new CountableIngredient((Ingredient) null, amount);
            ci.tag = LoadableTag.getOrCreate(tag);
            return ci;
        }*/
        Tag<Item> tag = TagRegistry.item(tagId);
        if(tag == null) {
            return EMPTY;
        }
        return new CountableIngredient(Ingredient.fromTag(tag), amount);
    }

    public static CountableIngredient of(String name, int amount) {
        if(!name.contains(":")) {
            name = "c:" + name;
        }
        return of(new Identifier(name), amount);
    }

    public boolean isEmpty() {
        return amount == 0 || getIngredient() == null;
    }

    public boolean isNotLoaded() {
        return tag != null && !tag.isLoaded();
    }

    public boolean matches(ItemStack stack, MatchingType type) {
        Ingredient i = getIngredient();
        if(type == MatchingType.EXACT)
            return amount == stack.getCount() && i.test(stack);
        else if(type == MatchingType.AT_LEAST)
            return amount <= stack.getCount() && i.test(stack);
        else if(type == MatchingType.IGNORE_AMOUNT)
            return i.test(stack);
        return false;
    }

    public Ingredient getIngredient() {
        /*if(ingredient == null && tag != null) {
            if(tag.isLoaded())
                ingredient = Ingredient.fromTag(tag.getTag());
            else
                throw new IllegalStateException("Can't get ingredient of tag " + tag.getId() + " when tags are not loaded");
        }*/
        return ingredient;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack toItemStack() {
        ItemStack stack = getIngredient().getMatchingStacks()[0];
        stack.setCount(amount);
        return stack;
    }

    public EntryIngredient toEntryStack() {
        Brachydium.LOGGER.info("To EntryStack. Size {}", getMatchingStacks().length);
        EntryIngredient entries = EntryIngredients.ofIngredient(getIngredient());
        for(EntryStack<?> entryStack : entries) {
            ((ItemStack)entryStack.getValue()).setCount(amount);
        }
        return entries;
    }

    public ItemStack[] getMatchingStacks() {
        return getIngredient().getMatchingStacks();
    }

    @Override
    public boolean test(ItemStack stack) {
        return getIngredient().test(stack);
    }
}
