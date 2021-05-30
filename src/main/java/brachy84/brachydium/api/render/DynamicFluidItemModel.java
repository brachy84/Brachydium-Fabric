package brachy84.brachydium.api.render;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.fluid.SimpleFluidRenderer;
import brachy84.brachydium.gui.math.Color;
import com.mojang.datafixers.util.Pair;
import io.github.astrarre.transfer.internal.mixin.BucketItemAccess_AccessFluid;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class DynamicFluidItemModel implements BakedModel, FabricBakedModel {

    public abstract ModelIdentifier getBaseModel();

    public abstract ModelIdentifier getBackgroundModel();

    public abstract ModelIdentifier getFluidModel();

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        Fluid fluid = Fluids.EMPTY;
        if (stack.getItem() instanceof BucketItemAccess_AccessFluid) {
            fluid = ((BucketItemAccess_AccessFluid) stack.getItem()).getFluid();
        }
        BakedModelManager bakedModelManager = MinecraftClient.getInstance().getBakedModelManager();
        context.fallbackConsumer().accept(bakedModelManager.getModel(getBaseModel()));
        //context.fallbackConsumer().accept(bakedModelManager.getModel(getBackgroundModel()));

        if (fluid != Fluids.EMPTY) {
            SimpleFluidRenderer.FluidRenderingData data = SimpleFluidRenderer.fromFluid(fluid);
            if (data != null) {
                BakedModel fluidModel = bakedModelManager.getModel(getFluidModel());
                //Color color = Color.of(data.getColor());
                int color = Color.of((float) (data.getColor() >> 16 & 255) / 255.0F, (float) (data.getColor() >> 8 & 255) / 255.0F, (float) (data.getColor() & 255) / 255.0F).asInt();
                context.pushTransform(quad -> {
                    quad.nominalFace(GeometryHelper.lightFace(quad));
                    quad.spriteColor(0, color, color, color, color);
                    quad.spriteBake(0, data.getSprite(), MutableQuadView.BAKE_LOCK_UV);
                    return true;
                });
                final QuadEmitter emitter = context.getEmitter();
                fluidModel.getQuads(null, null, randomSupplier.get()).forEach(q -> {
                    emitter.fromVanilla(q.getVertexData(), 0, false);
                    emitter.emit();
                });
                context.popTransform();
            } else {
                Brachydium.LOGGER.info("Couldn't find fluid " + fluid);
            }

        }
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {}

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
        return Collections.emptyList();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelHelper.DEFAULT_ITEM_TRANSFORMS;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    /*@Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return Collections.emptyList();
    }*/
}
