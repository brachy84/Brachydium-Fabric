package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class BucketModel extends DynamicFluidItemModel {

    @Override
    public ModelIdentifier getBaseModel() {
        return new ModelIdentifier(Brachydium.id("bucket_base"), "inventory");
    }

    @Override
    public ModelIdentifier getBackgroundModel() {
        return new ModelIdentifier(Brachydium.id("bucket_fluid"), "inventory");
    }

    @Override
    public ModelIdentifier getFluidModel() {
        return new ModelIdentifier(Brachydium.id("bucket_fluid"), "inventory");
    }

    @Override
    public Sprite getSprite() {
        return MinecraftClient.getInstance()
                .getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
                .apply(new Identifier("item/bucket"));
    }

    /*@Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return new BucketModel();
    }*/
}
