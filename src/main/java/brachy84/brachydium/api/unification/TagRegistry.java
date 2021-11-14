package brachy84.brachydium.api.unification;

import net.devtech.arrp.json.tags.JTag;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;

import java.util.Collection;

import static brachy84.brachydium.Brachydium.RESOURCE_PACK;
import static net.devtech.arrp.json.tags.JTag.tag;

public interface TagRegistry {

    Event<TagRegistry> EVENT = EventFactory.createArrayBacked(TagRegistry.class,
            (listeners) -> () -> {
                for (TagRegistry listener : listeners) {
                    listener.load();
                }
            });

    void load();

    static void register(String type, Identifier id, Identifier... entries) {
        Identifier ide = new Identifier(id.getNamespace(), type + "/" + id.getPath());
        register(ide, entries);
    }

    static void register(String type, Identifier id, Iterable<Identifier> entries) {
        Identifier ide = new Identifier(id.getNamespace(), type + "/" + id.getPath());
        register(ide, entries);
    }

    static void register(Identifier id, Identifier... entries) {
        JTag tag = tag();
        for(Identifier ids : entries) {
            tag.add(ids);
        }
        RESOURCE_PACK.addTag(id, tag);
    }

    static void registerItems(Identifier id, Identifier... entries) {
        register("items", id, entries);
    }

    static void register(Identifier id, Iterable<Identifier> entries) {
        JTag tag = tag();
        for(Identifier ids : entries) {
            tag.add(ids);
        }
        RESOURCE_PACK.addTag(id, tag);
    }

    static void registerItems(Identifier id, Iterable<Identifier> entries) {
        register("items", id, entries);
    }
}
