package brachy84.brachydium.gui.math;

public class Color {

    private byte r, g, b, a;

    public Color(byte red, byte green, byte blue, byte alpha) {
        this.r = red;
        this.g = green;
        this.b = blue;
        this.a = alpha;
    }

    public static Color of(int red, int green, int blue, int alpha) {
        return new Color((byte)red, (byte)green, (byte)blue, (byte)alpha);
    }

    public static Color of(int red, int green, int blue) {
        return of(red, green, blue, 255);
    }

    public static Color of(int color) {
        byte a = (byte) (color >> 24 & 255);
        byte r = (byte) (color >> 16 & 255);
        byte g = (byte) (color >> 8 & 255);
        byte b = (byte) (color & 255);
        return new Color(r, g, b, a);
    }

    public Color withRed(byte r) {
        return new Color(r, g, b, a);
    }

    public Color withBlue(byte b) {
        return new Color(r, g, b, a);
    }

    public Color withGreen(byte g) {
        return new Color(r, g, b, a);
    }

    public Color withAlpha(byte a) {
        return new Color(r, g, b, a);
    }

    public Color withOpacity(double opacity) {
        byte a = (byte) (255 * opacity);
        return new Color(r, g, b, a);
    }

    public byte getRed() {
        return r;
    }

    public byte getGreen() {
        return g;
    }

    public byte getBlue() {
        return b;
    }

    public byte getAlpha() {
        return a;
    }
}
