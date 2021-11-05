package brachy84.brachydium.api.util;

public class MathUtil {

    public static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    public static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public static long clamp(long v, long min, long max) {
        return Math.max(min, Math.min(max, v));
    }

    public static boolean isInRange(long v, long min, long max) {
        return v >= min && v <= max;
    }

    public static boolean isInRange(double v, double min, double max) {
        return v >= min && v <= max;
    }
}
