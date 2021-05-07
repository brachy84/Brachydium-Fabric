package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.ModularGui;
import net.minecraft.entity.player.PlayerEntity;

public interface IUIHolder {

    ModularGui createUi(PlayerEntity player);

}
