package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.math.AABB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MultiChildWidget extends SingleChildWidget {

    private List<Widget> children = new ArrayList<>();

    public MultiChildWidget(AABB bounds, Widget... widgets) {
        super(bounds);
        children(widgets);
    }

    public MultiChildWidget children(Widget... widgets) {
        children.addAll(Arrays.asList(widgets));
        return this;
    }

    @Override
    public List<Widget> getChildren() {
        List<Widget> widgets = new ArrayList<>();
        for(Widget child : children) {
            if(child instanceof SingleChildWidget) {
                widgets.addAll(((SingleChildWidget) child).getChildren());
            }
        }
        widgets.addAll(children);
        return widgets;
    }

    @Override
    public List<Widget> getAllChildren() {
        return children;
    }
}
