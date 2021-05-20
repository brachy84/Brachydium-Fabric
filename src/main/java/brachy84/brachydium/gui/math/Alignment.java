package brachy84.brachydium.gui.math;

public class Alignment {

    public static final Alignment TopLeft = new Alignment(-1, -1);
    public static final Alignment TopCenter = new Alignment(0, -1);
    public static final Alignment TopRight = new Alignment(1, -1);
    public static final Alignment CenterLeft = new Alignment(-1, 0);
    public static final Alignment Center = new Alignment(0, 0);
    public static final Alignment CenterRight = new Alignment(1, 0);
    public static final Alignment BottomLeft = new Alignment(-1, 1);
    public static final Alignment BottomCenter = new Alignment(0, 1);
    public static final Alignment BottomRight = new Alignment(1, 1);

    public final float x, y;

    public Alignment(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static final Alignment[] ALL = {
            TopLeft, TopCenter, TopRight,
            CenterLeft, Center, CenterRight,
            BottomLeft, BottomCenter, BottomRight
    };

    public static final Alignment[] CORNERS = {
            TopLeft, TopRight,
            BottomLeft, BottomRight
    };
}
