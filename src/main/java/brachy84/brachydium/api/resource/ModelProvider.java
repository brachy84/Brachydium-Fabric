package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.render.MbeHolderModel;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModelProvider implements ModelResourceProvider {

    public static final MbeHolderModel META_BLOCK_ENTITY_MODEL = new MbeHolderModel();

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier identifier, ModelProviderContext modelProviderContext) {
        if(identifier.getNamespace().equals(Brachydium.MOD_ID)) {
            if(identifier.getPath().equals("block/generic_mbe") || identifier.getPath().startsWith("item/mbe/")) {
                return META_BLOCK_ENTITY_MODEL;
            }
        }
        if(identifier.equals(Brachydium.id("block/generic_mbe")))
            return META_BLOCK_ENTITY_MODEL;
        return null;
    }
}
