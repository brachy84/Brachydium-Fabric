package brachy84.brachydium.api.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Util {

    public static Item getItem(String mod, String name) {
        return Registry.ITEM.get(new Identifier(mod, name));
    }

    public static ItemStack getItemStack(String mod, String name, int amount) {
        return new ItemStack(getItem(mod, name), amount);
    }

}
