package brachy84.brachydium.api.blockEntity.trait;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.render.IRenderable;
import brachy84.brachydium.api.render.TileRenderUtil;
import brachy84.brachydium.api.util.Face;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;

import java.util.*;

public class TileEntityRenderer extends TileTrait {

    private boolean renderMissing;
    private final EnumMap<Face, List<IRenderable>> overlays = new EnumMap<>(Face.class);

    public TileEntityRenderer(TileEntity tile, Map<Face, List<IRenderable>> overlays) {
        super(tile);
        Brachydium.LOGGER.info("Creating RenderTrait with base texture");
        for (Face face : Face.values()) {
            this.overlays.put(face, new ArrayList<>());
        }
        this.overlays.putAll(overlays);
    }

    public static TileEntityRenderer create(TileEntity tile, IRenderable baseTexture) {
        Map<Face, List<IRenderable>> textureMap = new EnumMap<>(Face.class);
        for (Face face : Face.values())
            textureMap.put(face, Lists.newArrayList(baseTexture));
        return new TileEntityRenderer(tile, textureMap);
    }

    public static TileEntityRenderer create(TileEntity tile) {
        return new TileEntityRenderer(tile, new HashMap<>());
    }

    /**
     * Made to be used in a builder like style
     *
     * @param face    the face where to render the textures
     * @param texture the textures to render <b>ORDER IS IMPORTANT</b>
     * @return this
     */
    public TileEntityRenderer addOverlay(Face face, IRenderable texture) {
        List<IRenderable> textures = overlays.get(face);
        textures.add(texture);
        overlays.put(face, textures);
        return this;
    }

    public TileEntityRenderer addOverlay(IRenderable texture) {
        for (Face face : Face.values()) {
            addOverlay(face, texture);
        }
        return this;
    }

    public TileEntityRenderer addHorizontalOverlay(IRenderable texture) {
        for (Face face : Face.HORIZONTAL) {
            addOverlay(face, texture);
        }
        return this;
    }

    public TileEntityRenderer addTopBottomOverlay(IRenderable texture) {
        addOverlay(Face.BOTTOM, texture);
        addOverlay(Face.TOP, texture);
        return this;
    }

    public TileEntityRenderer addSideOverlay(IRenderable texture) {
        addOverlay(Face.LEFT, texture);
        addOverlay(Face.RIGHT, texture);
        return this;
    }

    public TileEntityRenderer addFrontOverlay(IRenderable texture) {
        return addOverlay(Face.FRONT, texture);
    }

    public void onRender(QuadEmitter emitter) {
        TileRenderUtil.renderOverlays(emitter, tile.getFrontFace(), overlays);
    }

    public boolean doesRenderMissing() {
        return renderMissing;
    }

    public void stopRenderMissing() {
        renderMissing = false;
    }

    @Override
    public final void tick() {
    }

    @Override
    public final NbtCompound serializeNbt() {
        return null;
    }

    @Override
    public final void deserializeNbt(NbtCompound tag) {
    }
}
