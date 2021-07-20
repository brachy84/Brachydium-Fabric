package brachy84.brachydium.mixin;

import brachy84.brachydium.api.util.NbtFormatter;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract boolean hasNbt();

    @Shadow private NbtCompound nbt;

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    public void tooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        if(context.isAdvanced() && hasNbt()) {
            List<Text> list = cir.getReturnValue();
            list.add(new LiteralText(" - NBT tag - ").formatted(Formatting.AQUA));
            for(String line : NbtFormatter.format(nbt)) {
                list.add(new LiteralText(line).formatted(Formatting.DARK_GRAY));
            }
            //list.add(new LiteralText(tag.asString()).formatted(Formatting.DARK_GRAY));
            cir.setReturnValue(list);
        }
    }
}
