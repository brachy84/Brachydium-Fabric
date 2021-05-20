package brachy84.brachydium.mixin;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.recipe.RecipeLoadEvent;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TagManagerLoader.class)
public class TagManagerLoaderMixin {

    @Inject(method = "reload", at = @At("TAIL"), cancellable = true)
    public void reloadMixin(ResourceReloadListener.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        CompletableFuture<Void> future = cir.getReturnValue();
        future.thenRun(() -> {
            Brachydium.LOGGER.info("reloading tags");
            RecipeLoadEvent.EVENT.invoker().load();
        });
        cir.setReturnValue(future);
    }
}
