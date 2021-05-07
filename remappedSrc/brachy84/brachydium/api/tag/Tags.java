package brachy84.brachydium.api.tag;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class Tags {

    public static String materialItem(String tag, Material material) {
        return "c:" + material.getName() + "_" + tag;
    }

    public static Tag<Item> of(String name) {
        Tag<Item> tag = ServerTagManagerHolder.getTagManager().getItems().getTag(new Identifier(name));
        if(tag == null) {
            Brachydium.LOGGER.error("Could not find a tag with name " + name);
        }
        return tag;
    }

    public static final String INGOT = "ingots";
    public static final String NUGGET = "nuggets";
    public static final String BLOCK = "blocks";
    public static final String DUST = "dusts";
    public static final String SMALL_DUST = "small_dusts";
    public static final String PLATE = "plates";
    public static final String GEAR = "gears";
    public static final String SMALL_GEAR = "small_gears";
    public static final String DENSE_PLATE = "dense_plates";

}
