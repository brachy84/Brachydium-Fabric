package brachy84.brachydium.gui.impl;

import brachy84.brachydium.gui.api.GuiHelper;
import brachy84.brachydium.gui.api.ISprite;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class Sprite implements ISprite {

    private Identifier path;
    private Size size;
    private float u, v;
    private Size drawSize;
    private int z;

    public Sprite(Identifier path, Size size) {
        this(path, size, 0, 0);
    }

    public Sprite(Identifier path, Size size, int u, int v) {
        this(path, size, u, v, size);
    }

    public Sprite(Identifier path, Size size, int u, int v, Size drawSize) {
        Identifier processedPath = path;
        if(!path.getPath().endsWith(".png")) {
            processedPath = new Identifier(path.getNamespace(), path.getPath() + ".png");
        }
        this.path = processedPath;
        this.size = size;
        this.u = u;
        this.v = v;
        this.drawSize = drawSize;
        this.z = z;
    }

    @Override
    public Identifier getPath() {
        return path;
    }

    @Override
    public Size getSize() {
        return size;
    }
}
