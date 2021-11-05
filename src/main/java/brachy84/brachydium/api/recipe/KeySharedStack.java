package brachy84.brachydium.api.recipe;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class KeySharedStack {

    private static final WeakHashMap<ItemVariant, WeakReference<ItemVariant>> registeredItemStackKeys = new WeakHashMap<>();

    private KeySharedStack() {

    }

    public static synchronized ItemVariant getRegisteredStack(final @NotNull ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            throw new IllegalArgumentException("stack cannot be empty");
        }

        int oldStackSize = itemStack.getCount();
        itemStack.setCount(1);

        ItemVariant search = ItemVariant.of(itemStack);
        WeakReference<ItemVariant> weak = registeredItemStackKeys.get(search);
        ItemVariant ret = null;

        if (weak != null) {
            ret = weak.get();
        }

        if (ret == null) {
            ret = ItemVariant.of(itemStack);
            registeredItemStackKeys.put(ret, new WeakReference<>(ret));
        }
        itemStack.setCount(oldStackSize);

        return ret;
    }
}
