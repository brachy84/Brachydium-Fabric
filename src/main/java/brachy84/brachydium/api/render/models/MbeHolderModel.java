package brachy84.brachydium.api.render.models;

import brachy84.brachydium.api.block.BlockMachineItem;
import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.render.SpriteLoader;
import brachy84.brachydium.api.render.SpriteMap;
import brachy84.brachydium.api.render.Texture;
import brachy84.brachydium.api.render.Textures;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

@Environment(EnvType.CLIENT)
public class MbeHolderModel implements UnbakedModel, BakedModel, FabricBakedModel {

    private Mesh mesh;

    private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:block/block");

    private ModelTransformation transformation;

    private List<SpriteIdentifier> depends;

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> supplier, RenderContext renderContext) {
        renderContext.meshConsumer().accept(mesh);
        QuadEmitter emitter = renderContext.getEmitter();
        BlockEntity blockEntity = blockRenderView.getBlockEntity(blockPos);
        if (blockEntity instanceof BlockEntityHolder) {
            TileEntity tile = ((BlockEntityHolder) blockEntity).getActiveTileEntity();
            if (tile != null) {
                tile.render(emitter);
            }
        }
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, RenderContext renderContext) {
        renderContext.meshConsumer().accept(mesh);
        if (itemStack.getItem() instanceof BlockMachineItem) {
            QuadEmitter emitter = renderContext.getEmitter();
            TileEntity tile = TileEntity.ofStack(itemStack);
            tile.render(emitter);
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
    public Sprite getParticleSprite() {
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
        if(depends == null) {
            depends = SpriteLoader.getAllSprites();
        }
        return depends;
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        System.out.println("baking model");
        JsonUnbakedModel defaultBlockModel = (JsonUnbakedModel) loader.getOrLoadModel(DEFAULT_BLOCK_MODEL);
        transformation = defaultBlockModel.getTransformations();
        SpriteLoader.load(textureGetter);
        if (RendererAccess.INSTANCE.hasRenderer()) {
            net.fabricmc.fabric.api.renderer.v1.Renderer renderer = RendererAccess.INSTANCE.getRenderer();
            MeshBuilder builder = renderer.meshBuilder();
            QuadEmitter emitter = builder.getEmitter();

            mesh = builder.build();
        }
        return this;
    }
}
