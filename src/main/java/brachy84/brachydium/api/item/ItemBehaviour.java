package brachy84.brachydium.api.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ItemBehaviour {

    /**
     * @param stack    that clicks
     * @param target   that got clicked
     * @param attacker who clicks
     * @return if other actions should be cancelled
     */
    default boolean onLeftClickEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return false;
    }

    /*default ActionResult onItemUseFirst(PlayerEntity player, World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, Hand hand) {
        return ActionResult.PASS;
    }*/

    default ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    default ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ActionResult.PASS;
    }

    default TypedActionResult<ItemStack> onUse(World world, PlayerEntity player, Hand hand) {
        return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
    }

    /**
     * Adds tooltip information
     *
     * @param stack   current stack
     * @param world   current world
     * @param tooltip tooltip
     * @param context context
     */
    default void addInformation(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    }

    default void onInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    }

    default void onUsageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    }

    default Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return ImmutableMultimap.of();
    }
}
