package brachy84.brachydium.api.block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;

public class BlockMachineItem extends BlockItem {

    private final Identifier id;
    public BlockMachineItem(Block block, Identifier id) {
        super(block, new Settings());
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }
}
