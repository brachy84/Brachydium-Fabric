package brachy84.brachydium.api.block;

import brachy84.brachydium.ItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.text.Text;

public class MaterialBlockItem extends BlockItem {

    public MaterialBlockItem(MaterialBlock block) {
        super(block, new FabricItemSettings().group(ItemGroups.MATERIALS));
    }

    @Override
    public Text getName() {
        return getBlock().getName();
    }
}
