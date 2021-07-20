package brachy84.brachydium.api.tag;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.material.Material;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class Tags {

    public static String materialItem(String tag, Material materialOld) {
        return "c:" + materialOld.getRegistryName() + "_" + tag;
    }

    public static Tag<Item> of(Identifier id) {
        if(!Brachydium.areTagsLoaded()) {
            Brachydium.LOGGER.error("Can't load tag {} now. Tags are loaded on world load", id);
            return null;
        }
        return ServerTagManagerHolder.getTagManager().getTag(Registry.ITEM_KEY, id, id1 -> new NullPointerException("Could not find tag " + id1));
    }

    public static Tag<Item> of(String name) {
        return of(new Identifier(name));
    }

    public static final String INGOT = "ingots";

    private static final Map<Identifier, Tag<Item>> TAG_MAP = new HashMap<>();
    /*private static final RequiredTagList<Item> REQUIRED_TAGS = RequiredTagListRegistry.register(Registry.ITEM_KEY, "tags/items");;

    private static Tag.Identified<Item> register(String id) {
        REQUIRED_TAGS.getGroup().
        return REQUIRED_TAGS.add(id);
    }

    public static TagGroup<Item> getTagGroup() {
        return REQUIRED_TAGS.getGroup();
    }*/

    public static Tag<Item> register(Identifier id) {
        Tag<Item> tag = TagRegistry.item(id);
        TAG_MAP.put(id, TagRegistry.item(id));
        return tag;
    }

    public static Tag<Item> getOrCreate(Identifier id) {
        return TAG_MAP.computeIfAbsent(id, Tags::register);
    }
}
