package brachy84.brachydium.client;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import brachy84.brachydium.api.blockEntity.MetaBlockEntityHolder;
import brachy84.brachydium.api.handlers.AbstractRecipeLogic;
import brachy84.brachydium.api.material.Material;
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
            if(client.world != null) {
                BlockEntity blockEntity = client.world.getBlockEntity(pos);
                if(blockEntity instanceof MetaBlockEntityHolder) {
                    MetaBlockEntity mbe = ((MetaBlockEntityHolder) blockEntity).getMetaBlockEntity();
                    AbstractRecipeLogic recipeLogic = (AbstractRecipeLogic) mbe.getTrait(AbstractRecipeLogic.class);
                    if(recipeLogic != null) {
                        String state = buf.readString();
                        Brachydium.LOGGER.info("Setting state to {} at {} on Client", state, pos);
                        recipeLogic.setState(AbstractRecipeLogic.State.valueOf(state));
                        //clien.world.addSyncedBlockEvent(pos, client.world.getBlockState(pos).getBlock(), 0, 0);
                    }
                }
            }
        }));

        Material.REGISTRY.foreach(Material::registerClient);
    }

    public static Text getModIdForTooltip(String mod) {
        return new LiteralText(mod).formatted(Formatting.BLUE, Formatting.ITALIC);
    }
}
