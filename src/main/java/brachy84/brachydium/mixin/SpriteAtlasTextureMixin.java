package brachy84.brachydium.mixin;

import brachy84.brachydium.Brachydium;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin {

    @Shadow
    @Final
    private Map<Identifier, Sprite> sprites;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    protected abstract Identifier getTexturePath(Identifier id);

    @Shadow
    @Final
    private Identifier id;
    private Sprite missingSprite;
    private Sprite voidSprite;

    // returns an empty texture if a texture that ends in _overlay could not be found
    @Inject(method = "getSprite", at = @At("HEAD"), cancellable = true)
    public void getSprite(Identifier id, CallbackInfoReturnable<Sprite> cir) {
        Sprite sprite = this.sprites.get(id);
        if (sprite != null) {
            cir.setReturnValue(sprite);
            return;
        }
        if (id.getPath().endsWith("_overlay")) {
            if (voidSprite == null)
                voidSprite = this.sprites.get(Brachydium.id("item/void"));
            cir.setReturnValue(voidSprite);
            return;
        }
        if (missingSprite == null)
            missingSprite = this.sprites.get(MissingSprite.getMissingSpriteId());
        cir.setReturnValue(missingSprite);

    }

    // don't spam log if it couldn't find a texture that ends in _overlay
    @Inject(method = "loadSprites(Lnet/minecraft/resource/ResourceManager;Ljava/util/Set;)Ljava/util/Collection;", at = @At("HEAD"), cancellable = true)
    public void loadSprites(ResourceManager resourceManager, Set<Identifier> ids, CallbackInfoReturnable<Collection<Sprite.Info>> cir) {
        List<CompletableFuture<?>> list = Lists.newArrayList();
        Queue<Sprite.Info> queue = new ConcurrentLinkedQueue();
        Iterator var5 = ids.iterator();

        while (var5.hasNext()) {
            Identifier identifier = (Identifier) var5.next();
            if (!MissingSprite.getMissingSpriteId().equals(identifier)) {
                list.add(CompletableFuture.runAsync(() -> {
                    Identifier identifier2 = this.getTexturePath(identifier);

                    Sprite.Info info3;
                    try {
                        Resource resource = resourceManager.getResource(identifier2);

                        try {
                            PngFile pngFile = new PngFile(resource.toString(), resource.getInputStream());
                            AnimationResourceMetadata animationResourceMetadata = (AnimationResourceMetadata) resource.getMetadata(AnimationResourceMetadata.READER);
                            if (animationResourceMetadata == null) {
                                animationResourceMetadata = AnimationResourceMetadata.EMPTY;
                            }

                            Pair<Integer, Integer> pair = animationResourceMetadata.ensureImageSize(pngFile.width, pngFile.height);
                            info3 = new Sprite.Info(identifier, (Integer) pair.getFirst(), (Integer) pair.getSecond(), animationResourceMetadata);
                        } catch (Throwable var11) {
                            if (resource != null) {
                                try {
                                    resource.close();
                                } catch (Throwable var10) {
                                    var11.addSuppressed(var10);
                                }
                            }

                            throw var11;
                        }

                        if (resource != null) {
                            resource.close();
                        }
                    } catch (RuntimeException var12) {
                        LOGGER.error("Unable to parse metadata from {} : {}", identifier2, var12);
                        return;
                    } catch (IOException var13) {
                        if (!identifier.getPath().endsWith("_overlay"))
                            LOGGER.error("Using missing texture, unable to load {} : {}", identifier2, var13);
                        return;
                    }

                    queue.add(info3);
                }, Util.getMainWorkerExecutor()));
            }
        }

        CompletableFuture.allOf((CompletableFuture[]) list.toArray(new CompletableFuture[0])).join();
        cir.setReturnValue(queue);
    }
}
