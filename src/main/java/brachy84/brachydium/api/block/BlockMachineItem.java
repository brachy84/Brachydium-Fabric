package brachy84.brachydium.api.block;

import brachy84.brachydium.ItemGroups;
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

public class BlockMachineItem extends BlockItem {

    private final TileEntityGroup group;
    private final String translationKey;

    public BlockMachineItem(Block block, TileEntityGroup group) {
        super(block, new Settings().group(ItemGroups.GENERAL));
        this.group = group;
        this.translationKey = group.id.getNamespace() + ".tile." + group.tileName;
    }

    public TileEntityGroup getTileGroup() {
        return group;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (context.isAdvanced() && stack.hasNbt()) {
            Object o = group.readKey(stack.getNbt());
            tooltip.add(new LiteralText("TileKey: " + o.toString()).formatted(Formatting.DARK_GRAY));
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        if (stack.hasNbt()) {
            int tier = group.readKey(stack.getNbt());
            if (tier < Voltage.VALUES.length)
                return new TranslatableText(translationKey, Voltage.VALUES[tier].shortName.toUpperCase());
        }
        return super.getName(stack);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return translationKey;
    }
}
