package brachy84.brachydium.api.tag;

import brachy84.brachydium.api.material.Material;
import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class LoadableTag {

    private static final Map<String, Map<String, LoadableTag>> tags = new HashMap<>();
    private static final Map<String, Map<String, LoadableTag>> MATERIAL_TAGS = new HashMap<>();

    public static void loadAll() {
        for(Map<String, LoadableTag> map : tags.values()) {
            for(LoadableTag tag : map.values()) {
                tag.load();
            }
        }

        for(Map<String, LoadableTag> map : MATERIAL_TAGS.values()) {
            for(LoadableTag tag : map.values()) {
                tag.load();
            }
        }
    }

    private Identifier id;
    private Tag<Item> tag;
    private boolean loaded = false;

    public static LoadableTag getOrCreate(Identifier id) {
        LoadableTag tag = get(id);
        if(tag != null) {
            return tag;
        }
        return new LoadableTag(id);
    }

    public static LoadableTag getOrCreate(Material material, TagDictionary.Entry entry) {
        LoadableTag tag = get(material, entry);
        if(tag != null) {
            return tag;
        }
        return new LoadableTag(material, entry);
    }

    private LoadableTag(Identifier id) {
        this.id = id;
        Map<String, LoadableTag> map = new HashMap<>();
        if(tags.containsKey(id.getNamespace())) {
            map = tags.get(id.getNamespace());
        }
        map.put(id.getPath(), this);
        tags.put(id.getNamespace(), map);
    }

    private LoadableTag(Material material, TagDictionary.Entry entry) {
        this.id = new Identifier("c", material.getRegistryName() + "_" + entry.getTagName());
        Map<String, LoadableTag> map = new HashMap<>();
        if(MATERIAL_TAGS.containsKey(material.getRegistryName())) {
            map = MATERIAL_TAGS.get(material.getRegistryName());
        }
        map.put(entry.getTagName(), this);
        MATERIAL_TAGS.put(material.getRegistryName(), map);
    }

    public void load() {
        tag = Tags.of(id);
        loaded = tag != null;
    }

    public Tag<Item> getTag() {
        return tag;
    }

    public boolean isLoaded() {
        return loaded;
    }

    @Nullable
    public static LoadableTag get(Identifier id) {
        Map<String, LoadableTag> map = tags.get(id.getNamespace());
        if(map == null) return null;
        return map.get(id.getPath());
    }

    @Nullable
    public static LoadableTag get(Material material, TagDictionary.Entry entry) {
        Map<String, LoadableTag> map = MATERIAL_TAGS.get(material.getRegistryName());
        if(map == null) return null;
        return map.get(entry.getTagName());
    }
}
