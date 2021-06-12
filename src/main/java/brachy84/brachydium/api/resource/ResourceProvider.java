package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.api.render.models.MaterialItemModel;
import brachy84.brachydium.api.render.models.MbeHolderModel;
import brachy84.brachydium.api.tag.TagDictionary;
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
            if(identifier.getPath().startsWith("item/material")) {
                String[] parts = identifier.getPath().split("\\.")[1].split("_");
                return MaterialItemModel.simple(Material.REGISTRY.getEntry(parts[1]), TagDictionary.getEntry(parts[0]));
                //return new MaterialItemModel(Material2.REGISTRY.getEntry(parts[1]), TagDictionary.getEntry(parts[0]));
            }
            if(identifier.getPath().equals("block/generic_mbe") || identifier.getPath().startsWith("item/mbe/")) {
                return META_BLOCK_ENTITY_MODEL;
            }
        }
        if(identifier.equals(Brachydium.id("block/generic_mbe")))
            return META_BLOCK_ENTITY_MODEL;
        return null;
    }
}
