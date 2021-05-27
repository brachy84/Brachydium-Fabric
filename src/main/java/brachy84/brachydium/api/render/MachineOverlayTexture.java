package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import net.minecraft.util.Identifier;

public class MachineOverlayTexture {

    private String machineName;
    private final Texture top, front, side, top_active, front_active, side_active;

    public MachineOverlayTexture(String machineName) {
        this(Brachydium.MOD_ID, machineName);
    }

    public MachineOverlayTexture(String mod, String machineName) {
        this.top = new Texture(new Identifier(mod, "block/machines/" + machineName + "/overlay_top"));
        this.front = new Texture(new Identifier(mod, "block/machines/" + machineName + "/overlay_front"));
        this.side = new Texture(new Identifier(mod, "block/machines/" + machineName + "/overlay_side"));
        this.top_active = new Texture(new Identifier(mod, "block/machines/" + machineName + "/overlay_top_active"));
        this.front_active = new Texture(new Identifier(mod, "block/machines/" + machineName + "/overlay_front_active"));
        this.side_active = new Texture(new Identifier(mod, "block/machines/" + machineName + "/overlay_top_side_active"));
    }

    public Texture getTop(boolean active) {
        return active ? top_active : top;
    }

    public Texture getFront(boolean active) {
        return active ? front_active : front;
    }

    public Texture getSide(boolean active) {
        return active ? side_active : side;
    }
}
