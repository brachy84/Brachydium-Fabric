package brachy84.brachydium.gui.wrapper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;

public class ModularScreenHandler extends ScreenHandler {

    protected ModularScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return false;
    }
}
