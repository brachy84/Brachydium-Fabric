package brachy84.brachydium.mixin;

import brachy84.brachydium.api.util.ITagGroupGetter;
import brachy84.brachydium.api.util.ITagHolder;
import net.minecraft.item.Item;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ServerTagManagerHolder.class)
public class TagManagerLoaderMixin {

    @Inject(method = "setTagManager", at = @At("TAIL"))
    private static void setTagManager(TagManager tagManager, CallbackInfo ci) {
        TagGroup<Item> group = ((ITagGroupGetter) ServerTagManagerHolder.getTagManager()).getTagGroup2(Registry.ITEM_KEY);
        for (Map.Entry<Identifier, Tag<Item>> entry : group.getTags().entrySet()) {
            for (Item item : entry.getValue().values()) {
                ((ITagHolder) item).addTag(entry.getKey());
            }
        }
    }
}
