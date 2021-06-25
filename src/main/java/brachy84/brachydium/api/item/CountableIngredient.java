package brachy84.brachydium.api.item;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.tag.LoadableTag;
import brachy84.brachydium.api.tag.Tags;
import brachy84.brachydium.api.util.MatchingType;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CountableIngredient {

    private LoadableTag tag;

    private Ingredient ingredient;
    private int amount;

    public static final CountableIngredient EMPTY = new CountableIngredient(ItemStack.EMPTY);

    public CountableIngredient(Ingredient ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public CountableIngredient(Item item, int amount) {
        this.ingredient = Ingredient.ofItems(item);
        this.amount = amount;
    }

    public CountableIngredient(ItemStack itemStack) {
        this.ingredient = Ingredient.ofStacks(itemStack);
        this.amount = itemStack.getCount();
    }

    public static CountableIngredient of(Identifier tag, int amount) {
        if(!Brachydium.areTagsLoaded()) {
            CountableIngredient ci = new CountableIngredient((Ingredient) null, amount);
            ci.tag = LoadableTag.getOrCreate(tag);
            return ci;
        }
        Tag<Item> tag1 = Tags.of(tag);
        if(tag1 == null) {
            return EMPTY;
        }
        return new CountableIngredient(Ingredient.fromTag(tag1), amount);
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
        if(ingredient == null) {
            if(tag.isLoaded())
                ingredient = Ingredient.fromTag(tag.getTag());
            else
                throw new IllegalStateException("Can't get ingredient when tags are not loaded");
        }
        return ingredient;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack toItemStack() {
        ItemStack stack = getIngredient().getMatchingStacksClient()[0];
        stack.setCount(amount);
        return stack;
    }

    public EntryIngredient toEntryStack() {
        EntryIngredient entries = EntryIngredients.ofIngredient(getIngredient());
        for(EntryStack<?> entryStack : entries) {
            ((ItemStack)entryStack.getValue()).setCount(amount);
        }
        return entries;
    }
}
