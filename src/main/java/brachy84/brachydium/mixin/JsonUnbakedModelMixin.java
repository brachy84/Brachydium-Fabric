package brachy84.brachydium.mixin;

import brachy84.brachydium.Brachydium;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SpriteAtlasTexture.class)
public abstract class JsonUnbakedModelMixin {

    @Shadow @Final private Map<Identifier, Sprite> sprites;

    @Inject(method = "getSprite", at = @At("RETURN"), cancellable = true)
    public void getSprite(Identifier id, CallbackInfoReturnable<Sprite> cir) {
        Sprite sprite = cir.getReturnValue();
        if(id.getPath().endsWith("_overlay") && sprite.getId().equals(MissingSprite.getMissingSpriteId())) {
            Sprite voidSprite = this.sprites.get(Brachydium.id("item/void")); //this.sprites.get(ModelLoader.WATER_OVERLAY.getTextureId());
            Brachydium.LOGGER.info(" --- Overlay texture not found. Using void texture. {}", voidSprite != null);
            if(voidSprite != null) {
                cir.setReturnValue(voidSprite);
            }
        }
    }
}
