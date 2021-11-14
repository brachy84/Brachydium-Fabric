package brachy84.brachydium.api.block;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.item.MaterialItem;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import net.devtech.arrp.json.tags.JTag;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class BrachydiumBlocks {

    public static void init() {
        // material set models
        RRPHelper.generateMaterialBlockModel(TagDictionary.block);
        RRPHelper.generateMaterialBlockModel(TagDictionary.frameGt);
        RRPHelper.generateOreModels();

        JTag climbable = JTag.tag(); // FIXME

        TagDictionary.block.setGenerator(MaterialBlock::createAndRegister);
        TagDictionary.frameGt.setGenerator((tag, material) -> {
            MaterialBlock block = MaterialBlock.createAndRegister(tag, material, settings -> settings.strength(2.5f, 5).nonOpaque());
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
            climbable.add(MaterialItem.createItemId(material, tag));
        });
        Brachydium.RESOURCE_PACK.addTag(new Identifier("minecraft", "blocks/climbable"), climbable);

        TagDictionary.ore.setGenerator((tag, material) -> {
            OreBlock oreBlock = OreBlock.createAndRegister(tag, material);
        });
    }
}
