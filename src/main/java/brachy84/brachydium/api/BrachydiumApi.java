package brachy84.brachydium.api;

import brachy84.brachydium.api.block.BlockEntityHolderBlock;
import brachy84.brachydium.api.block.BlockMachineItem;
import brachy84.brachydium.api.blockEntity.*;
import brachy84.brachydium.api.fluid.MaterialFluid;
import brachy84.brachydium.api.fluid.MaterialFluidBlock;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.util.BrachydiumRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

public class BrachydiumApi {

    public static final BrachydiumRegistry<Identifier, TileEntityGroup> BLOCK_ENTITY_GROUP_REGISTRY = new BrachydiumRegistry<>();

    public static <T extends TileEntityGroup> T registerTileEntityGroup(T group) {
        if(group == null || group.id == null) {
            throw new NullPointerException("Can't register null BlockEntity or BlockEntity with null Identifier");
        }
        Identifier id = group.id;
        Block block = registerBlock(id, new BlockEntityHolderBlock(group));
        BlockItem item = registerItem(id, new BlockMachineItem(block, group));
        group.setBlock(block);
        group.setItem(item);
        group.setType(Registry.register(Registry.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create((pos, state) -> new BlockEntityHolder(group, pos, state), block).build(null)));
        BLOCK_ENTITY_GROUP_REGISTRY.register(id, group);
        // register Apis
        Set<BlockApiLookup<Object, Object>> apis = new HashSet<>();
        for(TileEntity tile : group.getTileEntities()) {
            if(tile instanceof WorkableTileEntity) {
                RecipeTable<?> recipeTable = ((WorkableTileEntity) tile).getRecipeTable();
                if(recipeTable != null)
                    recipeTable.addTileItem(tile.asStack());
            }
            tile.registerApis();
            apis.addAll(tile.getLookups());
        }
        for(BlockApiLookup<Object, Object> api : apis) {
            api.registerForBlocks((world, pos, state, blockEntity, context) -> {
                if(blockEntity instanceof BlockEntityHolder) {
                    TileEntity tile = ((BlockEntityHolder) blockEntity).getActiveTileEntity();
                    if(tile != null) {
                        return tile.getApiProvider(api, world, pos, state, blockEntity, context);
                    }
                }
                return null;
            }, group.getBlock());
        }
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
        RRPHelper.addGenericMbeBlockState(id);
        return group;
    }

    private static <T extends Block> T registerBlock(Identifier id, T block) {
        return Registry.register(Registry.BLOCK, id, block);
    }

    private static <T extends Item> T registerItem(Identifier id, T item) {
        return Registry.register(Registry.ITEM, id, item);
    }

    public static MaterialFluid.Still registerFluid(String mod, String fluidPrefix, Material material) {
        String fluidName = String.format("%s_%s", fluidPrefix, material.toString());
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
