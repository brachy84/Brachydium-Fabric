package brachy84.brachydium.gui.math;

public class Utils {

    public static float clamp(float v, float min, float max) {
        return Math.min(max, Math.max(v, min));
    }
}
