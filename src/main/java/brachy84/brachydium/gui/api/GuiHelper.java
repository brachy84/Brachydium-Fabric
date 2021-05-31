package brachy84.brachydium.gui.api;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.gui.math.Color;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Shape;
import brachy84.brachydium.gui.math.Size;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * You don't need to implement this
 */
@ApiStatus.NonExtendable
@Environment(EnvType.CLIENT)
public abstract class GuiHelper extends DrawableHelper {

    public abstract TextRenderer getTextRenderer();

    public abstract void drawText(MatrixStack matrices, Text text, Point point);

    public abstract void drawText(MatrixStack matrices, Text text, Point point, Color color);

    public abstract void drawShape(Point point, Shape shape, Color color);

    public abstract void drawItem(ItemStack stack, Point point);

    public abstract void drawFluid(FluidStack stack, Point point, Size size);

    public abstract void drawTextureArea(TextureArea texture, Point point);

    public abstract void drawTextureArea(TextureArea texture, Point point, Size drawSize);

    @Override
    public void fillGradient(MatrixStack matrices, int xStart, int yStart, int xEnd, int yEnd, int colorStart, int colorEnd) {
        super.fillGradient(matrices, xStart, yStart, xEnd, yEnd, colorStart, colorEnd);
    }

    public abstract void renderFluidTooltip(FluidStack stack, Point pos);

    public abstract void renderTooltip(List<? extends OrderedText> lines, Point pos);
}
