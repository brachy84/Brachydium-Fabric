package brachy84.brachydium.api.util;

import net.minecraft.util.math.Direction;

public enum Face {

    FRONT, BACK, TOP, BOTTOM, LEFT, RIGHT;

    public static final Face[] HORIZONTAL = {
            FRONT, RIGHT, BACK, LEFT
    };

    /**
     * @return relative direction to front
     */
    public static Face getFace(Direction dir, Direction front) {
        if(dir == front) {
            return FRONT;
        } else if(dir.getOpposite() == front) {
            return BACK;
        } else if(dir == Direction.DOWN) {
            return BOTTOM;
        } else if(dir == Direction.UP) {
            return TOP;
        } else if(dir.rotateYClockwise() == front) {
            return RIGHT;
        } else if(dir.rotateYCounterclockwise() == front) {
            return LEFT;
        }
        throw new NullPointerException("Unable to get Face from Directions");
    }

    public Direction getDirection(Direction front) {
        switch (this) {
            case FRONT: return front;
            case BACK:  return front.getOpposite();
            case TOP:   return Direction.UP;
            case BOTTOM:return Direction.DOWN;
            case LEFT:  return front.rotateYClockwise();
            case RIGHT: return front.rotateYCounterclockwise();
        }
        return null;
    }

    public Face getOpposite() {
        switch (this) {
            case FRONT: return BACK;
            case BACK:  return FRONT;
            case TOP:   return BOTTOM;
            case BOTTOM:return TOP;
            case LEFT:  return RIGHT;
            case RIGHT: return LEFT;
        }
        return null;
    }
}
