package brachy84.brachydium.compat;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.handlers.FluidTankList;
import brachy84.brachydium.api.handlers.ItemInventory;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.gui.widgets.RootWidget;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeTableCategory implements RecipeCategory<RecipeTableDisplay> {

    private RecipeTable<?> recipeTable;

    public RecipeTableCategory(RecipeTable<?> recipeTable) {
        this.recipeTable = recipeTable;
    }

    @Override
    public @NotNull Identifier getIdentifier() {
        return Brachydium.id(recipeTable.unlocalizedName + "_recipes");
    }

    @Override
    public @NotNull String getCategoryName() {
        return I18n.translate("brachydium.categgory." + getIdentifier().getPath());
    }

    @Override
    public @NotNull List<Widget> setupDisplay(RecipeTableDisplay recipeDisplay, Rectangle bounds) {
        RootWidget rootWidget = recipeTable.createUITemplate(RootWidget.builder(),
                ItemInventory.importInventory(recipeTable.getMaxInputs()),
                ItemInventory.exportInventory(recipeTable.getMaxOutputs()),
                FluidTankList.importTanks(recipeTable.getMaxFluidInputs()),
                FluidTankList.exportTanks(recipeTable.getMaxFluidOutputs())
        ).build();
        List<Widget> widgets = new ArrayList<>();
        AtomicReference<Float> lowestY = new AtomicReference<>(0f);
        rootWidget.forAllChildren(child -> {
            Optional<Widget> optionalWidget = child.getReiWidget();
            if(optionalWidget.isPresent()) {
                widgets.add(optionalWidget.get());
                lowestY.set(Math.max(lowestY.get(), child.getRelativPos().getY() + child.getSize().height));
            }
        });
        Point point = new Point(5, lowestY.get());
        widgets.add(Widgets.createLabel(point, new TranslatableText("brachydium.text.eu_t", recipeDisplay.getEUt())));
        point.translate(0, 10);
        widgets.add(Widgets.createLabel(point, new TranslatableText("brachydium.text.duration_sec", recipeDisplay.getDuration() / 20f)));
        point.translate(0, 10);
        widgets.add(Widgets.createLabel(point, new TranslatableText("brachydium.text.total_eu", recipeDisplay.getDuration() * recipeDisplay.getEUt())));
        return widgets;
    }
}
