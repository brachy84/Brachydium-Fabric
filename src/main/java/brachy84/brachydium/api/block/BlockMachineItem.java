package brachy84.brachydium.api.block;

import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.api.blockEntity.BlockEntityGroup;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BlockMachineItem extends BlockItem {

    private final BlockEntityGroup<?> group;

    public BlockMachineItem(Block block, BlockEntityGroup<?> group) {
        super(block, new Settings().group(ItemGroups.GENERAL));
        this.group = group;
    }

    public BlockEntityGroup<?> getTileGroup() {
        return group;
    }

}
