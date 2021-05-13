package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.ModularGui;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public interface IUIHolder {

    boolean hasUI();

    @NotNull
    ModularGui createUi(PlayerEntity player);

}
