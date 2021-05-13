package brachy84.brachydium.api;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.block.BlockMachine;
import brachy84.brachydium.api.block.BlockMachineItem;
import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import brachy84.brachydium.api.blockEntity.MetaBlockEntityHolder;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.util.BrachydiumRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;

public class BrachydiumApi {

    public static final BrachydiumRegistry<Identifier, MetaBlockEntity> META_BLOCK_ENTITY_REGISTRY = new BrachydiumRegistry<>();

    public static <T extends MetaBlockEntity> T registerTileEntity(T entity) {
        if(entity == null || entity.getId() == null) {
            throw new NullPointerException("Can't register null BlockEntity or BlockEntity with null Identifier");
        }
        Brachydium.LOGGER.log(Level.INFO, "Registering MetaBlockEntity for " + entity.getId());
        Identifier newId = new Identifier(entity.getId().getNamespace(), "mbe/" + entity.getId().getPath());
        META_BLOCK_ENTITY_REGISTRY.register(newId, entity);
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

    private static <T extends Block> T registerBlock(Identifier id, T block) {
        return Registry.register(Registry.BLOCK, id, block);
    }

    private static <T extends Item> T registerItem(Identifier id, T item) {
        return Registry.register(Registry.ITEM, id, item);
    }
}
