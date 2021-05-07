package brachy84.brachydium.api.util;

import brachy84.brachydium.api.handlers.FluidStack;
import brachy84.brachydium.api.item.CountableIngredient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class Util {

    public static boolean equalsItemList(List<ItemStack> list1, List<ItemStack> list2) {
        if(list1 == null || list2 == null) {
            return false;
        }
        if(list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if(!list1.get(i).isItemEqual(list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsIngredientList(List<Ingredient> list1, List<Ingredient> list2) {
        if(list1 == null || list2 == null) {
            return false;
        }
        if(list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if(!equalsIngredient(list1.get(i),list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    /*public static boolean equalsFluidList(List<FluidStack> list1, List<FluidStack> list2) {
        if(list1 == null || list2 == null) {
            return false;
        }
        if(list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if(!list1.get(i).matches(list2.get(i))) {
                return false;
            }
        }
        return true;
    }*/

    public static boolean equalsIngredient(Ingredient i1, Ingredient i2) {
        return i1.getIds().equals(i2.getIds());
    }

    /**
     *
     * @param countableIngredients
     * @param stacks
     * @return true if stacks has all of countable Ingredients
     */
    public static boolean contains(List<CountableIngredient> countableIngredients, List<ItemStack> stacks) {
        for(CountableIngredient c : countableIngredients) {
            //System.out.println(c.toString()); //FIXME
            int i = 0;
            for (ItemStack s : stacks) {
                //printStack(s);
                if(c.contains(s)) {
                    break;
                }
                if(i == stacks.size()-1) {
                    return false;
                }
                i++;
            }
        }
        return true;
    }

    public static ItemStack getStack(Item item, int count) {
        return new ItemStack(item, count);
    }

    public static Item getItem(String mod, String name) {
        return Registry.ITEM.get(new Identifier(mod, name));
    }

    public static ItemStack getItemStack(String mod, String name, int amount) {
        return new ItemStack(getItem(mod, name), amount);
    }

}
