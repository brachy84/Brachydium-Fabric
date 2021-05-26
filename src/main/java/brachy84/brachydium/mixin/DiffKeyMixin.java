package brachy84.brachydium.mixin;

import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.DiffKey;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DiffKey.class)
public class DiffKeyMixin<T> extends ObjectKeyImpl<T> {

    @Inject(method = "onApply", at = @At("HEAD"), remap = false)
    public void onApplyMixin(Transaction transaction, CallbackInfo ci) {
        if (this.onApply != null) {
            this.onApply.run();
        }
    }
}
