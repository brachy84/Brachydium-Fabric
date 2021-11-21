package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.block.SurfaceStoneBlock;
import brachy84.brachydium.api.item.BrachydiumItem;
import brachy84.brachydium.api.item.MaterialItem;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.material.MaterialRegistry;
import brachy84.brachydium.api.unification.material.Materials;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class SurfaceStoneBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    public static final SurfaceStoneBlock BLOCK = new SurfaceStoneBlock();
    public static final BlockEntityType<SurfaceStoneBlockEntity> TYPE = FabricBlockEntityTypeBuilder.create((SurfaceStoneBlockEntity::new), BLOCK).build();
    private Material material;

    public SurfaceStoneBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
        material = Materials.Neutronium;
    }

    public static void init() {
        Identifier id = Brachydium.id("surface_stone");
        Registry.register(Registry.BLOCK, id, BLOCK);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id, TYPE);
        ColorProviderRegistry.BLOCK.register((state, world1, pos1, tintIndex) -> {
            if (tintIndex != 1)
                return -1;
            BlockEntity be = world1.getBlockEntity(pos1);
            if (be instanceof SurfaceStoneBlockEntity surfaceStoneBlockEntity)
                return surfaceStoneBlockEntity.material.getMaterialRGB();
            return -1;
        }, BLOCK);
    }

    public ItemStack getDrop() {
        Item item = BrachydiumItem.get(MaterialItem.createItemId(material, TagDictionary.dustTiny).getPath());
        if (item != null) {
            return new ItemStack(item);
        }
        return ItemStack.EMPTY;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("Material", material.toString());
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Material"))
            material = MaterialRegistry.get(nbt.getString("Material"));
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }
}
