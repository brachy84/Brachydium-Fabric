package brachy84.brachydium.gui.impl;

import brachy84.brachydium.gui.api.GuiHelper;
import brachy84.brachydium.gui.math.Color;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Shape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class GuiHelperImpl implements GuiHelper {

    private final MinecraftClient client;
    protected final MatrixStack matrices;
    protected TextRenderer textRenderer;
    protected ItemRenderer itemRenderer;
    private final float Z = 0;

    public GuiHelperImpl(MatrixStack matrices) {
        this.client = MinecraftClient.getInstance();
        this.matrices = matrices;
        this.textRenderer = client.textRenderer;
        this.itemRenderer = client.getItemRenderer();
    }

    @Override
    public void drawText() {

    }

    @Override
    public void drawShape(Shape shape, Color color) {
        BufferBuilder builder = getBufferBuilder();
        Matrix4f matrix = this.matrices.peek().getModel();
        builder.begin(GL11.GL_POLYGON_MODE, VertexFormats.POSITION_COLOR);
        shape.forEachVertex((x, y) -> {
            builder.vertex(matrix, x, y, Z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        });
        builder.end();
        BufferRenderer.draw(builder);
    }

    protected BufferBuilder getBufferBuilder() {
        return Tessellator.getInstance().getBuffer();
    }
}
