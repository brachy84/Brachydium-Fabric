package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.render.Texture;
import brachy84.brachydium.api.render.TileRenderUtil;
import brachy84.brachydium.api.util.Face;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.nbt.NbtCompound;

import java.util.*;

public class RenderTrait extends TileTrait {

    private boolean renderMissing;
    protected Texture baseTexture;
    private final Map<Face, List<Texture>> overlays = new HashMap<>();

    public RenderTrait(TileEntity tile, Texture baseTexture) {
        super(tile);
        renderMissing = true;
        this.baseTexture = Objects.requireNonNull(baseTexture);
        for(Face face : Face.values()) {
            overlays.put(face, new ArrayList<>());
        }
    }

    @Override
    public String getName() {
        return "renderable";
    }

    protected List<Texture> getOverlays(Face face) {
        return overlays.get(face);
    }

    public void addOverlay(Face face, Texture texture) {
        List<Texture> textures = overlays.get(face);
        textures.add(texture);
        overlays.put(face, textures);
    }

    public void onRender(QuadEmitter emitter) {
        TileRenderUtil.renderCube(emitter, baseTexture);
        TileRenderUtil.renderOverlays(emitter, tile.getFrontFacing(), overlays);
    }

    public void renderSide(QuadEmitter emitter, Face face, Texture texture) {
        if (texture.getSprite() instanceof MissingSprite && !renderMissing) return;
        TileRenderUtil.renderSide(emitter, face, tile.getFrontFacing(), texture.getSprite());
    }

    public boolean isRenderMissing() {
        return renderMissing;
    }

    public void stopRenderMissing() {
        renderMissing = false;
    }

    @Override
    public final void addApis(BlockEntityType<BlockEntityHolder> type) {
    }

    @Override
    public final void tick() {
    }

    @Override
    public final NbtCompound serializeTag() {
        return null;
    }

    @Override
    public final void deserializeTag(NbtCompound tag) {
    }
}
