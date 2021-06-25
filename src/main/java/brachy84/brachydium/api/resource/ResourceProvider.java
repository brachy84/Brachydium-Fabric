package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.render.models.MbeHolderModel;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ResourceProvider implements ModelResourceProvider {

    public static final MbeHolderModel META_BLOCK_ENTITY_MODEL = new MbeHolderModel();

    private final ResourceManager manager;

    public ResourceProvider(ResourceManager manager) {
        this.manager = manager;
    }

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier identifier, ModelProviderContext modelProviderContext) {
        if(identifier.getNamespace().equals(Brachydium.MOD_ID)) {
            if(identifier.getPath().equals("block/generic_tile") || identifier.getPath().startsWith("item/tile/")) {
                return META_BLOCK_ENTITY_MODEL;
            }
        }
        if(identifier.equals(Brachydium.id("block/generic_mbe")))
            return META_BLOCK_ENTITY_MODEL;
        return null;
    }
}
