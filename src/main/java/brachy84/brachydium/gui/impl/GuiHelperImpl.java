package brachy84.brachydium.gui.impl;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.gui.api.GuiHelper;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.math.*;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class GuiHelperImpl extends GuiHelper {

    private final MinecraftClient client;
    protected MatrixStack matrices;
    protected TextRenderer textRenderer;
    protected ItemRenderer itemRenderer;
    private Transformation transformation;
    private int z = 0;

    public GuiHelperImpl(MatrixStack matrices) {
        this.client = MinecraftClient.getInstance();
        this.matrices = matrices;
        this.textRenderer = client.textRenderer;
        this.itemRenderer = client.getItemRenderer();
        this.transformation = Transformation.ZERO;
    }

    @Override
    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    @Override
    public void drawText(MatrixStack matrices, Text text, Point point) {
        drawText(matrices, text, point, Color.of(1f, 1f, 1f));
    }

    @Override
    public void drawText(MatrixStack matrices, Text text, Point point, Color color) {
        textRenderer.draw(matrices, text, point.getX(), point.getY(), color.toInt());

    }

    @Override
    public void drawShape(Point point, Shape shape, Color color) {
        BufferBuilder builder = getBufferBuilder();
        Matrix4f matrix = this.matrices.peek().getModel();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.pushMatrix();
        //transformation.applyRotation();
        //transformation.applyTranslation();
        //transformation.applyScale();
        builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        shape.forEachVertex((x, y) -> {
            builder.vertex(matrix, point.getX() + x, point.getY() + y, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        });
        builder.end();
        RenderSystem.popMatrix();
        BufferRenderer.draw(builder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        RenderSystem.enableDepthTest();
    }

    @Override
    public void drawItem(ItemStack stack, Point point) {
        itemRenderer.zOffset = z;
        itemRenderer.renderInGuiWithOverrides(stack, (int) point.getX() + 1, (int) point.getY() + 1);
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, (int) point.getX() + 1, (int) point.getY() + 1);
    }

    @Override
    public void drawFluid(FluidStack stack, Point point) {

    }

    @Override
    public void drawTextureArea(TextureArea texture, Point point) {
        drawTextureArea(texture, point, texture.getImageSize());
    }

    @Override
    public void drawTextureArea(TextureArea texture, Point point, Size drawSize) {
        client.getTextureManager().bindTexture(texture.getPath());
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        //drawTexture(matrices, (int) point.getX(), (int) point.getY(), z, u, v, (int) sprite.getSize().width, (int) sprite.getSize().height, (int) drawSize.width, (int) drawSize.height);
        Matrix4f matrix4f = matrices.peek().getModel();

        AABB bounds = AABB.of(drawSize, point);

        //float x0 = point.getX(), x1 = x0 + sprite.getSize().width, y0 = point.getY(), y1 = y0 + sprite.getSize().height;
        //float u0 = u / sprite.getSize().width, u1 = (u + drawSize.width) / sprite.getSize().width, v0 = v / sprite.getSize().height, v1 = (v + drawSize.height / sprite.getSize().height);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, bounds.x0, bounds.y1, (float)z).texture(texture.u0, texture.v1).next();
        bufferBuilder.vertex(matrix4f, bounds.x1, bounds.y1, (float)z).texture(texture.u1, texture.v1).next();
        bufferBuilder.vertex(matrix4f, bounds.x1, bounds.y0, (float)z).texture(texture.u1, texture.v0).next();
        bufferBuilder.vertex(matrix4f, bounds.x0, bounds.y0, (float)z).texture(texture.u0, texture.v0).next();
        bufferBuilder.end();
        RenderSystem.enableAlphaTest();
        BufferRenderer.draw(bufferBuilder);
    }

    public void setMatrixStack(MatrixStack matrices) {
        this.matrices = matrices;
    }

    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
    }

    protected static BufferBuilder getBufferBuilder() {
        return Tessellator.getInstance().getBuffer();
    }

    /**
     * use {@link #drawTextureArea(TextureArea, Point)} (or any other drawSprite method)
     * the drawTexture methods have a fuchsie whoopsie
     */
    @Deprecated
    @Override
    public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        super.drawTexture(matrices, x, y, u, v, width, height);
    }

    public void setZ(int z) {
        this.z = z;
    }
}
