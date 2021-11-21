package brachy84.brachydium.api.render;

import brachy84.brachydium.gui.api.math.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

import java.util.List;

/**
 * Implement this on a Block class to get a outline render callback
 */
public interface ICustomOutlineRender {

    /**
     * Render extra block outline
     *
     * @return true if the default outline should be rendered too
     */
    @Environment(EnvType.CLIENT)
    boolean renderOutline(MatrixStack matrices, VertexConsumer vertexConsumer, double camX, double camY, double camZ, BlockState state, BlockPos pos);

    /**
     * A helper method to render Edges that can be transformed
     * Supposed to be called from {@link #renderOutline(MatrixStack, VertexConsumer, double, double, double, BlockState, BlockPos)}
     */
    default void drawEdges(List<Edge> edges, MatrixStack matrices, VertexConsumer vertexConsumer, double camX, double camY, double camZ, Vec3i pos) {
        MatrixStack.Entry entry = matrices.peek();
        double drawX = pos.getX() - camX, drawY = pos.getY() - camY, drawZ = pos.getZ() - camZ;
        edges.forEach(edge -> {
            edge.render(entry, vertexConsumer, drawX, drawY, drawZ);
        });
    }

    /**
     * A helper class to render lines, which can also be transformed to other sides;
     */
    class Edge {
        private double startX = 0D, startY = 0D, startZ = 0D, endX = 0D, endY = 0D, endZ = 0D;
        private float red = 0f, green = 0f, blue = 0f, alpha = 0.4f;

        /**
         * Sets the start pos of the edge.
         * Should be values between 0 and 1
         */
        public Edge start(double x, double y, double z) {
            this.startX = x;
            this.startY = y;
            this.startZ = z;
            return this;
        }

        /**
         * Sets the end pos of the edge.
         * Should be values between 0 and 1
         */
        public Edge end(double x, double y, double z) {
            this.endX = x;
            this.endY = y;
            this.endZ = z;
            return this;
        }

        /**
         * Sets the color of the drawn edge
         * Should be values between 0 and 1
         */
        public Edge color(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
            return this;
        }

        /**
         * Sets the color of the drawn edge
         */
        public Edge color(Color color) {
            return color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        }

        /**
         * Renders the edge
         */
        public void render(MatrixStack.Entry entry, VertexConsumer vertexConsumer, double x, double y, double z) {
            float q = (float) (endX - startX);
            float r = (float) (endY - startY);
            float s = (float) (endZ - startZ);
            float t = MathHelper.sqrt(q * q + r * r + s * s);
            q /= t;
            r /= t;
            s /= t;
            vertexConsumer.vertex(entry.getModel(), (float) (startX + x), (float) (startY + y), (float) (startZ + z)).color(red, green, blue, alpha).normal(entry.getNormal(), q, r, s).next();
            vertexConsumer.vertex(entry.getModel(), (float) (endX + x), (float) (endY + y), (float) (endZ + z)).color(red, green, blue, alpha).normal(entry.getNormal(), q, r, s).next();
        }

        /**
         * Creates an exact copy and transforms it via {@link #transform(Direction, Direction)}
         *
         * @param from what the data currently is on
         * @param to   the result side
         * @return the copied and transformed edge
         */
        public Edge copyAndTransform(Direction from, Direction to) {
            Edge edge = new Edge().start(startX, startY, startZ).end(endX, endY, endZ).color(red, green, blue, alpha);
            edge.transform(from, to);
            return edge;
        }

        /**
         * Transforms the edge from one direction to another
         *
         * @param from what the data currently is on
         * @param to   the result side
         */
        public void transform(Direction from, Direction to) {
            if (from == to)
                return;
            if (from.getAxis() != to.getAxis()) {
                double temp = getStart(from.getAxis());
                setStart(from.getAxis(), getStart(to.getAxis()));
                setStart(to.getAxis(), temp);

                temp = getEnd(from.getAxis());
                setEnd(from.getAxis(), getEnd(to.getAxis()));
                setEnd(to.getAxis(), temp);
            }

            if (from.getDirection() != to.getDirection()) {
                setStart(to.getAxis(), 1 - getStart(to.getAxis()));
                setEnd(to.getAxis(), 1 - getEnd(to.getAxis()));
            }
        }

        private double getStart(Direction.Axis axis) {
            return axis.choose(startX, startY, startZ);
        }

        private double getEnd(Direction.Axis axis) {
            return axis.choose(endX, endY, endZ);
        }

        private void setStart(Direction.Axis axis, double value) {
            switch (axis) {
                case X -> startX = value;
                case Y -> startY = value;
                case Z -> startZ = value;
            }
        }

        private void setEnd(Direction.Axis axis, double value) {
            switch (axis) {
                case X -> endX = value;
                case Y -> endY = value;
                case Z -> endZ = value;
            }
        }
    }
}
