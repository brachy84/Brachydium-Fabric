package brachy84.brachydium.mixin;

import brachy84.brachydium.api.util.ITagGroupGetter;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagManager.class)
public abstract class TagManagerMixin implements ITagGroupGetter {

    @Shadow
    @Nullable
    protected abstract <T> TagGroup<T> getTagGroup(RegistryKey<? extends Registry<T>> registryKey);

    @Override
    public <T> TagGroup<T> getTagGroup2(RegistryKey<? extends Registry<T>> registryKey) {
        return getTagGroup(registryKey);
    }
}
