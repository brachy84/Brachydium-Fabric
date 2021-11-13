package brachy84.brachydium.mixin;

import brachy84.brachydium.api.item.tool.IToolItem;
import brachy84.brachydium.api.util.ITagHolder;
import brachy84.brachydium.api.util.NbtFormatter;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract boolean hasNbt();

    @Shadow
    private NbtCompound nbt;

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    private boolean empty;

    @Shadow
    public abstract void setCount(int count);

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    public void tooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        if (context.isAdvanced() && hasNbt()) {
            List<Text> list = cir.getReturnValue();
            for (Identifier tag : ((ITagHolder) getItem()).getTags()) {
                list.add(new LiteralText("#" + tag.toString()).formatted(Formatting.DARK_GRAY));
            }
            list.add(new LiteralText(" - NBT tag - ").formatted(Formatting.AQUA));
            for (String line : NbtFormatter.format(nbt)) {
                list.add(new LiteralText(line).formatted(Formatting.DARK_GRAY));
            }
            //list.add(new LiteralText(tag.asString()).formatted(Formatting.DARK_GRAY));
            cir.setReturnValue(list);
        }
    }

    @Inject(method = "isDamageable", at = @At("HEAD"), cancellable = true)
    public void isDamagable(CallbackInfoReturnable<Boolean> cir) {
        if (getItem() instanceof IToolItem && !empty && (nbt == null || !nbt.getBoolean("Unbreakable")))
            cir.setReturnValue(true);
    }

    @Inject(method = "getDamage", at = @At("HEAD"), cancellable = true)
    public void getDamage(CallbackInfoReturnable<Integer> cir) {
        if (getItem() instanceof IToolItem toolItem)
            cir.setReturnValue(toolItem.getItemDamage((ItemStack) (Object) this));
    }

    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    public void getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        if (getItem() instanceof IToolItem toolItem)
            cir.setReturnValue(toolItem.getMaxItemDamage((ItemStack) (Object) this));
    }

    @Inject(method = "setDamage", at = @At("HEAD"), cancellable = true)
    public void setDamage(int damage, CallbackInfo ci) {
        if (getItem() instanceof IToolItem toolItem) {
            toolItem.setDamage((ItemStack) (Object) this, damage);
            if (damage >= toolItem.getMaxItemDamage((ItemStack) (Object) this) - 1 && !empty) {
                setCount(0);
            }
            ci.cancel();
        }
    }
}
