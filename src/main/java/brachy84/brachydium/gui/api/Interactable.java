package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.widgets.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
public interface Interactable<T extends Widget> {

    /**
     * @return the implementing class
     */
    T getParent();

    /**
     * called when ever the mouse moves on the screen
     * @param point of the mouse
     */
    @ApiStatus.OverrideOnly
    default void onMouseMoved(Point point) {}

    /**
     * called when clicked on the Interactable based on {@link Interactable#isMouseOver(Point)}
     * @param point of the mouse
     * @param buttonId the button id (Left == 1, right == 2)
     */
    @ApiStatus.OverrideOnly
    default void onClick(Point point, int buttonId) {}

    /**
     * called when released a click on the Interactable based on {@link Interactable#isMouseOver(Point)}
     * @param point of the mouse
     * @param buttonId the button id (Left == 1, right == 2)
     */
    @ApiStatus.OverrideOnly
    default void onClickReleased(Point point, int buttonId) {}

    /**
     * called when the interactable is focused and the mouse gets dragged
     * @param point of the mouse
     * @param buttonId the button id (Left == 1, right == 2)
     * @param deltaX difference from last call
     * @param deltaY difference from last call
     */
    @ApiStatus.OverrideOnly
    default void onMouseDragged(Point point, int buttonId, double deltaX, double deltaY) {}

    /**
     * called when the interactable is focused and the scrollweel is used
     * @param point of the mouse
     * @param amount of lines scrolled
     */
    @ApiStatus.OverrideOnly
    default void onScrolled(Point point, double amount) {}

    /**
     * called when the interactable is focused and a key is pressed
     * @param keyCode key
     * @param scanCode ?
     * @param modifiers ?
     */
    @ApiStatus.OverrideOnly
    default void onKeyPressed(int keyCode, int scanCode, int modifiers) {}

    /**
     * called when the interactable is focused and a key is released
     * @param keyCode key
     * @param scanCode ?
     * @param modifiers ?
     */
    @ApiStatus.OverrideOnly
    default void onKeyReleased(int keyCode, int scanCode, int modifiers) {}

    /**
     * called when the interactable is focused and a char is typed
     * @param chr character
     * @param modifiers ?
     */
    @ApiStatus.OverrideOnly
    default void onCharTyped(char chr, int modifiers) {}

    /**
     * try change the focus
     * @param lookForwards should look for next focus
     */
    @ApiStatus.NonExtendable
    default void changeFocus(boolean lookForwards) {}

    /**
     * @param point to check
     * @return if the point is within the parents bounds
     */
    default boolean isMouseOver(Point point) {
        return getParent().isHovering(point);
    }
}
