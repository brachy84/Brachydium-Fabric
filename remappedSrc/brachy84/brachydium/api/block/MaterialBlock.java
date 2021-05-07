package brachy84.brachydium.api.block;

import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.Brachydium;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 *  A block from a material
 *  Is not necessarily a Block like Iron Block, but also frames, casings etc..
 */
public class MaterialBlock extends Block {

    // TODO: autogen Blockstate.json

    private Material material;
    private String blockComponent;

    private MaterialBlock(Settings settings) {
        super(settings);
    }

    public MaterialBlock(String blockComponent, Material material) {
        super(material.getBlockSettings());
        this.blockComponent = blockComponent;
        this.material = material;
    }

    public void register() {



        BlockItem blockItem = new BlockItem(this, new Item.Settings().group(ItemGroups.MATERIALS)) {
            @Override
            public Text getName(ItemStack itemStack) {
                return new TranslatableText("component." + blockComponent, material.translatedText().getString());
            }
        };

        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> material.getColor(), this);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> material.getColor(), blockItem);

        Identifier blockId = new Identifier(Brachydium.MOD_ID, "material/" + material.getName() + "." + blockComponent);

        Registry.register(Registry.BLOCK, blockId, this);
        Registry.register(Registry.ITEM, blockId, blockItem);
    }

    @Override
    public MutableText getName() {
        return new TranslatableText("component." + blockComponent, material.translatedText().getString());
    }
}
