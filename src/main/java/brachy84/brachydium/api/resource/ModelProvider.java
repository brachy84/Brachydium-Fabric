package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import net.fabricmc.fabric.api.client.model.ExtraModelProvider;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModelProvider implements ExtraModelProvider {

    @Override
    public void provideExtraModels(ResourceManager manager, Consumer<Identifier> out) {
        out.accept(new ModelIdentifier(Brachydium.id("bucket_base"), "inventory"));
        out.accept(new ModelIdentifier(Brachydium.id("bucket_fluid"), "inventory"));
        out.accept(new ModelIdentifier(Brachydium.id("bucket_background"), "inventory"));
    }
}
