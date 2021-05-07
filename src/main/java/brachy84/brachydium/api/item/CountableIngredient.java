package brachy84.brachydium.api.item;

import brachy84.brachydium.api.tag.Tags;
import me.shedaniel.rei.api.EntryStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;

import java.util.List;

public class CountableIngredient {
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

    public static CountableIngredient of(String name, int amount) {
        Tag<Item> tag = Tags.of(name);
        if(tag == null) {
            return EMPTY;
        }
        return new CountableIngredient(Ingredient.fromTag(tag), amount);
    }

    public boolean isEmpty() {
        return amount == 0 || ingredient == null;
    }

    /*public boolean matches(CountableIngredient countableIngredient) {
        return amount == countableIngredient.amount &&
                Util.equalsIngredient(ingredient, countableIngredient.ingredient);
    }*/

    public boolean matches(ItemStack stack) {
        return amount == stack.getCount() && ingredient.test(stack);
    }

    /**
     * @param stack
     * @return true if ingredient has stack and at least amount
     */
    public boolean contains(ItemStack stack) {
        boolean matches = false;
        matches = ingredient.test(stack);
        return amount <= stack.getCount() && matches;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getAmount() {
        return amount;
    }
/*
    @Override
    public String toString() {
        return "CountableIngredient {" +
                "  ingredient=" + Util.stacksToString(ingredient.getMatchingStacksClient()) +
                ", amount=" + amount +
                '}';
    }*/

    public ItemStack toItemStack() {
        ItemStack stack = ingredient.getMatchingStacksClient()[0];
        stack.setCount(amount);
        return stack;
    }

    public List<EntryStack> toEntryStack() {
        List<EntryStack> entries = EntryStack.ofIngredient(ingredient);
        for (EntryStack entry : entries) {
            entry.setAmount(amount);
        }
        return entries;
    }
}
