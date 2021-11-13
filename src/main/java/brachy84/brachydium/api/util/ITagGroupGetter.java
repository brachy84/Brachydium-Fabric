package brachy84.brachydium.api.util;

import net.minecraft.tag.TagGroup;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface ITagGroupGetter {

    <T> TagGroup<T> getTagGroup2(RegistryKey<? extends Registry<T>> registryKey);
}
