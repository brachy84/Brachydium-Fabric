package brachy84.brachydium.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow private NbtCompound tag;

    @Shadow public abstract boolean hasTag();

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    public void tooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        if(context.isAdvanced() && hasTag()) {
            List<Text> list = cir.getReturnValue();
            list.add(new LiteralText(tag.asString()));
            cir.setReturnValue(list);
        }
    }
}
