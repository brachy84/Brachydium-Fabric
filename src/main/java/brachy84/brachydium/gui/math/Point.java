package brachy84.brachydium.gui.math;

import java.util.Objects;

public class Point {

    public static final Point ZERO = new Point(0f, 0f);

    private float x, y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y) {
        this((float) x, (float) y);
    }

    public static Point cartesian(float x, float y) {
        return new Point(x, y);
    }

    public static Point polar(float angle, float length) {
        float sin = (float) Math.sin(Math.toRadians(angle));
        float cos = (float) Math.cos(Math.toRadians(angle));
        return new Point(cos * length, sin * length);
    }

    public Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    public Point subtract(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    public void move(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void translate(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public double distance(Point p) {
        float x = Math.max(this.x - p.x, p.x - this.x);
        float y = Math.max(this.y - p.y, p.y - this.y);
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public float angle(Point p) {
        float x = this.x - p.x;
        float y = this.y - p.y;
        return (float) Math.toDegrees(Math.atan(y / x)) + 90;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public me.shedaniel.math.Point toReiPoint() {
        return new me.shedaniel.math.Point(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Float.compare(point.x, x) == 0 && Float.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
