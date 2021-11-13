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
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BrachydiumItem extends Item {

    private static final Map<String, BrachydiumItem> ITEMS = new HashMap<>();

    @Nullable
    public static BrachydiumItem get(String name) {
        return ITEMS.get(name);
    }

    @ApiStatus.Internal
    public static void registerItems() {
        ITEMS.values().forEach(brachydiumItem -> Registry.register(Registry.ITEM, brachydiumItem.id, brachydiumItem));
    }

    private final Identifier id;
    private final List<ItemBehaviour> behaviours = new ArrayList<>();

    protected BrachydiumItem(Identifier id, Settings settings) {
        super(settings);
        this.id = Objects.requireNonNull(id);
        ITEMS.put(id.getPath(), this);
    }

    public BrachydiumItem appendBehaviour(ItemBehaviour behaviour) {
        this.behaviours.add(Objects.requireNonNull(behaviour));
        return this;
    }

    public String getRegistryName() {
        return id.getPath();
    }

    public Identifier getId() {
        return id;
    }

    public ItemStack asStack() {
        return asStack(1);
    }

    public ItemStack asStack(int amount) {
        return new ItemStack(this, amount);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        for (ItemBehaviour behaviour : behaviours) {
            TypedActionResult<ItemStack> behaviourResult = behaviour.onUse(world, user, hand);
            user.setStackInHand(hand, behaviourResult.getValue());
            ItemStack stack = user.getStackInHand(hand);
            if (behaviourResult.getResult() != ActionResult.PASS) {
                return new TypedActionResult<>(behaviourResult.getResult(), stack);
            } else if (stack.isEmpty()) {
                return TypedActionResult.pass(ItemStack.EMPTY);
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        for (ItemBehaviour behaviour : behaviours) {
            ActionResult result = behaviour.useOnBlock(context);
            if (result.isAccepted()) {
                return result;
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        for (ItemBehaviour behaviour : behaviours) {
            ActionResult result = behaviour.useOnEntity(stack, user, entity, hand);
            if (result.isAccepted()) {
                return result;
            }
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        for (ItemBehaviour behaviour : behaviours) {
            behaviour.addInformation(stack, world, tooltip, context);
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        for (ItemBehaviour behaviour : behaviours) {
            behaviour.onInventoryTick(stack, world, entity, slot, selected);
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        for (ItemBehaviour behaviour : behaviours) {
            behaviour.onUsageTick(world, user, stack, remainingUseTicks);
        }
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        for (ItemBehaviour behaviour : behaviours) {
            builder.putAll(behaviour.getAttributeModifiers(slot));
        }
        return builder.build();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        for (ItemBehaviour behaviour : behaviours) {
            if (behaviour.onLeftClickEntity(stack, target, attacker)) {
                return true;
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public String getTranslationKey() {
        return id.getNamespace() + ".item." + id.getPath() + ".name";
    }

    /**
     * Called when the cursor clicks on this stack
     *
     * @param stack                this stack
     * @param cursorStack          current cursor Stack
     * @param slot                 slot the stack is currently in
     * @param clickType            click type
     * @param player               clicking player
     * @param cursorStackReference cursor stack reference
     * @return false if successfull ?????
     */
    @Override
    public boolean onClicked(ItemStack stack, ItemStack cursorStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        return super.onClicked(stack, cursorStack, slot, clickType, player, cursorStackReference);
    }
}
