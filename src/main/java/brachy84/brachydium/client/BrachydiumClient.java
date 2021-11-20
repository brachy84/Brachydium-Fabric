package brachy84.brachydium.client;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.SyncedBlockEntity;
import brachy84.brachydium.api.blockEntity.TileEntityGroup;
import brachy84.brachydium.api.network.Channels;
import brachy84.brachydium.api.resource.ModelProvider;
import brachy84.brachydium.api.resource.ResourceProvider;
import brachy84.brachydium.api.resource.VariantProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class BrachydiumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerModelProvider(new ModelProvider());
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(ResourceProvider::new);
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(VariantProvider::new);

        ClientPlayNetworking.registerGlobalReceiver(Channels.SYNC_TILE_CUSTOM, ((client, handler, buf, responseSender) -> {
            if (client.world != null) {
                BlockEntity blockEntity = client.world.getBlockEntity(buf.readBlockPos());
                if (blockEntity instanceof SyncedBlockEntity synced) {
                    synced.readCustomData(buf.readVarInt(), buf);
                } else {
                    Brachydium.LOGGER.error("Failed to sync custom data");
                }
            } else {
                Brachydium.LOGGER.error("Failed to sync custom data");
            }
        }));

        ClientPlayNetworking.registerGlobalReceiver(Channels.SYNC_TILE_INIT, ((client, handler, buf, responseSender) -> {
            if (client.world != null) {
                BlockEntity blockEntity = client.world.getBlockEntity(buf.readBlockPos());
                if (blockEntity instanceof BlockEntityHolder synced) {
                    synced.readPlaceData(buf);
                } else {
                    Brachydium.LOGGER.error("Failed to sync init data");
                }
            } else {
                Brachydium.LOGGER.error("Failed to sync init data");
            }
        }));
    }

    public static Text getModIdForTooltip(String mod) {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(mod);
        if (modContainer.isPresent())
            mod = modContainer.get().getMetadata().getName();
        return new LiteralText(mod).formatted(Formatting.BLUE, Formatting.ITALIC);
    }
}
