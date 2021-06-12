package brachy84.brachydium.api.render.models;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.api.tag.TagDictionary;
import brachy84.brachydium.gui.math.Color;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
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
import java.util.stream.Stream;

public class MaterialItemModel implements BakedModel, FabricBakedModel, UnbakedModel {

    protected ColoredSprite[] sprites = new ColoredSprite[0];
    private ModelIdentifier modelId;
    private Mesh mesh;
    private Color materialColor;
    private TagDictionary.Entry tag;
    private final boolean isBlock;

    private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:block/block");

    private ModelTransformation transformation;

    public MaterialItemModel(Material material, TagDictionary.Entry tag) {
        modelId = new ModelIdentifier(Brachydium.id(String.format("materials/%s", tag.getName())), "inventory");
        materialColor = material.color;
        this.isBlock = tag.getType() == TagDictionary.Type.BLOCK;
        //sprites = new ColoredSprite[]{
        //        new ColoredSprite(Brachydium.id("item/materials/" + tag.getName()), material.color.withAlpha((byte) 125).asInt()),
        //new ColoredSprite(Brachydium.id("item/materials/" + tag.getName() + "_overlay"), -1)
        //};
    }

    public MaterialItemModel(TagDictionary.Entry tag, Material material, ColoredSprite... sprites) {
        modelId = new ModelIdentifier(Brachydium.id(String.format("materials/%s", tag.getName())), "inventory");
        this.sprites = sprites;
        this.tag = tag;
        this.isBlock = tag.getType() == TagDictionary.Type.BLOCK;
        materialColor = material.color;
    }

    public static MaterialItemModel simple(Material material, TagDictionary.Entry tag) {
        ColoredSprite[] sprites = {
                new ColoredSprite(Brachydium.id("item/materials/" + tag.getName()), material.color.asInt()),
                //new ColoredSprite(Brachydium.id("item/materials/" + tag.getName() + "_overlay"), -1)
        };
        return new MaterialItemModel(tag, material, sprites);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        //context.meshConsumer().accept(mesh);
        BakedModelManager bakedModelManager = MinecraftClient.getInstance().getBakedModelManager();
        context.fallbackConsumer().accept(bakedModelManager.getModel(modelId));
        //context.fallbackConsumer().accept(bakedModelManager.getModel(getBackgroundModel()));

        if(isBlock) {
            for (Direction direction : Direction.values()) {
                for (ColoredSprite sprite : sprites) {
                    if (!sprite.hasColor() && sprite.getSprite() instanceof MissingSprite) continue;
                    sprite.emitFace(context.getEmitter(), direction, 0);
                }
            }
        } else {
            BakedModel itemModel = bakedModelManager.getModel(modelId);
            int color = Color.of((float) (materialColor.asInt() >> 16 & 255) / 255.0F, (float) (materialColor.asInt() >> 8 & 255) / 255.0F, (float) (materialColor.asInt() & 255) / 255.0F).asInt();

            final QuadEmitter emitter = context.getEmitter();


            context.pushTransform(quad -> {
                quad.nominalFace(GeometryHelper.lightFace(quad));
                quad.spriteColor(0, color, color, color, color);
                quad.spriteBake(0, itemModel.getSprite(), MutableQuadView.BAKE_LOCK_UV);
                return true;
            });

            itemModel.getQuads(null, null, randomSupplier.get()).forEach(q -> {
                emitter.fromVanilla(q.getVertexData(), 0, false);
                emitter.emit();
            });

            context.popTransform();
        }
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        //context.meshConsumer().accept(mesh);
        for (Direction direction : Direction.values()) {
            for (ColoredSprite sprite : sprites) {
                if (!sprite.hasColor() && sprite.getSprite() instanceof MissingSprite) continue;
                sprite.emitFace(context.getEmitter(), direction, 0);
            }
        }
    }


    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return Stream.of(sprites).map(ColoredSprite::getId).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        for (int i = 0; i < sprites.length; ++i) {
            sprites[i].makeSprite(textureGetter);
        }
        JsonUnbakedModel defaultBlockModel = (JsonUnbakedModel) loader.getOrLoadModel(DEFAULT_BLOCK_MODEL);
        transformation = defaultBlockModel.getTransformations();
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        /*if(isBlock) {
            for(Direction direction : Direction.values()) {
                for(ColoredSprite sprite : sprites) {
                    if(!sprite.hasColor() && sprite.getSprite() instanceof MissingSprite) continue;
                    sprite.emitFace(emitter, direction, 0);
                }
            }
        } else {
            for(ColoredSprite sprite : sprites) {
                sprite.emit(emitter, sprite.hasColor());
            }

        }*/
        mesh = builder.build();

        return this;
    }

    @Override
    public Sprite getSprite() {
        return sprites[0].getSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return isBlock ? transformation : ModelHelper.DEFAULT_ITEM_TRANSFORMS;
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
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return null;
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
}
