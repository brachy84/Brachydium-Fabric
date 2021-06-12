package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.api.render.models.BucketModel;
import brachy84.brachydium.api.render.models.MaterialItemModel;
import brachy84.brachydium.api.tag.TagDictionary;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import org.jetbrains.annotations.Nullable;

public class VariantProvider implements ModelVariantProvider {

    private final ResourceManager manager;

    public VariantProvider(ResourceManager manager) {
        this.manager = manager;
    }

    @Override
    public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
        if(modelId.getNamespace().equals(Brachydium.MOD_ID)) {
            if(modelId.getPath().endsWith("_bucket")) {
                Brachydium.LOGGER.info("Loading model: " + modelId);
                return new SimpleUnbaked<>(BucketModel::new);
            }
            if(modelId.getPath().startsWith("item/material")) {
                String[] parts = modelId.getPath().split("\\.")[1].split("_");
                return MaterialItemModel.simple(Material.REGISTRY.getEntry(parts[1]), TagDictionary.getEntry(parts[0]));
                //return new MaterialItemModel(Material2.REGISTRY.getEntry(parts[1]), TagDictionary.getEntry(parts[0]));
            }
        }
        return null;
    }
}