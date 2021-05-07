package brachy84.brachydium.api.render;

import brachy84.brachydium.api.blockEntity.MetaBlockEntityHolder;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class MbeHolderRenderer extends BlockEntityRenderer<MetaBlockEntityHolder> {

    public MbeHolderRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(MetaBlockEntityHolder entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

    }
}
