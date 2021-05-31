package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.util.Face;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Textures {

    public static final Texture[] MACHINECASING;
    public static final Texture OUTPUT_OVERLAY = new Texture("block/overlays/output");
    public static final Texture INPUT_OVERLAY = new Texture("block/overlays/input");

    public static final WorkableOverlayRenderer MIXER = new WorkableOverlayRenderer(id("block/machines/mixer"), Face.FRONT, Face.TOP, Face.SIDE);
    public static final WorkableOverlayRenderer ALLOY_SMELTER = new WorkableOverlayRenderer(id("block/machines/alloy_smelter"), Face.FRONT);

    static {
        MACHINECASING = new Texture[]{
                new Texture("block/casings/voltage/elv"),
                new Texture("block/casings/voltage/ulv"),
                new Texture("block/casings/voltage/lv"),
                new Texture("block/casings/voltage/mv"),
                new Texture("block/casings/voltage/hv"),
                new Texture("block/casings/voltage/ev"),
                new Texture("block/casings/voltage/iv"),
                new Texture("block/casings/voltage/luv"),
                new Texture("block/casings/voltage/uv"),
                new Texture("block/casings/voltage/gv"),
                new Texture("block/casings/voltage/gmv"),
                new Texture("block/casings/voltage/ghv"),
                new Texture("block/casings/voltage/gev"),
                new Texture("block/casings/voltage/giv"),
                new Texture("block/casings/voltage/ugv"),
                new Texture("block/casings/voltage/max")
        };
    }

    public static void init() {}

    private static Identifier id(String path) {
        return Brachydium.id(path);
    }
}
