package brachy84.brachydium.gui.wrapper;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.api.IUIHolder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

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
            public Text getDisplayName() { return new LiteralText(""); }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new ModularScreenHandler(syncId, uiHolder, player);
            }
        };
    }

    private IUIHolder uiHolder;
    private PlayerEntity player;
    private ModularGui gui;

    public ModularScreenHandler(int syncId, IUIHolder uiHolder, PlayerEntity player) {
        super(MODULAR_SCREEN_HANDLER, syncId);
        if(uiHolder == null) {
            throw new NullPointerException("UIHolder can't be null");
        }
        this.uiHolder = uiHolder;
        this.gui = uiHolder.createUi(player);

        gui.initWidgets();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        // TODO maybe implement some sort of security
        return true;
    }

    public IUIHolder getUiHolder() {
        return uiHolder;
    }

    public ModularGui getGui() {
        return gui;
    }
}
