package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.energy.Voltage;
import brachy84.brachydium.api.util.Face;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Textures {

    public static final Texture[] MACHINECASING = new Texture[Voltage.VALUES.length];
    public static final Texture OUTPUT_OVERLAY = new Texture("block/overlays/output");
    public static final Texture INPUT_OVERLAY = new Texture("block/overlays/input");

    static {
        for(int i = 0; i < Voltage.VALUES.length; i++) {
            MACHINECASING[i] = new Texture("block/casings/voltage/" + Voltage.VALUES[i].shortName.toLowerCase());
        }
    }

    public static void init() {}

    private static Identifier id(String path) {
        return Brachydium.id(path);
    }
}
