package brachy84.brachydium.gui;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.gui.impl.Sprite;
import brachy84.brachydium.gui.math.Size;
import net.minecraft.util.Identifier;

public class Sprites {

    public static final Sprite BACKGROUND = new Sprite(id("textures/gui/base/background"), new Size(176, 166));
    public static final Sprite BORDERED_BACKGROUND = new Sprite(id("textures/gui/base/bordered_background"), new Size(195, 136));
    public static final Sprite DISPLAY = new Sprite(id("textures/gui/base/display"), new Size(143, 75));
    public static final Sprite SLOT = new Sprite(id("textures/gui/base/slot"), new Size(18, 18));
    public static final Sprite FLUID_SLOT = new Sprite(id("textures/gui/base/fluid_slot"), new Size(18, 18));

    private static Identifier id(String path) {
        return Brachydium.id(path);
    }
}
