package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import com.google.common.collect.Lists;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SingleChildWidget extends Widget{

    private Widget child;

    public SingleChildWidget(AABB bounds) {
        super(bounds);
    }

    @Override
    public void render(MatrixStack matrices, Point mousePos, float delta) {
        super.render(matrices, mousePos, delta);
        getChildren().forEach(widget -> widget.render(matrices, mousePos, delta));
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void onDestroy() {
       super.onDestroy();
    }

    /**
     * @return only this child (can be multiple)
     */
    public List<Widget> getChildren() {
        if(child != null)
            return Lists.newArrayList(child);
        return new ArrayList<>();
    }

    public void forEachChild(Consumer<Widget> consumer) {
        getChildren().forEach(widget -> {
            if(widget != null) {
                consumer.accept(widget);
            }
        });
    }

    /**
     * @return child with all sub-children
     */
    public List<Widget> getAllChildren() {
        List<Widget> widgets = new ArrayList<>();
        if(child instanceof SingleChildWidget) {
            widgets.addAll(((SingleChildWidget) child).getAllChildren());
        }
        widgets.add(child);
        return widgets;
    }
}
