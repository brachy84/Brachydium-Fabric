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
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.ApiStatus;

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

    public abstract void drawFluid(FluidStack stack, Point point);

    public abstract void drawSprite(ISprite sprite, Point point);

    public abstract void drawSprite(ISprite sprite, Point point, float u, float v);

    /**
     * Basically {@link #drawTexturedQuad(Matrix4f, int, int, int, int, int, float, float, float, float)} but not broken
     * @param sprite to draw
     * @param point to draw on (top left corner)
     * @param u texture offset in x
     * @param v texture offset in y
     * @param drawSize The size the drawn image should have
     */
    public abstract void drawSprite(ISprite sprite, Point point, float u, float v, Size drawSize);
}
