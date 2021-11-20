package brachy84.brachydium.api.gui;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.gui.api.rendering.ProgressTexture;
import brachy84.brachydium.gui.api.rendering.TextureArea;
import net.minecraft.util.Identifier;

public class GuiTextures {

    public static final TextureArea BACKGROUND = TextureArea.fullImage(id("textures/gui/base/background"));
    public static final TextureArea BORDERED_BACKGROUND = TextureArea.fullImage(id("textures/gui/base/bordered_background"));
    public static final TextureArea DISPLAY = TextureArea.fullImage(id("textures/gui/base/display"));
    public static final TextureArea SLOT = TextureArea.fullImage(id("textures/gui/base/slot"));
    public static final TextureArea FLUID_SLOT = TextureArea.fullImage(id("textures/gui/base/fluid_slot"));

    public static final ProgressTexture ARROW = ProgressTexture.of(TextureArea.fullImage(id("textures/gui/progress_bar/arrow")));

    private static Identifier id(String path) {
        return Brachydium.id(path);
    }
}
