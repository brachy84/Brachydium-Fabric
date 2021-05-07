package brachy84.brachydium.api.render;

public class Textures {

    public static final Texture[] MACHINECASING;

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
}
