package brachy84.brachydium.gui.math;

/**
 * Describes an area in a gui
 */
public final class AABB {

    public final float x0, x1, y0, y1;
    public final float width, height;

    private AABB(float x0, float x1, float y0, float y1) {
        this.x0 = Math.min(x0, x1);
        this.x1 = Math.max(x0, x1);
        this.y0 = Math.min(y0, y1);
        this.y1 = Math.max(y0, y1);
        this.width = this.x1 - this.x0;
        this.height = this.y1 - this.y0;
    }

    public static AABB ofPoints(Point p0,Point p1) {
        return new AABB(p0.getX(), p0.getY(), p1.getX(), p1.getY());
    }

    public static AABB of(Size size, Point point) {
        return new AABB(point.getX(), size.width, point.getY(), size.height);
    }

    /**
     * Makes an area of the top left corner and width and height
     * left - top - width - height
     */
    public static AABB ltwh(float x, float y, float width, float height) {
        return new AABB(x, x + width, y, y + height);
    }

    public boolean isInBounds(float x, float y) {
        return x >= x0 && x <= x1 && y >= y0 && y <= y1;
    }

    public boolean isInBounds(Point point) {
        return isInBounds(point.getX(), point.getY());
    }

    public Point getCenter() {
        return new Point((x1 - x0) / 2, (y1 - y0) / 2);
    }

    public Point getTopLeft() {
        return new Point(x0, y0);
    }

    public float getArea() {
        return width * height;
    }

    public Size getSize() {
        return new Size(width, height);
    }
}
