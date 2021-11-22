package brachy84.brachydium.api.block;

import brachy84.brachydium.ItemGroups;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.blockEntity.TileEntityGroup;
import brachy84.brachydium.api.energy.Voltage;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BlockMachineItem extends BlockItem {

    private final TileEntityGroup group;

    public BlockMachineItem(Block block, TileEntityGroup group) {
        super(block, new Settings().group(ItemGroups.GENERAL));
        this.group = group;
    }

    public TileEntityGroup getTileGroup() {
        return group;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        Optional<TileEntity> optTile = TileEntity.tryOfStack(stack);
        optTile.ifPresent(tile -> {
            if (context.isAdvanced()) {
                tooltip.add(new LiteralText("TileKey: " + tile.getGroupKey()).formatted(Formatting.DARK_GRAY));
            }
            tile.addTooltip(stack, world, tooltip, context.isAdvanced());
        });
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        Optional<TileEntity> tile = TileEntity.tryOfStack(stack);
        if(tile.isPresent())
            return tile.get().getNameKey();
        return super.getTranslationKey();
    }
}
