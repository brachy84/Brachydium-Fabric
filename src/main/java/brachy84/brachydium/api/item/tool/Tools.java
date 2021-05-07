package brachy84.brachydium.api.item.tool;

import net.minecraft.util.registry.Registry;

import static brachy84.brachydium.api.item.tool.CraftingTool.MAX_DURABILITY;

public class Tools {

    public static final CraftingTool WRENCH = new CraftingTool("wrench", MAX_DURABILITY);
    public static final CraftingTool SCREW_DRIVER = new CraftingTool("screw_driver", MAX_DURABILITY);
    public static final CraftingTool FILE = new CraftingTool("file", MAX_DURABILITY);
    public static final CraftingTool HAMMER = new CraftingTool("hammer", MAX_DURABILITY);
    public static final CraftingTool SAW = new CraftingTool("saw", MAX_DURABILITY);

    public static void register() {
        Registry.register(Registry.ITEM, WRENCH.getId(), WRENCH);
        Registry.register(Registry.ITEM, HAMMER.getId(), HAMMER);
        Registry.register(Registry.ITEM, SCREW_DRIVER.getId(), SCREW_DRIVER);
        Registry.register(Registry.ITEM, FILE.getId(), FILE);
        Registry.register(Registry.ITEM, SAW.getId(), SAW);
    }
}
