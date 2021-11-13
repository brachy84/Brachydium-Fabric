package brachy84.brachydium.mixin;

import brachy84.brachydium.api.util.ITagHolder;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = Item.class, remap = false)
public class ItemMixin implements ITagHolder {

    @Unique
    @Final
    private final List<Identifier> tags = new ArrayList<>();

    @Override
    public void addTag(Identifier id) {
        tags.add(id);
    }

    @Override
    public List<Identifier> getTags() {
        return tags;
    }
}
