package brachy84.brachydium.gui.impl;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.fluid.SimpleFluidRenderer;
import brachy84.brachydium.gui.api.GuiHelper;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.math.*;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.List;

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
        textRenderer.draw(matrices, text, point.getX(), point.getY(), color.asInt());
    }

    @Override
    public void drawShape(Point point, Shape shape, Color color) {
        BufferBuilder builder = getBufferBuilder();
        Matrix4f matrix = this.matrices.peek().getModel();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        matrices.push();
        //transformation.applyRotation();
        //transformation.applyTranslation();
        //transformation.applyScale();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        shape.forEachVertex((x, y) -> {
            builder.vertex(matrix, point.getX() + x, point.getY() + y, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        });
        builder.end();
        matrices.pop();
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
    public void drawFluid(FluidStack stack, Point point, Size size) {
        if(stack.isEmpty()) return;
        z += 50;
        SimpleFluidRenderer.renderInGui(matrices, stack.getFluid(), AABB.of(size, point), z);
        int amount = stack.getAmount() / 81000;
        //z += 150;
        if (amount >= 1000) {
            String string = String.valueOf((int) Math.floor(stack.getAmount() / 1000D));
            matrices.translate(0, 0, this.z + 5);
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            textRenderer.draw(string, point.x + 19 - 2 - textRenderer.getWidth(string), point.y + 6 + 3, 16777215, true, matrices.peek().getModel(), immediate, false, 0, 15728880);
            immediate.draw();
            //matrices.translate(0, 0, z);
        }
        z -= 50;
    }

    @Override
    public void drawTextureArea(TextureArea texture, Point point) {
        drawTextureArea(texture, point, texture.getImageSize());
    }

    @Override
    public void drawTextureArea(TextureArea texture, Point point, Size drawSize) {
        //client.getTextureManager().bindTexture(texture.getPath());
        //matrices.color4f(1f, 1f, 1f, 1f);
        Matrix4f matrix4f = matrices.peek().getModel();

        AABB bounds = AABB.of(drawSize, point);
        RenderSystem.setShaderTexture(0, texture.getPath());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, bounds.x0, bounds.y1, (float)z).texture(texture.u0, texture.v1).next();
        bufferBuilder.vertex(matrix4f, bounds.x1, bounds.y1, (float)z).texture(texture.u1, texture.v1).next();
        bufferBuilder.vertex(matrix4f, bounds.x1, bounds.y0, (float)z).texture(texture.u1, texture.v0).next();
        bufferBuilder.vertex(matrix4f, bounds.x0, bounds.y0, (float)z).texture(texture.u0, texture.v0).next();
        bufferBuilder.end();
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

    public void renderFluidTooltip(FluidStack stack, Point pos) {
        renderTooltip(Lists.transform(stack.getTooltipLines(), Text::asOrderedText), pos);
    }

    public void renderTooltip(List<? extends OrderedText> lines, Point pos) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if(screen != null) {
            screen.renderOrderedTooltip(matrices, lines, (int) pos.x, (int) pos.y);
        }
    }
}
