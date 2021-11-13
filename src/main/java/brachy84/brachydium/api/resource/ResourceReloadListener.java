package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.unification.LoadableTag;
import brachy84.brachydium.api.util.ITagGroupGetter;
import brachy84.brachydium.api.util.ITagHolder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ResourceReloadListener implements SimpleSynchronousResourceReloadListener {

    public static final ResourceReloadListener INSTANCE = new ResourceReloadListener();

    public static final Identifier RELOAD_CHANNEL = Brachydium.id("reload_resources");

    private ResourceReloadListener() {
    }

    @Override
    public Identifier getFabricId() {
        return Brachydium.id("general");
    }

    @Override
    public void reload(ResourceManager manager) {
        LoadableTag.loadTags();
        TagGroup<Item> group = ((ITagGroupGetter) ServerTagManagerHolder.getTagManager()).getTagGroup2(Registry.ITEM_KEY);
        for (Map.Entry<Identifier, Tag<Item>> entry : group.getTags().entrySet()) {
            for (Item item : entry.getValue().values()) {
                ((ITagHolder) item).addTag(entry.getKey());
            }
        }
        RecipeTable.loadRecipes();
        /*for(Identifier id : manager.findResources("brachydium", path -> path.endsWith(".json"))) {
            if(id.getPath().contains("/recipes/")) {
                try(InputStream stream = manager.getResource(id).getInputStream()) {
                    JsonRecipe.loadFromJson(id, toJsonElement(stream).getAsJsonObject());
                } catch (IOException e) {
                    Brachydium.LOGGER.error("Could not load recipe {}", id);
                }
            }
            // Materials need to be loaded much earlier due to items
            /*if(id.getPath().contains("/materials/")) {
                try(InputStream stream = manager.getResource(id).getInputStream()) {
                    JsonMaterial.loadFromJson(id, toJsonElement(stream).getAsJsonObject());
                } catch (IOException e) {
                    Brachydium.LOGGER.error("Could not load material {}", id);
                }
            }
        }*/
    }

    public static JsonElement toJsonElement(InputStream stream) throws IOException {
        // According to stackoverflow this is the most efficient way to make a string from stream
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = stream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        return new JsonParser().parse(result.toString("UTF-8"));
    }

}
