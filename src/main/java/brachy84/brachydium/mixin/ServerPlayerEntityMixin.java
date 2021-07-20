package brachy84.brachydium.mixin;

import brachy84.brachydium.gui.wrapper.ModularScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;sendContentUpdates()V"))
    public void tick(ScreenHandler screenHandler) {
        screenHandler.sendContentUpdates();
        /*if(screenHandler instanceof ModularScreenHandler sh) {
            if(sh.requiresUpdate()) sh.sendContentUpdates();
        } else {
            screenHandler.sendContentUpdates();
        }*/
    }
}
