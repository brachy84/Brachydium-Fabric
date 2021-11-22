package brachy84.brachydium.api.util;

import net.minecraft.util.math.Direction;

public enum Face {

    FRONT("front"),
    BACK("back"),
    TOP("top"),
    BOTTOM("bottom"),
    LEFT("left"),
    RIGHT("right"),
    SIDE("side");

    public static final Face[] HORIZONTAL = {
            FRONT, RIGHT, BACK, LEFT
    };

    public final String name;

    Face(String name) {
        this.name = name;
    }

    /**
     * @return relative direction to front
     */
    public static Face getFace(Direction dir, Direction front) {
        if (dir == front) {
            return FRONT;
        } else if (dir.getOpposite() == front) {
            return BACK;
        } else if (dir == Direction.DOWN) {
            return BOTTOM;
        } else if (dir == Direction.UP) {
            return TOP;
        } else if (dir.rotateYClockwise() == front) {
            return RIGHT;
        } else if (dir.rotateYCounterclockwise() == front) {
            return LEFT;
        }
        throw new NullPointerException("Unable to get Face from Directions");
    }

    public Direction getDirection(Direction front) {
        return switch (this) {
            case FRONT -> front;
            case BACK -> front.getOpposite();
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case LEFT -> front.rotateYClockwise();
            case RIGHT -> front.rotateYCounterclockwise();
            default -> null;
        };
    }

    public Face getOpposite() {
        return switch (this) {
            case FRONT -> BACK;
            case BACK -> FRONT;
            case TOP -> BOTTOM;
            case BOTTOM -> TOP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            default -> null;
        };
    }
}
