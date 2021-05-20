package brachy84.brachydium.gui.math;

import me.shedaniel.math.Rectangle;

import java.util.Objects;

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
        return AABB.ltwh(point.getX(), point.getY(), size.width, size.height);
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
        return new Point((x1 - x0) / 2 + x0, (y1 - y0) / 2 + y0);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AABB aabb = (AABB) o;
        return Float.compare(aabb.x0, x0) == 0 && Float.compare(aabb.x1, x1) == 0 && Float.compare(aabb.y0, y0) == 0 && Float.compare(aabb.y1, y1) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x0, x1, y0, y1);
    }

    @Override
    public String toString() {
        return "AABB{" +
                "x0=" + x0 +
                ", x1=" + x1 +
                ", y0=" + y0 +
                ", y1=" + y1 +
                '}';
    }

    public Rectangle toReiRectangle() {
        return new Rectangle((int) x0, (int) y0, (int) width, (int) height);
    }
}
