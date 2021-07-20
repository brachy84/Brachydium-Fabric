package brachy84.brachydium.api.render;

import net.minecraft.client.texture.Sprite;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@FunctionalInterface
public interface IRenderable extends Supplier<Sprite> {

    @NotNull
    Sprite getSprite();

    @Override
    default Sprite get() {
        return getSprite();
    }
}
