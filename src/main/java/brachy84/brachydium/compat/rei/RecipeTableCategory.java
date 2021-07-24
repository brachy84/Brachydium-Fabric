package brachy84.brachydium.compat.rei;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.gui.FluidSlotWidget;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.api.widgets.ResourceSlotWidget;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Label;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeTableCategory implements DisplayCategory<RecipeTableDisplay> {

    private RecipeTable<?> recipeTable;

    public RecipeTableCategory(RecipeTable<?> recipeTable) {
        this.recipeTable = recipeTable;
    }

    @Override
    public CategoryIdentifier<? extends RecipeTableDisplay> getCategoryIdentifier() {
        return ReiCompat.category(recipeTable);
    }

    @Override
    public Renderer getIcon() {
        if (recipeTable.getTileItems().size() > 0 && recipeTable.getTileItems().get(0) != null) {
            return EntryStacks.of(recipeTable.getTileItems().get(0));
        }
        return EntryStacks.of(new ItemStack(Items.ACACIA_DOOR));
    }

    @Override
    public Text getTitle() {
        return new TranslatableText("brachydium.category." + getIdentifier().getPath());
    }

    @Override
    public @NotNull List<Widget> setupDisplay(RecipeTableDisplay recipeDisplay, Rectangle rect) {
        /*AABB bounds = AABB.of(rect);
        Pos2d origin = bounds.getTopLeft();
        Gui gui = recipeTable.createUITemplate(() -> 0, Gui.defaultBuilder(MinecraftClient.getInstance().player),
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
        gui.forAllChildren(child -> {
            List<Widget> innerWidgets = new ArrayList<>();
            child.getReiWidgets(innerWidgets, origin);
            for (Widget widget : innerWidgets) {
                if (widget instanceof Slot && child instanceof ResourceSlotWidget) {
                    Brachydium.LOGGER.info("Adding resource slot to rei");
                    brachydiumSlots.add((ResourceSlotWidget<?>) child);
                    reiSlots.add((Slot) widget);
                }
                widgets.add(widget);
            }
            if (innerWidgets.size() > 0) {
                lowestY.set(Math.max(lowestY.get(), child.getRelativPos().getY() + child.getSize().height()));
            }
        });
        setEntries(recipeDisplay, brachydiumSlots, reiSlots);
        Pos2d point = origin.add(new Pos2d(4, lowestY.get() + 6));
        widgets.add(simpleLabel(point, new TranslatableText("brachydium.text.eu_t", recipeDisplay.getEUt())));
        widgets.add(simpleLabel(point.add(0, 10), new TranslatableText("brachydium.text.duration_sec", recipeDisplay.getDuration() / 20f)));
        widgets.add(simpleLabel(point.add(0, 10), new TranslatableText("brachydium.text.total_eu", recipeDisplay.getDuration() * recipeDisplay.getEUt())));*/
        return new ArrayList<>();
    }

    private Label simpleLabel(Pos2d point, Text text) {
        Label label = Widgets.createLabel(point.asReiPoint(), text);
        label.setHorizontalAlignment(-1);
        return label;
    }

    private void setEntries(RecipeTableDisplay display, List<ResourceSlotWidget<?>> slots, List<Slot> reiSlots) {
        Iterator<EntryIngredient> inputItems = display.getItemInputs().iterator();
        Iterator<EntryIngredient> inputFluids = display.getFluidInputs().iterator();
        Iterator<EntryIngredient> outputItems = display.getItemOutputs().iterator();
        Iterator<EntryIngredient> outputFluids = display.getFluidOutputs().iterator();
        for (int i = 0; i < reiSlots.size(); i++) {
            ResourceSlotWidget<?> slot = slots.get(i);
            Slot reiSlot = reiSlots.get(i);
            if (slot instanceof ItemSlotWidget) {
                if (reiSlot.getNoticeMark() == 1) {
                    if (!inputItems.hasNext()) continue;
                    Brachydium.LOGGER.info(" - inputItem");
                    reiSlot.entries(inputItems.next());
                } else if (reiSlot.getNoticeMark() == 2) {
                    if (!outputItems.hasNext()) continue;
                    Brachydium.LOGGER.info(" - outputItem");
                    reiSlot.entries(outputItems.next());
                }
            } else if (slot instanceof FluidSlotWidget) {
                if (reiSlot.getNoticeMark() == 1) {
                    if (!inputFluids.hasNext()) continue;
                    Brachydium.LOGGER.info(" - inputFluid");
                    reiSlot.entries(inputFluids.next());
                } else if (reiSlot.getNoticeMark() == 2) {
                    if (!outputFluids.hasNext()) continue;
                    Brachydium.LOGGER.info(" - outputFluid");
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
