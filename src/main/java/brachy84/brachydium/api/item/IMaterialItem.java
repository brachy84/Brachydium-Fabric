package brachy84.brachydium.api.item;

import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.material.MaterialRegistry;
import brachy84.brachydium.api.unification.material.Materials;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public interface IMaterialItem {

    default Material getMaterial(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if(nbt == null || !nbt.contains("Material"))
            return Materials.Neutronium;
        String material = nbt.getString("Material");
        if(material.startsWith("$"))
            return Materials.Neutronium;
        return MaterialRegistry.MATERIAL_REGISTRY.tryGetEntry(material);
    }

    default void writeMaterial(Material material, ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if(nbt == null) {
            nbt = new NbtCompound();
        }
        if (nbt.contains("Material"))
            throw new IllegalStateException("MaterialItem already has a material");
        nbt.putString("Material", material.toString());
        stack.setNbt(nbt);
    }
}
