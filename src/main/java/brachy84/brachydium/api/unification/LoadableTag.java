package brachy84.brachydium.api.unification;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadableTag<T> implements Tag.Identified<T> {

    private static final Map<Identifier, LoadableTag<?>> TAGS = new HashMap<>();
    private static boolean loaded = false;

    public static boolean isLoaded() {
        return loaded;
    }

    public static void loadTags() {
        TAGS.values().forEach(LoadableTag::loadTag);
        loaded = true;
    }

    public static LoadableTag<Item> getItemTag(Identifier id) {
        return (LoadableTag<Item>) TAGS.computeIfAbsent(id, id1 -> new LoadableTag<>(Registry.ITEM_KEY, id1));
    }

    public static LoadableTag<Block> getBlockTag(Identifier id) {
        return (LoadableTag<Block>) TAGS.computeIfAbsent(id, id1 -> new LoadableTag<>(Registry.BLOCK_KEY, id1));
    }

    public static LoadableTag<Fluid> getFluidTag(Identifier id) {
        return (LoadableTag<Fluid>) TAGS.computeIfAbsent(id, id1 -> new LoadableTag<>(Registry.FLUID_KEY, id1));
    }

    public static LoadableTag<EntityType<?>> getEntityTypeTag(Identifier id) {
        return (LoadableTag<EntityType<?>>) TAGS.computeIfAbsent(id, id1 -> new LoadableTag<>(Registry.ENTITY_TYPE_KEY, id1));
    }

    public static LoadableTag<Biome> getBiomeTag(Identifier id) {
        return (LoadableTag<Biome>) TAGS.computeIfAbsent(id, id1 -> new LoadableTag<>(Registry.BIOME_KEY, id1));
    }

    public static LoadableTag<GameEvent> getGameEventTag(Identifier id) {
        return (LoadableTag<GameEvent>) TAGS.computeIfAbsent(id, id1 -> new LoadableTag<>(Registry.GAME_EVENT_KEY, id1));
    }

    RegistryKey<Registry<T>> registryKey;
    private final Identifier id;
    private Tag<T> tag;

    private LoadableTag(RegistryKey<Registry<T>> registryKey, Identifier id) {
        this.registryKey = registryKey;
        this.id = id;
        if (loaded)
            loadTag();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean contains(T entry) {
        if (!loaded)
            return false;
        return tag.contains(entry);
    }

    @Override
    public List<T> values() {
        if (!loaded)
            return new ArrayList<>();
        return tag.values();
    }

    private void loadTag() {
        tag = ServerTagManagerHolder.getTagManager().getTag(registryKey, id, id -> new IllegalArgumentException("Could not load LoadableTag from tag " + id));
    }
}
