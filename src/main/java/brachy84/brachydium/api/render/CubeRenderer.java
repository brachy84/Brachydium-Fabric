package brachy84.brachydium.api.render;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.List;

public class CubeRenderer extends SpriteLoader {

    private final Identifier id;
    private Sprite sprite;

    public CubeRenderer(Identifier id) {
        this.id = id;
    }

    @Override
    public void loadSprites(SpriteMap spriteMap) {
        this.sprite = spriteMap.loadSprite(id);
    }

    @Override
    public void addSprites(List<SpriteIdentifier> sprites) {
        sprites.add(blockSprite(id));
    }

    public void render(QuadEmitter emitter) {
        TileRenderUtil.renderCube(emitter, sprite);
    }
}
