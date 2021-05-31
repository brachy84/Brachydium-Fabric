package brachy84.brachydium.compat;

import brachy84.brachydium.api.handlers.FluidTankList;
import brachy84.brachydium.api.handlers.ItemInventory;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.gui.api.ResourceSlotWidget;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.widgets.BackgroundWidget;
import brachy84.brachydium.gui.widgets.FluidSlotWidget;
import brachy84.brachydium.gui.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.widgets.RootWidget;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Label;
import me.shedaniel.rei.api.widgets.Slot;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeTableCategory implements RecipeCategory<RecipeTableDisplay> {

    private RecipeTable<?> recipeTable;

    public RecipeTableCategory(RecipeTable<?> recipeTable) {
        this.recipeTable = recipeTable;
    }

    @Override
    public @NotNull Identifier getIdentifier() {
        return ReiCompat.category(recipeTable);
    }

    @Override
    public @NotNull String getCategoryName() {
        return I18n.translate("brachydium.category." + getIdentifier().getPath());
    }

    @Override
    public @NotNull EntryStack getLogo() {
        if(recipeTable.getTileItems().size() > 0 && recipeTable.getTileItems().get(0) != null) {
            EntryStack stack = EntryStack.create(recipeTable.getTileItems().get(0));
            if(stack != null) return stack;
        }
        return EntryStack.create(new ItemStack(Items.ACACIA_DOOR));
    }

    @Override
    public @NotNull List<Widget> setupDisplay(RecipeTableDisplay recipeDisplay, Rectangle rect) {
        AABB bounds = AABB.of(rect);
        Point origin = bounds.getTopLeft();
        RootWidget rootWidget = recipeTable.createUITemplate(() -> 0, RootWidget.builder(),
                ItemInventory.importInventory(recipeTable.getMaxInputs()),
                ItemInventory.exportInventory(recipeTable.getMaxOutputs()),
                FluidTankList.importTanks(recipeTable.getMaxFluidInputs()),
                FluidTankList.exportTanks(recipeTable.getMaxFluidOutputs())
        ).build();
        List<Widget> widgets = new ArrayList<>();
        AtomicReference<Float> lowestY = new AtomicReference<>(0f);
        List<ResourceSlotWidget<?>> brachydiumSlots = new ArrayList<>();
        List<Slot> reiSlots = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(rect));
        rootWidget.forAllChildren(child -> {
            List<Widget> innerWidgets = new ArrayList<>();
            child.getReiWidgets(innerWidgets, origin);
            for(Widget widget : innerWidgets) {
                if(widget instanceof Slot && child instanceof ResourceSlotWidget) {
                    brachydiumSlots.add((ResourceSlotWidget<?>) child);
                    reiSlots.add((Slot) widget);
                }
                widgets.add(widget);
            }
            if(!(child instanceof BackgroundWidget)) {
                lowestY.set(Math.max(lowestY.get(), child.getRelativPos().getY() + child.getSize().height));
            }
        });
        setEntries(recipeDisplay, brachydiumSlots, reiSlots);
        Point point = origin.add(new Point(4, lowestY.get() + 6));
        widgets.add(simpleLabel(point, new TranslatableText("brachydium.text.eu_t", recipeDisplay.getEUt())));
        point.translate(0, 10);
        widgets.add(simpleLabel(point, new TranslatableText("brachydium.text.duration_sec", recipeDisplay.getDuration() / 20f)));
        point.translate(0, 10);
        widgets.add(simpleLabel(point, new TranslatableText("brachydium.text.total_eu", recipeDisplay.getDuration() * recipeDisplay.getEUt())));
        return widgets;
    }

    private Label simpleLabel(Point point, Text text) {
        Label label = Widgets.createLabel(point.toReiPoint(), text);
        label.setHorizontalAlignment(-1);
        return label;
    }

    private void setEntries(RecipeTableDisplay display, List<ResourceSlotWidget<?>> slots, List<Slot> reiSlots) {
        Iterator<List<EntryStack>> inputItems = display.getItemInputs().iterator();
        Iterator<List<EntryStack>> inputFluids = display.getFluidInputs().iterator();
        Iterator<List<EntryStack>> outputItems = display.getItemOutputs().iterator();
        Iterator<List<EntryStack>> outputFluids = display.getFluidOutputs().iterator();
        for(int i = 0; i < reiSlots.size(); i++) {
            ResourceSlotWidget<?> slot = slots.get(i);
            Slot reiSlot = reiSlots.get(i);
            if(slot instanceof ItemSlotWidget) {
                if(reiSlot.getNoticeMark() == 1) {
                    if(!inputItems.hasNext()) continue;
                    reiSlot.entries(inputItems.next());
                } else if(reiSlot.getNoticeMark() == 2) {
                    if(!outputItems.hasNext()) continue;
                    reiSlot.entries(outputItems.next());
                }
            } else if(slot instanceof FluidSlotWidget) {
                if(reiSlot.getNoticeMark() == 1) {
                    if(!inputFluids.hasNext()) continue;
                    reiSlot.entries(inputFluids.next());
                } else if(reiSlot.getNoticeMark() == 2) {
                    if(!outputFluids.hasNext()) continue;
                    reiSlot.entries(outputFluids.next());
                }
            }
        }
    }

    @Override
    public int getDisplayHeight() {
        return 100;
    }

    @Override
    public int getDisplayWidth(RecipeTableDisplay display) {
        return 150;
    }
}
