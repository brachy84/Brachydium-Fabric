package brachy84.brachydium.api.material;

import brachy84.brachydium.api.block.MaterialBlock;
import brachy84.brachydium.api.item.MaterialItem;
import brachy84.brachydium.api.resource.RRPHelper;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class MaterialFlag {

    public static final List<String> ALL = new ArrayList<>();

    private String name;
    //private Map<String, MaterialItem> items = new HashMap<>();

    public MaterialFlag(String name) {
        this.name = name;
        registerFlag(this);
    }

    private void registerFlag(MaterialFlag flag) {
        if(ALL.contains(flag.name)) {
            Material.LOGGER.fatal("Can't register MaterialFlag with name \"" + flag.name + "\" as it already exists. (Skipping)");
            return;
        }
        ALL.add(flag.name);
    }

    public static List<String> getFlagNames() {
        return Collections.unmodifiableList(ALL);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public void register(Material material) {}

    public void registerRecipes() {}

    public void addItem(String name, Material material) {
        MaterialItem item = new MaterialItem(name, material);
        material.registerComponent(name, item);
        //items.put(name, item);
        item.register();
        RRPHelper.addBasicMaterialItemModel(material.getName(), name);
        RRPHelper.addSimpleMaterialItemTag(material.getName(), name);
        //ResourcePack.addBasicMaterialItemModel(material.getName(), name);
        //ResourcePack.addSimpleMaterialItemTag(material.getName(), name);
    }

    public void addBlock(String name, Material material) {
        if(material.getBlockSettings() == null) {
            material.setBlockSettings(FabricBlockSettings.of(net.minecraft.block.Material.METAL)
                    .hardness(6f)
                    .resistance(5f));
        }
        MaterialBlock block = new MaterialBlock(name, material);
        block.register();
        RRPHelper.addBasicMaterialBlockState(material.getName(), name);
        RRPHelper.addBasicMaterialBlockItemModel(material.getName(), name);
        RRPHelper.addSimpleMaterialItemTag(material.getName(), name);
        RRPHelper.addSimpleLootTable("material/" + material.getName() + "." + name);
    }

    //public MaterialItem get(String name) {
    //    return items.get(name);
    //}

    public MaterialFlag[] getRequiredFlags() {return new MaterialFlag[0];}

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialFlag that = (MaterialFlag) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static class Builder {

        private String name;
        private BiConsumer<MaterialFlag, Material> registry;
        private MaterialFlag[] requiredFlags;

        private Builder(String name) {
            this.name = name;
        }

        public Builder register(BiConsumer<MaterialFlag, Material> consumer) {
            this.registry = consumer;
            return this;
        }

        public Builder requiredFlags(MaterialFlag... flags) {
            this.requiredFlags = flags;
            return this;
        }

        public MaterialFlag build() {
            return new MaterialFlag(name) {
                @Override
                public void register(Material material) {
                    super.register(material);
                    registry.accept(this, material);
                }

                @Override
                public MaterialFlag[] getRequiredFlags() {
                    return requiredFlags != null ? requiredFlags : super.getRequiredFlags();
                }
            };
        }
    }
}
