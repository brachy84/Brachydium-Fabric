package brachy84.brachydium.api.tag;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.material.MaterialOld;
import net.minecraft.item.Item;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Tags {

    public static String materialItem(String tag, MaterialOld materialOld) {
        return "c:" + materialOld.getName() + "_" + tag;
    }

    public static Tag<Item> of(String name) {
        if(!Brachydium.areTagsLoaded()) {
            Brachydium.LOGGER.error("Can't load tag {} now. Tags are loaded on world load", name);
            return null;
        }
        return ServerTagManagerHolder.getTagManager().getTag(Registry.ITEM_KEY, new Identifier(name), id -> new NullPointerException("Could not find tag " + id));
    }

    public static final String INGOT = "ingots";

}
