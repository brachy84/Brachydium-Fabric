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
        Material material = MaterialRegistry.MATERIAL_REGISTRY.tryGetEntry(nbt.getString("Material"));
        return material == null ? Materials.Neutronium : material;
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
