package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.energy.Voltages;
import brachy84.brachydium.gui.math.*;
import brachy84.brachydium.gui.widgets.RootWidget;
import brachy84.brachydium.gui.widgets.ShapeWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class GuiTestBlockEntity extends TieredMetaBlockEntity{

    public GuiTestBlockEntity(Identifier id) {
        super(id, Voltages.MV);
    }

    @Override
    public boolean hasUi() {
        return true;
    }

    @NotNull
    @Override
    public RootWidget.Builder createUITemplate(PlayerEntity player, RootWidget.Builder builder) {
        builder.widget(new ShapeWidget(Shape.rect(new Size(60, 20)), Color.of(230, 20, 20), Point.cartesian(0, 0)).setTransformation(Transformation.scale(15f, 15f)));
        builder.widget(new ShapeWidget(Shape.line(Point.cartesian(200, 0), 20), Color.of(230, 20, 20), Point.cartesian(0, 0)));
        //builder.widget(new ShapeWidget(Shape.regularPolygon(40, 50), Color.of(20, 200, 20), Point.cartesian(0, 0)));
        return builder;
    }
}
