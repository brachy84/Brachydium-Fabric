package brachy84.brachydium.api.tag;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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

}
