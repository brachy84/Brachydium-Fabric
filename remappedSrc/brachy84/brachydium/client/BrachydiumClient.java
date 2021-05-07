package brachy84.brachydium.client;

import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.resource.ModelProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

@Environment(EnvType.CLIENT)
public class BrachydiumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        //BrachydiumApi.registerBlockEntitiesClient();
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> new ModelProvider());
    }
}
