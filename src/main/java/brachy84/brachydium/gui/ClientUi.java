package brachy84.brachydium.gui;

import brachy84.brachydium.gui.wrapper.ModularGuiScreen;
import brachy84.brachydium.gui.wrapper.UIFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class ClientUi {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(UIFactory.UI_SYNC_ID, (client, handler, buf, responseSender) -> {
            // Read packet data on the event loop
            Identifier factoryId = buf.readIdentifier();
            UIFactory<?> factory = UIFactory.UI_FACTORY_REGISTRY.tryGetEntry(factoryId);
            if (factory != null) {
                client.execute(() -> {
                    factory.openClientUi(buf);
                });
            }
        });
    }
}
