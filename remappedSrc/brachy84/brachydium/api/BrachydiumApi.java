package brachy84.brachydium.api;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.block.BlockMachine;
import brachy84.brachydium.api.block.BlockMachineItem;
import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import brachy84.brachydium.api.blockEntity.MetaBlockEntityHolder;
import brachy84.brachydium.api.handlers.WorldAccesApiHolder;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.util.BrachydiumControlledRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;

public class BrachydiumApi {

    public static final BrachydiumControlledRegistry<Identifier, MetaBlockEntity> META_BLOCK_ENTITY_REGISTRY = new BrachydiumControlledRegistry<>();

    public static <T extends MetaBlockEntity, W> T registerTileEntity(T entity) {
        if(entity == null || entity.getId() == null) {
            throw new NullPointerException("Can't register null BlockEntity or BlockEntity with null Identifier");
        }
        Brachydium.LOGGER.log(Level.INFO, "Registering MetaBlockEntity for " + entity.getId());
        Identifier newId = new Identifier(entity.getId().getNamespace(), "mbe/" + entity.getId().getPath());
        META_BLOCK_ENTITY_REGISTRY.put(newId, entity);
        entity.setBlock(registerBlock(newId, new BlockMachine(newId)));
        entity.setBlockItem(registerItem(newId, new BlockMachineItem(entity.getBlock(), newId)));
        entity.setBlockEntityType(Registry.register(Registry.BLOCK_ENTITY_TYPE, newId, BlockEntityType.Builder.create(() -> new MetaBlockEntityHolder(entity), entity.getBlock()).build(null)));
        entity.addApis();

        //entity.init();
        RecipeTable<?> recipeTable = entity.getRecipeTable();
        if(recipeTable != null) recipeTable.addTileItem(entity.getItem());
        RRPHelper.addGenericMbeBlockState(newId);
        return entity;
    }

    public static void registerBlockEntitiesClient() {
        /*META_BLOCK_ENTITY_REGISTRY.foreach((id, tile) -> {
            // if has screen -> register
            Brachydium.LOGGER.log(Level.INFO, "Registering client screen for " + id);
            if(tile.hasUi()) {
                ScreenRegistry.<ModularScreenHandler, ModularHandledScreen>register(tile.getScreenHandlerType(), (gui, inventory, title) -> new ModularHandledScreen(gui, inventory));
            }
        });*/
    }

    private static <T extends Block> T registerBlock(Identifier id, T block) {
        return Registry.register(Registry.BLOCK, id, block);
    }

    private static <T extends Item> T registerItem(Identifier id, T item) {
        return Registry.register(Registry.ITEM, id, item);
    }
}
