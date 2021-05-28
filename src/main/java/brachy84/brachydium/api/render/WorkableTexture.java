package brachy84.brachydium.api.render;

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

public class WorkableTexture extends Texture {

    private Texture activeTexture;

    public WorkableTexture(Identifier path) {
        super(path);
        activeTexture = new Texture(new Identifier(path.getNamespace(), path.getPath() + "_active"));
    }

    /**
     * @param isActive if the active texture should be applied
     * @return the texture
     */
    public Sprite getSprite(boolean isActive) {
        return isActive ? activeTexture.getSprite() : getSprite();
    }

    /**
     * In this class use {@link #getSprite(boolean)}
     */
    @ApiStatus.Internal
    @Deprecated
    @Override
    public Sprite getSprite() {
        return super.getSprite();
    }
}
