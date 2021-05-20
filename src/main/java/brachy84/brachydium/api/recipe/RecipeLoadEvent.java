package brachy84.brachydium.api.recipe;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface RecipeLoadEvent {

    Event<RecipeLoadEvent> EVENT = EventFactory.createArrayBacked(RecipeLoadEvent.class,
            (listeners) -> () -> {
                for (RecipeLoadEvent listener : listeners) {
                    listener.load();
                }
            });

    void load();
}
