package brachy84.brachydium.api.item;

import brachy84.brachydium.Brachydium;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class BrachydiumItem extends Item {

    private static final List<Definition> ITEMS = new ArrayList<>();

    public static List<Definition> getDefinitions() {
        return Collections.unmodifiableList(ITEMS);
    }

    private final Identifier id;
    private final List<ItemBehaviour> behaviours;

    protected BrachydiumItem(Identifier id, Settings settings, List<ItemBehaviour> behaviours) {
        super(settings);
        this.id = id;
        this.behaviours = behaviours;
    }

    public static Definition create(String path) {
        return create(Brachydium.id(path));
    }

    public static Definition create(Identifier id) {
        return new Definition(id);
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
            if(behaviour.onLeftClickEntity(stack, target, attacker)) {
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
     * @param stack this stack
     * @param cursorStack current cursor Stack
     * @param slot slot the stack is currently in
     * @param clickType click type
     * @param player clicking player
     * @param cursorStackReference cursor stack reference
     * @return false if successfull ?????
     */
    @Override
    public boolean onClicked(ItemStack stack, ItemStack cursorStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        return super.onClicked(stack, cursorStack, slot, clickType, player, cursorStackReference);
    }

    public static class Definition {

        private BrachydiumItem item;

        private final Identifier id;
        private Settings settings;
        private final List<ItemBehaviour> behaviours = new ArrayList<>();

        private Definition(Identifier id) {
            this.id = id;
            ITEMS.add(this);
            this.settings = new FabricItemSettings();
        }

        public Definition buildSettings(Settings settings) {
            checkBuildable();
            this.settings = Objects.requireNonNull(settings);
            return this;
        }

        public Definition setGroup(ItemGroup group) {
            checkBuildable();
            settings.group(Objects.requireNonNull(group));
            return this;
        }

        public Definition appendBehaviour(ItemBehaviour behaviour) {
            checkBuildable();
            this.behaviours.add(Objects.requireNonNull(behaviour));
            return this;
        }

        public BrachydiumItem register() {
            item = new BrachydiumItem(id, settings, behaviours);
            return item;
        }

        public String getUnlocalizedName() {
            return id.getPath();
        }

        public ItemStack asStack() {
            return asStack(1);
        }

        public ItemStack asStack(int amount) {
            if(item == null)
                throw new IllegalStateException("Item " + id + " is not registered yet");
            return new ItemStack(item, amount);
        }

        public void checkBuildable() {
            if(item != null)
                throw new IllegalStateException("Can't edit item after registration");
        }
    }
}
