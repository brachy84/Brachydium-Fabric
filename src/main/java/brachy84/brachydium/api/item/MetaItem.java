package brachy84.brachydium.api.item;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.ItemGroups;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MetaItem extends Item {

    private Identifier id;

    private MetaItem(Settings settings) {
        super(settings);
    }

    public MetaItem(String name) {
        super(new Settings().group(ItemGroups.GENERAL));
        this.id = new Identifier(Brachydium.MOD_ID, name);
    }

    public void register() {
        Registry.register(Registry.ITEM, id, this);
    }

    @Override
    public String getTranslationKey() {
        return Brachydium.MOD_ID + ".metaitem." + id.getPath() + ".name";
    }
}
