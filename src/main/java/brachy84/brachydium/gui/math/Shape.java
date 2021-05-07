package brachy84.brachydium.gui.math;

import brachy84.brachydium.gui.Serializable;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.fabricmc.loom.util.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Shape implements Serializable {

    private float[] vertices;
    private Transformation transformation;

    public Shape(float... vertices) {
        this.vertices = vertices;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Shape(CompoundTag tag) {
        read(tag);
    }

    public static Shape line(Point p0, Point p1, float thiccness) {
        float angle = p0.angle(p1);
        float length = (float) p0.distance(p1);
        return builder().changeBuildMode(BuildMode.INCREMENT)
                .vertex(Point.polar(angle - 90, thiccness / 2))
                .vertex(Point.polar(angle, length))
                .vertex(Point.polar(angle + 90, thiccness))
                .vertex(Point.polar(angle + 180, length))
                .vertex(Point.polar(angle + 270, thiccness / 2))
                .build();
    }

    public static Shape rect(AABB bounds) {
        return builder().changeBuildMode(BuildMode.INCREMENT)
                .vertex(bounds.getTopLeft())
                .vertexX(bounds.width)
                .vertexY(-bounds.height)
                .vertexX(-bounds.width)
                .vertexY(bounds.height)
                .build();
    }

    public int vertices() {
        checkVertices();
        return vertices.length / 2;
    }

    public void forEachVertex(Consumer<Point> consumer) {
        checkVertices();
        for(int i = 1; i < vertices.length; i += 2) {
            consumer.accept(new Point(vertices[i-1], vertices[i]));
        }
    }

    public void forEachVertex(BiConsumer<Float, Float> consumer) {
        checkVertices();
        for(int i = 1; i < vertices.length; i += 2) {
            consumer.accept(vertices[i-1], vertices[i]);
        }
    }

    private void checkVertices() {
        if(vertices.length % 2 != 0) {
            throw new IllegalStateException("Vertices can not have an odd amount off values");
        }
    }

    @Override
    public void write(CompoundTag tag) {
        ListTag verticesTag = new ListTag();
        for(int i = 0; i < vertices.length; i++) {
            verticesTag.set(i, FloatTag.of(vertices[i]));
        }
        tag.put("vertices", verticesTag);
    }

    @Override
    public void read(CompoundTag tag) {
        ListTag verticesTag = tag.getList("vertices", 5);
        vertices = new float[verticesTag.size()];
        for(int i = 0; i < vertices.length; i++) {
            vertices[i] = verticesTag.getFloat(i);
        }
    }

    public static class Builder {

        BuildMode buildMode;
        List<Float> vertexList = new ArrayList<>();
        Point lastPoint;

        private Builder() {
            this.buildMode = BuildMode.ABSOLUTE;
        }

        public Builder changeBuildMode(BuildMode buildMode) {
            this.buildMode = buildMode;
            this.lastPoint = Point.ZERO;
            return this;
        }

        public Builder vertexX(float x) {
            float y = buildMode == BuildMode.ABSOLUTE ? lastPoint.getY() : 0;
            return vertex(Point.cartesian(x, y));
        }

        public Builder vertexY(float y) {
            float x = buildMode == BuildMode.ABSOLUTE ? lastPoint.getX() : 0;
            return vertex(Point.cartesian(x, y));
        }

        public Builder vertex(float x, float y) {
            return vertex(new Point(x, y));
        }

        public Builder polarVertex(float angle, float length) {
            return vertex(Point.polar(angle, length));
        }

        public Builder vertex(Point p) {
            Point p1 = buildMode == BuildMode.INCREMENT ? lastPoint.add(p) : p;
            vertexList.add(p1.getX());
            vertexList.add(p1.getY());
            this.lastPoint = p1;
            return this;
        }

        public Shape build() {
            float[] vertices = new float[vertexList.size()];
            for(int i = 0; i < vertices.length; i++) {
                vertices[i] = vertexList.get(i);
            }
            vertexList = null;
            return new Shape(vertices);
        }
    }

    public enum BuildMode {
        ABSOLUTE,
        INCREMENT
    }
}
