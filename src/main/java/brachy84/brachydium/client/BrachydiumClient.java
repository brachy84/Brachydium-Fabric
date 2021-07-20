package brachy84.brachydium.client;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.blockEntity.trait.AbstractRecipeLogic;
import brachy84.brachydium.api.network.Channels;
import brachy84.brachydium.api.resource.ModelProvider;
import brachy84.brachydium.api.resource.ResourceProvider;
import brachy84.brachydium.api.resource.VariantProvider;
import brachy84.brachydium.gui.ClientUi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class BrachydiumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerModelProvider(new ModelProvider());
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(ResourceProvider::new);
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(VariantProvider::new);
        ClientUi.init();

        ClientPlayNetworking.registerGlobalReceiver(Channels.UPDATE_WORKING_STATE, ((client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            if (client.world != null) {
                BlockEntity blockEntity = client.world.getBlockEntity(pos);
                if (blockEntity instanceof BlockEntityHolder) {
                    TileEntity tile = ((BlockEntityHolder) blockEntity).getActiveTileEntity();
                    if (tile != null) {
                        AbstractRecipeLogic recipeLogic = tile.getTrait(AbstractRecipeLogic.class);
                        if (recipeLogic != null) {
                            String state = buf.readString();
                            Brachydium.LOGGER.info("Setting state to {} at {} on Client", state, pos);
                            recipeLogic.setState(AbstractRecipeLogic.State.valueOf(state));
                        }
                    }
                }
            }
        }));
    }

    public static Text getModIdForTooltip(String mod) {
        return new LiteralText(mod).formatted(Formatting.BLUE, Formatting.ITALIC);
    }
}
