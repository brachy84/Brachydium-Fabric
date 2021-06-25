package brachy84.brachydium.gui.math;

public record Size(float width, float height) {

    public static final Size ZERO = new Size(0, 0);

    /**
     * @param size to center
     * @return the point of the top left corner
     */
    public Point getCenteringPointForChild(Size size) {
        return new Point((width - size.width) / 2, (height - size.height) / 2);
    }
}
