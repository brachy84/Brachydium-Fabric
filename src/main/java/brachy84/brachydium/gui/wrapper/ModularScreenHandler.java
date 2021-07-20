package brachy84.brachydium.gui.wrapper;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.api.IUIHolder;
import brachy84.brachydium.gui.api.ResourceSlotWidget;
import brachy84.brachydium.gui.api.SlotTags;
import brachy84.brachydium.gui.widgets.ItemSlotWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ModularScreenHandler extends ScreenHandler {

    public final static ScreenHandlerType<ModularScreenHandler> MODULAR_SCREEN_HANDLER;

    static {
        MODULAR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(Brachydium.id("shitty_ass_screen_handler_type_shit_crack_nightmare"), (syncId, inv) -> {
            return new ModularScreenHandler(syncId, UIFactory.getCachedHolder(syncId), inv.player);
        });
    }

    public static NamedScreenHandlerFactory createFactory(IUIHolder uiHolder) {
        return new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return new LiteralText("");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new ModularScreenHandler(syncId, uiHolder, player);
            }
        };
    }

    private final IUIHolder uiHolder;
    private final PlayerEntity player;
    private final ModularGui gui;
    private boolean requiresUpate = false;

    private long lastCursorSet;
    private ItemStack lastCursorStack = ItemStack.EMPTY;

    public ModularScreenHandler(int syncId, IUIHolder uiHolder, PlayerEntity player) {
        super(MODULAR_SCREEN_HANDLER, syncId);
        Brachydium.LOGGER.info("Opening UI with " + player.getClass().getCanonicalName());
        if (uiHolder == null) {
            throw new NullPointerException("UIHolder can't be null");
        }
        if (player == null) {
            throw new NullPointerException("Player can't be null");
        }
        this.player = player;
        this.uiHolder = uiHolder;
        this.gui = uiHolder.createUi(player);

        gui.open();
        for(Slot slot : gui.getAndClearSlots()) {
            addSlot(slot);
        }
        lastCursorSet = Util.getMeasuringTimeMs();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        // TODO maybe implement some sort of security
        return player.squaredDistanceTo(this.player) < 64D;
    }

    public IUIHolder getUiHolder() {
        return uiHolder;
    }

    public ModularGui getGui() {
        return gui;
    }

    @Override
    public void sendContentUpdates() {
        requiresUpate = false;
        super.sendContentUpdates();
    }

    public boolean requiresUpdate() {
        return requiresUpate;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        if(getSlot(index) instanceof McSlot) {
            McSlot slot = (McSlot) getSlot(index);
            ItemSlotWidget widget = slot.getSlotWidget();

            if(widget.getTag().trim().equals("")) return slot.getStack();
            List<ResourceSlotWidget<?>> slots = gui.getSlots(slot1 -> slot1 instanceof ItemSlotWidget && !slot1.getTag().trim().equals("") && !slot1.getTag().equals(widget.getTag()));
            String[] order;
            switch (widget.getTag()) {
                case SlotTags.INPUT:
                case SlotTags.OUTPUT:
                    order = new String[]{SlotTags.PLAYER, SlotTags.HOTBAR};
                    break;
                case SlotTags.HOTBAR:
                    order = new String[]{SlotTags.INPUT, SlotTags.PLAYER};
                    break;
                case SlotTags.PLAYER:
                    order = new String[]{SlotTags.INPUT, SlotTags.HOTBAR};
                    break;
                default:
                    return slot.getStack();
            }
            ItemStack slotStack = slot.getStack();
            List<ISyncedWidget> syncQueue = new ArrayList<>();
            int toInsert = slotStack.getCount();
            Transaction transaction = Transaction.create();
            outer:
            for(String target : order) {
                for(ResourceSlotWidget<?> rslot : slots) {
                    if(!rslot.getTag().equals(target)) continue;
                    ItemSlotWidget slot1 = (ItemSlotWidget) rslot;
                    int inserted = slot1.getItemSlot().insert(transaction, ItemKey.of(slotStack), Math.min(toInsert, slotStack.getMaxCount()));
                    toInsert -= inserted;
                    if(inserted > 0) {
                        syncQueue.add(slot1);
                    }
                    if(toInsert == 0) {
                        break outer;
                    }
                }
            }
            transaction.commit();
            slot.setStack(newStack(slotStack, toInsert));
            /*for (ISyncedWidget syncedWidget: syncQueue) {
                syncedWidget.sendToClient((ServerPlayerEntity) gui.player);
            }*/
            sendContentUpdates();
        }
        return getSlot(index).getStack();
    }

    private ItemStack newStack(ItemStack stack, int amount) {
        ItemStack stack1 = stack.copy();
        stack1.setCount(amount);
        return stack1;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public void setCursorStack(ItemStack stack) {
        /*long time = Util.getMeasuringTimeMs();
        if (player == null && stack.isEmpty() && time - lastCursorSet < 100) {
            Brachydium.LOGGER.info("CursorError detected. Was modified {}ms ago.", time - lastCursorSet);
            return;
        }*/
        super.setCursorStack(stack);
        //lastCursorSet = time;

        //else side = player.getClass().getCanonicalName();
        if(RenderSystem.isOnRenderThread()) {

            Brachydium.LOGGER.info("Setting cursor to {} on client", stack);
        } else {

            Brachydium.LOGGER.info("Setting cursor to {} on server", stack);
        }

    }
}
