package brachy84.brachydium.api;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.block.BlockMachine;
import brachy84.brachydium.api.block.BlockMachineItem;
import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import brachy84.brachydium.api.blockEntity.MetaBlockEntityHolder;
import brachy84.brachydium.api.fluid.MaterialFluid;
import brachy84.brachydium.api.fluid.MaterialFluidBlock;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.util.BrachydiumRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
        BlockRenderLayerMap.INSTANCE.putBlock(entity.getBlock(), RenderLayer.getTranslucent());
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

    public static MaterialFluid.Still registerFluid(String mod, Material material) {
        String fluidName = "molten_" + material.getName();
        MaterialFluid.Still still = Registry.register(Registry.FLUID, new Identifier(mod, fluidName), new MaterialFluid.Still(material));
        MaterialFluid.Flowing flowing = Registry.register(Registry.FLUID, new Identifier(mod, fluidName + "_flowing"), new MaterialFluid.Flowing(material));
        BucketItem item = registerItem(new Identifier(mod, fluidName + "_bucket"), new BucketItem(still, new Item.Settings().recipeRemainder(Items.BUCKET)));
        still.setData(still, flowing, item);
        flowing.setData(still, flowing, item);
        Block block = Registry.register(Registry.BLOCK, new Identifier(mod, fluidName), new MaterialFluidBlock(still, material));
        still.setBlock(block);
        flowing.setBlock(block);
        return still;
    }
}
