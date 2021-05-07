package brachy84.brachydium.api.item;

import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.Brachydium;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class MaterialItem extends Item {

    private String componentName;
    private Material material;
    private String translationKey;
    private Identifier id;

    private MaterialItem(Settings settings) {
        super(settings);
    }

    public MaterialItem(String componentName, Material material) {
        super(new Settings().group(ItemGroups.MATERIALS));
        this.componentName = componentName;
        this.material = material;
        this.id = new Identifier(Brachydium.MOD_ID, "material/" + material.getName() + "." + componentName );
    }

    public void register() {
        // register item
        ColorProviderRegistry.ITEM.register(((stack, tintIndex) -> {
            return material.getColor();
        }), this);
        Registry.register(Registry.ITEM, id, this);
    }

    private void registerRecipes() {

    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public Text getName(ItemStack stack) {
        return new TranslatableText(getTranslationKey(), material.translatedText().getString());
    }

    @Override
    public String getTranslationKey() {
        return "component." + componentName;
    }

    public String getComponentName() {
        return componentName;
    }

    public Material getMaterial() {
        return material;
    }

    public Identifier getId() {
        return id;
    }
}
