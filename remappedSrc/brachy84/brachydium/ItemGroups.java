package brachy84.brachydium;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ItemGroups {

    public static final ItemGroup MATERIALS = FabricItemGroupBuilder.build(
            new Identifier(Brachydium.MOD_ID, "materials"), () -> new ItemStack(Items.DIRT)
    );
    public static final ItemGroup GENERAL = FabricItemGroupBuilder.build(
            new Identifier(Brachydium.MOD_ID, "general"), () -> new ItemStack(Items.DIRT)
    );
}
