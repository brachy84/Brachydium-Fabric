package brachy84.brachydium.api.block;

import brachy84.brachydium.ItemGroups;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;

public class BlockMachineItem extends BlockItem {

    private final Identifier id;
    public BlockMachineItem(Block block, Identifier id) {
        super(block, new Settings().group(ItemGroups.GENERAL));
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }
}
