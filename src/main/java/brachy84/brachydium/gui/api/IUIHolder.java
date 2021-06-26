package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.ModularGui;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public interface IUIHolder {

    /**
     * if this returns false then {@link #createUi(PlayerEntity)} can return null
     * @return if the holder has a ui
     * if it returns true, it will call {@link #createUi(PlayerEntity)}
     */
    boolean hasUI();

    @NotNull
    ModularGui createUi(PlayerEntity player);

}
