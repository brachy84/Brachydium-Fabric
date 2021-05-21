package brachy84.brachydium.gui;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.gui.api.ProgressTexture;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.math.Size;
import net.minecraft.util.Identifier;

public class GuiTextures {

    public static final TextureArea BACKGROUND = TextureArea.fullImage(id("textures/gui/base/background"), new Size(176, 166));
    public static final TextureArea BORDERED_BACKGROUND = TextureArea.fullImage(id("textures/gui/base/bordered_background"), new Size(195, 136));
    public static final TextureArea DISPLAY = TextureArea.fullImage(id("textures/gui/base/display"), new Size(143, 75));
    public static final TextureArea SLOT = TextureArea.fullImage(id("textures/gui/base/slot"), new Size(18, 18));
    public static final TextureArea FLUID_SLOT = TextureArea.fullImage(id("textures/gui/base/fluid_slot"), new Size(18, 18));

    public static final ProgressTexture ARROW = ProgressTexture.of(TextureArea.fullImage(id("textures/gui/progress_bar/arrow"), new Size(20, 40)));

    private static Identifier id(String path) {
        return Brachydium.id(path);
    }
}
