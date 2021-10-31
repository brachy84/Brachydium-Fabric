package brachy84.brachydium.mixin;

import brachy84.brachydium.Brachydium;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Set;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    public Set<SpriteIdentifier> registerVoidTexture(Set<SpriteIdentifier> sprites) {
        Brachydium.LOGGER.info(" --- Registering Void texture");
        sprites.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Brachydium.id("item/void")));
        return sprites;
    }
}
