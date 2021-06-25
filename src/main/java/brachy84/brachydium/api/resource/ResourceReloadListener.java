package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.recipe.JsonRecipe;
import brachy84.brachydium.api.recipe.RecipeLoadEvent;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceReloadListener implements SimpleSynchronousResourceReloadListener {

    public static final ResourceReloadListener INSTANCE = new ResourceReloadListener();

    private ResourceReloadListener() {}

    @Override
    public Identifier getFabricId() {
        return Brachydium.id("general");
    }

    @Override
    public void reload(ResourceManager manager) {
        RecipeLoadEvent.EVENT.invoker().load();
        for(Identifier id : manager.findResources("brachydium", path -> path.endsWith(".json"))) {
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
            }*/
        }
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
