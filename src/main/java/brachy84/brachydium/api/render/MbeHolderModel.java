package brachy84.brachydium.api.render;

import brachy84.brachydium.api.block.BlockMachineItem;
import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import brachy84.brachydium.api.blockEntity.MetaBlockEntityHolder;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MbeHolderModel implements UnbakedModel, BakedModel, FabricBakedModel {

    private Mesh mesh;

    private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:block/block");

    private ModelTransformation transformation;

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> supplier, RenderContext renderContext) {
        renderContext.meshConsumer().accept(mesh);
        QuadEmitter emitter = renderContext.getEmitter();
        BlockEntity blockEntity = blockRenderView.getBlockEntity(blockPos);
        if(blockEntity instanceof MetaBlockEntityHolder) {
            MetaBlockEntityHolder holder = (MetaBlockEntityHolder) blockEntity;
            holder.getMetaBlockEntity().render(emitter);
        }
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, RenderContext renderContext) {
        renderContext.meshConsumer().accept(mesh);
        if(itemStack.getItem() instanceof BlockMachineItem) {
            QuadEmitter emitter = renderContext.getEmitter();
            MetaBlockEntity mbe = MetaBlockEntity.getFromId(((BlockMachineItem) itemStack.getItem()).getId());
            mbe.setFrontFacing(Direction.NORTH);
            mbe.render(emitter);
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return Collections.emptyList();
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
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return Textures.MACHINECASING[1].getSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return transformation;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return Texture.getAll().stream().map((Texture::getSpriteId)).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        System.out.println("baking model");
        JsonUnbakedModel defaultBlockModel = (JsonUnbakedModel) loader.getOrLoadModel(DEFAULT_BLOCK_MODEL);
        transformation = defaultBlockModel.getTransformations();
        if(!Texture.areInitialized()) {
            for(Texture texture : Texture.getAll()) {
                texture.makeSprite(textureGetter);
            }
        }
        if(RendererAccess.INSTANCE.hasRenderer()) {
            net.fabricmc.fabric.api.renderer.v1.Renderer renderer = RendererAccess.INSTANCE.getRenderer();
            MeshBuilder builder = renderer.meshBuilder();
            QuadEmitter emitter = builder.getEmitter();

            mesh = builder.build();
        }
        return this;
    }
}
