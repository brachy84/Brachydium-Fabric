package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.math.Size;
import net.minecraft.util.Identifier;

public class TextureArea {

    private final Identifier path;
    private final Size imageSize;
    public final float u0, v0, u1, v1;

    public TextureArea(Identifier path, Size imageSize, float u0, float v0, float u1, float v1) {
        this.path = path;
        this.imageSize = imageSize;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
    }

    public static TextureArea fullImage(Identifier path, Size imageSize) {
        return new TextureArea(path, imageSize, 0, 0, 1, 1);
    }

    public static TextureArea of(Identifier path, Size imageSize, float u, float v) {
        return new TextureArea(path, imageSize, u, v, 1, 1);
    }
    public static TextureArea of(Identifier path, Size imageSize, float u0, float v0, float u1, float v1) {
        return new TextureArea(path, imageSize, u0, v0, u1, v1);
    }


    public TextureArea getSubArea(float u, float v) {
        return TextureArea.of(path, imageSize, u0 + u0 * u, v0 + v0 * v);
    }

    public TextureArea getSubArea(float u0, float v0, float u1, float v1) {
        return TextureArea.of(path, imageSize, calcUV0(this.u0, u0), calcUV0(this.v0, v0), this.u1 * u1, this.v1 * v1);
    }

    public Identifier getPath() {
        return path;
    }

    public Size getImageSize() {
        return imageSize;
    }

    private float calcUV0(float oldV, float newV) {
        if(oldV == 0) {
            return oldV + newV;
        }
        return oldV + oldV * newV;
    }
}
